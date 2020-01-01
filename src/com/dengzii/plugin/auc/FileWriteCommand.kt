package com.dengzii.plugin.auc

import com.intellij.openapi.command.WriteCommandAction
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
class FileWriteCommand(private var kit: PluginKit) : ThrowableRunnable<RuntimeException> {

    companion object {
        fun startAction(kit: PluginKit) {
            WriteCommandAction.writeCommandAction(kit.project).run(FileWriteCommand(kit))
        }
    }

    override fun run() {

        val current = kit.getVirtualFile() ?: return
        if (!current.isDirectory) {
            return
        }
        AucTemplate.APP.forEach {
            if (it.isDir) {
                kit.createDir(it.getPath())
            } else {
                kit.createFile(it.getPath())
            }
        }
    }
}