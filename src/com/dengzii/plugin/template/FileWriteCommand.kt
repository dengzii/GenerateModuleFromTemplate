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
        fileTreeNode.expandPath()
        fileTreeNode.expandPkgName(true)
        fileTreeNode.resolveFileTemplate()

        var context: VelocityContext? = null
        if (module.enableApacheVelocity) {
            context = VelocityContext().apply {
                put("StringUtils", StringUtils::class.java)
                fileTreeNode.getPlaceholderInherit()?.forEach { (k, v) ->
                    put(k, v)
                }
            }
        }

        val failedList = mutableListOf<FileTreeNode>()
        fileTreeNode.children.forEach {
            failedList.addAll(createFileTree(context, it, current))
        }
        if (failedList.isNotEmpty()) {
            val msg = failedList.joinToString("\n") { it.getRealName() }
            NotificationUtils.showError(msg, "The following files creation failed.")
        }
    }

    private fun createFileTree(
        context: VelocityContext?,
        treeNode: FileTreeNode,
        currentDirectory: VirtualFile
    ): List<FileTreeNode> {
        Logger.d(TAG, "create node $treeNode")
        val failedList = mutableListOf<FileTreeNode>()
        if (treeNode.isDir) {
            Logger.d(TAG, "create dir ${treeNode.getPath()}")
            val dir = kit.createDir(treeNode.getRealName(context), currentDirectory)
            if (dir == null) {
                failedList.add(treeNode)
                Logger.e(TAG, "create directory failure: ${treeNode.getRealName(context)}")
            } else {
                treeNode.children.forEach {
                    failedList.addAll(createFileTree(context, it, dir))
                }
            }
        } else {
            val template = treeNode.getTemplateFile()
            if (template?.isNotBlank() == true) {
                val result = kit.createFileFromTemplate(
                    treeNode.getRealName(context),
                    template,
                    treeNode.getPlaceholderInherit().orEmpty(),
                    currentDirectory
                )
                if (result == null || !result.isValid) {
                    failedList.add(treeNode)
                    Logger.e(
                        TAG,
                        "create file from template failed, file: ${treeNode.getRealName(context)} template:$template"
                    )
                } else {
                    Logger.d(TAG, "create file from template ${treeNode.getRealName(context)}")
                }
            } else {
                if (!kit.createFile(treeNode.getRealName(context), currentDirectory)) {
                    failedList.add(treeNode)
                }
                Logger.d(TAG, "create file ${treeNode.getPath()}")
            }
        }
        return failedList
    }
}