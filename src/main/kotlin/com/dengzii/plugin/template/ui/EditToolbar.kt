package com.dengzii.plugin.template.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JPanel

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
class EditToolbar : JPanel() {

    private val btAdd = JButton("Add")
    private val btRemove = JButton("Remove")
    private val btCopy = JButton("Copy")
    private val btShare = JButton("Export")

    init {
        initLayout()
        initButton()
    }

    fun onAdd(listener: MouseAdapter) {
        btAdd.addMouseListener(listener)
    }

    fun onAdd(listener: (MouseEvent?) -> Unit) {
        btAdd.addMouseListener(object :MouseAdapter(){
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                listener(e)
            }
        })
    }

    fun onRemove(listener: () -> Unit) {
        btRemove.addActionListener {
            listener()
        }
    }

    fun onCopy(listener: () -> Unit) {
        btCopy.addActionListener {
            listener()
        }
    }

    fun onExport(listener: () -> Unit) {
        btShare.addActionListener {
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
        setIconButton(btShare, AllIcons.Actions.Download)
    }

    private fun setIconButton(button: JButton, icon: Icon) {
        add(button)
        button.toolTipText = button.text
        button.icon = icon
        button.text = ""
        button.preferredSize = Dimension(25, 25)
    }

}