package com.dengzii.plugin.auc

import com.dengzii.plugin.auc.template.AucTemplate
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
class FileWriteCommand(private var kit: PluginKit) : ThrowableRunnable<Exception> {

    companion object {
        private val TAG = FileWriteCommand::class.java.simpleName
        fun startAction(kit: PluginKit) {
            WriteCommandAction.writeCommandAction(kit.project)
                    .withGlobalUndo()
                    .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                    .run(FileWriteCommand(kit))
        }
    }

    override fun run() {

        val current = kit.getVirtualFile() ?: return
        if (!current.isDirectory) {
            Logger.i(TAG, "Current target is not directory.")
            return
        }
        AucTemplate.APP.children.forEach {
            createFileTree(it, current)
        }
    }

    private fun createFileTree(treeNode: FileTreeNode, current: VirtualFile?) {
        if (current == null) {
            Logger.e(TAG, "The parent of ${treeNode.getPath()} is null")
            return
        }
        Logger.i(TAG, "Create ${treeNode.getPath()}")
        if (treeNode.isDir) {
            kit.createDir(treeNode.name, current)
            treeNode.children.forEach {
                createFileTree(it, current.findChild(treeNode.name))
            }
        } else {
            kit.createFile(treeNode.name, current)
        }
    }
}