package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.utils.Logger
import com.dengzii.plugin.template.utils.PluginKit
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ThrowableRunnable

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/12/31
 * desc   :
 * </pre>
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
        fileTreeNode.children.forEach {
            createFileTree(it, current)
        }
    }

    private fun createFileTree(treeNode: FileTreeNode, currentDirectory: VirtualFile) {
        Logger.d(TAG, "create node $treeNode")
        if (treeNode.isDir) {
            val dir = kit.createDir(treeNode.getRealName(), currentDirectory)
            if (dir == null) {
                Logger.e(TAG, "create directory failure: ${treeNode.getRealName()}")
                return
            }
            treeNode.children.forEach {
                createFileTree(it, dir)
                Logger.d(TAG, "create dir ${it.getPath()}")
            }
        } else {
            val template = treeNode.getTemplateFile()
            if (template != null) {
                val result = kit.createFileFromTemplate(
                        treeNode.getRealName(),
                        template,
                        treeNode.getPlaceholderInherit().orEmpty(),
                        currentDirectory)
                if (result == null) {
                    Logger.e(TAG, "create file from template failed, file: ${treeNode.getRealName()} template:$template")
                } else {
                    Logger.d(TAG, "create file from template ${treeNode.getRealName()}")
                }
            } else {
                kit.createFile(treeNode.getRealName(), currentDirectory)
                Logger.d(TAG, "create file ${treeNode.getPath()}")
            }
        }
    }
}