package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.ModuleConfig
import com.dengzii.plugin.template.template.AucTemplate
import com.dengzii.plugin.template.template.Placeholder
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
class FileWriteCommand(private var kit: PluginKit, private var moduleConfig: ModuleConfig) : ThrowableRunnable<Exception> {

    companion object {
        private val TAG = FileWriteCommand::class.java.simpleName
        fun startAction(kit: PluginKit, moduleConfig: ModuleConfig) {
            WriteCommandAction.writeCommandAction(kit.project)
                    .withGlobalUndo()
                    .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                    .run(FileWriteCommand(kit, moduleConfig))
        }
    }

    override fun run() {

        val current = kit.getVirtualFile() ?: return
        if (!current.isDirectory) {
            Logger.i(TAG, "Current target is not directory.")
            return
        }
        val app = moduleConfig.template
        if (app.placeHolderMap == null) {
            app.placeHolderMap = AucTemplate.DEFAULT_PLACEHOLDER.toMutableMap()
        }
        app.placeHolderMap?.set(Placeholder.PACKAGE_NAME, moduleConfig.packageName)
        app.placeHolderMap?.set(Placeholder.MODULE_NAME, moduleConfig.name)

        Logger.d(TAG, app.placeHolderMap.toString())
        app.children.forEach {
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