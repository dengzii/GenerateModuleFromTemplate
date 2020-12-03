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
        fileTreeNode.build()

        val failedList = mutableListOf<FileTreeNode>()
        fileTreeNode.children.forEach {
            failedList.addAll(createFileTree(it, current))
        }
        if (failedList.isNotEmpty()) {
            val msg = failedList.joinToString { it.getRealName() + "\n" }
            NotificationUtils.showError(msg, "The following file creation failed.")
        }
    }

    private fun createFileTree(treeNode: FileTreeNode, currentDirectory: VirtualFile): List<FileTreeNode> {
        Logger.d(TAG, "create node $treeNode")
        val failedList = mutableListOf<FileTreeNode>()
        if (treeNode.isDir) {
            Logger.d(TAG, "create dir ${treeNode.getPath()}")
            val dir = kit.createDir(treeNode.getRealName(), currentDirectory)
            if (dir == null) {
                failedList.add(treeNode)
                Logger.e(TAG, "create directory failure: ${treeNode.getRealName()}")
            } else {
                treeNode.children.forEach {
                    failedList.addAll(createFileTree(it, dir))
                }
            }
        } else {
            val template = treeNode.getTemplateFile()
            if (template?.isNotBlank() == true) {
                val result = kit.createFileFromTemplate(
                        treeNode.getRealName(),
                        template,
                        treeNode.getPlaceholderInherit().orEmpty(),
                        currentDirectory)
                if (result == null || !result.isValid) {
                    failedList.add(treeNode)
                    Logger.e(TAG, "create file from template failed, file: ${treeNode.getRealName()} template:$template")
                } else {
                    Logger.d(TAG, "create file from template ${treeNode.getRealName()}")
                }
            } else {
                if (!kit.createFile(treeNode.getRealName(), currentDirectory)) {
                    failedList.add(treeNode)
                }
                Logger.d(TAG, "create file ${treeNode.getPath()}")
            }
        }
        return failedList
    }
}