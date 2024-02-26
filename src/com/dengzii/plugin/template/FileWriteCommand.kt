package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.tools.NotificationUtils
import com.dengzii.plugin.template.utils.Logger
import com.dengzii.plugin.template.utils.PluginKit
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ThrowableRunnable
import org.apache.velocity.VelocityContext
import org.apache.velocity.util.StringUtils

/**
 * Create file tree.
 *
 * @author https://github.com/dengzii
 */
class FileWriteCommand(private var kit: PluginKit, private var module: Module) : ThrowableRunnable<Exception> {

    companion object {
        private val TAG = FileWriteCommand::class.java.simpleName
        fun startAction(kit: PluginKit, module: Module) {
            WriteCommandAction.writeCommandAction(kit.project)
                .withGlobalUndo()
                .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                .run(FileWriteCommand(kit, module))
        }
    }

    override fun run() {
        val current = kit.getVirtualFile() ?: return
        if (!current.isDirectory) {
            Logger.i(TAG, "Current target is not directory.")
            return
        }
        val fileTreeNode = module.template
        Logger.d(TAG, "Placeholders : " + fileTreeNode.getPlaceholderInherit().toString())
        Logger.d(TAG, "FileTemplates : " + fileTreeNode.getFileTemplateInherit().toString())

        var context: VelocityContext? = null
        if (module.enableApacheVelocity) {
            context = VelocityContext().apply {
                put("StringUtils", StringUtils::class.java)
                fileTreeNode.getPlaceholderInherit()?.forEach { (k, v) ->
                    put(k, v)
                }
            }
        }
        fileTreeNode.context = context

        fileTreeNode.resolveTreeFileName()
        fileTreeNode.resolveFileTemplate()

        fileTreeNode.expandPath()
        fileTreeNode.expandPkgName(true)

        val failedList = createFileTree(context, fileTreeNode, current)
        if (failedList.isNotEmpty()) {
            val msg = failedList.joinToString("\n") { it.name }
            NotificationUtils.showError(msg, "The following files creation failed.")
        }
    }

    private fun createFileTree(
        context: VelocityContext?,
        treeNode: FileTreeNode,
        currentDirectory: VirtualFile
    ): List<FileTreeNode> {
        if (treeNode.isRoot()) {
            return treeNode.children.map {
                createFileTree(context, it, currentDirectory)
            }.flatten()
        }
        Logger.d(TAG, "create node $treeNode")
        val failedList = mutableListOf<FileTreeNode>()
        if (treeNode.isDir) {
            Logger.d(TAG, "create dir ${treeNode.getPath()}")
            val dir = kit.createDir(treeNode.name, currentDirectory)
            if (dir == null) {
                failedList.add(treeNode)
                Logger.e(TAG, "create directory failure: ${treeNode.name}")
            } else {
                treeNode.children.forEach {
                    failedList.addAll(createFileTree(context, it, dir))
                }
            }
        } else {
            val template = treeNode.getTemplateFile()
            if (template?.isNotBlank() == true) {
                val result = try {
                    kit.createFileFromTemplate(
                        treeNode.name,
                        template,
                        treeNode.getPlaceholderInherit().orEmpty(),
                        currentDirectory
                    )
                } catch (e: Throwable) {
                    e
                }
                if (result is Throwable) {
                    NotificationUtils.showError(
                        "file: ${treeNode.name} template:$template, error: ${result.message}",
                        "Create file failed."
                    )
                    failedList.add(treeNode)
                } else {
                    Logger.d(TAG, "create file from template ${treeNode.name}")
                }
            } else {
                if (!kit.createFile(treeNode.name, currentDirectory)) {
                    failedList.add(treeNode)
                }
                Logger.d(TAG, "create file ${treeNode.getPath()}")
            }
        }
        return failedList
    }
}