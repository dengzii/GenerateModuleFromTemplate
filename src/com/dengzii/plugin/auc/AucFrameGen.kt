package com.dengzii.plugin.auc

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/12/31
 * desc   :
</pre> *
 */
class AucFrameGen : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val kit = PluginKit.get(e)
        if (!kit.isProjectValid()) {
            return
        }
        WriteCommandAction.writeCommandAction(kit.project).run(FileWriteAction(kit))
    }

}