package com.dengzii.plugin.template

import com.dengzii.plugin.template.ui.CreateModuleDialog
import com.dengzii.plugin.template.utils.Logger
import com.dengzii.plugin.template.utils.PluginKit
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/12/31
 * desc   :
</pre> *
 */
class CreateModuleAction : AnAction() {

    companion object {
        var project: Project? = null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val kit = PluginKit.get(e)
        if (!kit.isProjectValid()) {
            Logger.d(CreateModuleAction::class.java.simpleName, "Project is not valid.")
            return
        }
        project = kit.project
        CreateModuleDialog.createAndShow(kit.project) {
            FileWriteCommand.startAction(kit, it)
        }
    }

}