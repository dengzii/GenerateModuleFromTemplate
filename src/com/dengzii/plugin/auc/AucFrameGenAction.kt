package com.dengzii.plugin.auc

import com.dengzii.plugin.auc.ui.CreateModuleDialog
import com.dengzii.plugin.auc.utils.Logger
import com.dengzii.plugin.auc.utils.PluginKit
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/12/31
 * desc   :
</pre> *
 */
class AucFrameGenAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val kit = PluginKit.get(e)
        if (!kit.isProjectValid()) {
            Logger.d(AucFrameGenAction::class.java.simpleName, "Project is not valid.")
            return
        }
        CreateModuleDialog.createAndShow {
            FileWriteCommand.startAction(kit, it)
        }
    }

}