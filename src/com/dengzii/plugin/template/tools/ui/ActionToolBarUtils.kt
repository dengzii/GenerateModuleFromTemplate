package com.dengzii.plugin.template.tools.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.ActionButtonLook
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.Icon

object ActionToolBarUtils {

    fun create(place: String, horizontal: Boolean = true, action: List<Action>): ActionToolbarImpl {
        val toolbarActionGroup = DefaultActionGroup(action)
        return object : ActionToolbarImpl(place, toolbarActionGroup, horizontal, false, true) {
            override fun createToolbarButton(action: AnAction, look: ActionButtonLook?, place: String,
                                             presentation: Presentation, minimumSize: Dimension): ActionButton {
                if (action is Action) {
                    presentation.icon = action.icon
                    presentation.isEnabled = action.isEnabled
                    presentation.description = action.desc
                }
                val bt = super.createToolbarButton(action, look, place, presentation, minimumSize)
                bt.setIconInsets(JBUI.insets(0))
                return bt
            }
        }
    }

    class Action(var icon: Icon,
                 var isEnabled: Boolean = true,
                 var desc: String = "",
                 var action: () -> Unit) : AnAction() {

        override fun actionPerformed(p0: AnActionEvent) {
            action.invoke()
        }
    }
}