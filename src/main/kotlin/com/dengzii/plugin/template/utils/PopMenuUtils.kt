package com.dengzii.plugin.template.utils

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/16
 * desc   :
 * </pre>
 */
object PopMenuUtils {

    fun create(menu: Map<String, com.dengzii.plugin.template.utils.PopMenuUtils.PopMenuListener?>): JBPopupMenu {

        val popMenu = JBPopupMenu()
        menu.forEach { (k, v) ->
            if (k.isBlank()) {
                popMenu.addSeparator()
                return@forEach
            }
            val item = JBMenuItem(k)
            item.addActionListener {
                v?.onClick()
            }
            popMenu.add(item)
        }
        return popMenu
    }

    interface PopMenuListener {
        fun onClick()
    }
}