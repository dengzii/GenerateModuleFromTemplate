package com.dengzii.plugin.template.ui

import com.intellij.icons.AllIcons
import com.intellij.util.ui.JBEmptyBorder
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.Border

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
class EditToolbar : JPanel() {

    private val btAdd = JButton("Add")
    private val btRemove = JButton("Remove")
    private val btCopy = JButton("Copy")

    init {
        initLayout()
        initButton()
    }

    fun onAdd(listener: () -> Unit) {
        btAdd.addActionListener{
            listener()
        }
    }

    fun onRemove(listener: () -> Unit) {
        btRemove.addActionListener{
            listener()
        }
    }

    fun onCopy(listener: () -> Unit) {
        btCopy.addActionListener{
            listener()
        }
    }

    private fun initLayout() {

        val flowLayout = FlowLayout()
        flowLayout.vgap = 0
        flowLayout.hgap = 5
        flowLayout.alignment = FlowLayout.LEFT
        layout = flowLayout
    }

    private fun initButton() {
        setIconButton(btAdd, AllIcons.General.Add)
        setIconButton(btRemove, AllIcons.General.Remove)
        setIconButton(btCopy, AllIcons.General.CopyHovered)
    }

    private fun setIconButton(button: JButton, icon: Icon) {
        add(button)
        button.toolTipText = button.text
        button.icon = icon
        button.text = ""
        button.preferredSize = Dimension(25, 25)
    }

}