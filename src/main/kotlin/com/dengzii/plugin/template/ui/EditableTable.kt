package com.dengzii.plugin.template.ui

import com.dengzii.plugin.template.tools.ui.ActionToolBarUtils
import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
class EditableTable(header: Array<String>, colEditable: Array<Boolean> = emptyArray()) : JPanel() {

    private val scrollPanel = JBScrollPane()
    private val editToolbar: JComponent
    private val table = JBTable()
    private var tableModel = TableModel(header, colEditable)

    init {
        layout = BorderLayout()
        editToolbar = ActionToolBarUtils.create("Edit1", listOf(
            ActionToolBarUtils.Action(AllIcons.General.Add) {
                tableModel.add()
                table.updateUI()
            },
            ActionToolBarUtils.Action(AllIcons.General.Remove) {
                if (table.selectedRow == -1) {
                    return@Action
                }
                tableModel.remove(table.selectedRow)
                table.updateUI()
            },
            ActionToolBarUtils.Action(AllIcons.General.CopyHovered) {
                if (table.selectedRow == -1) {
                    return@Action
                }
                tableModel.copy(table.selectedRow)
                table.updateUI()
            }
        ))
        add(editToolbar, BorderLayout.NORTH)
        scrollPanel.setViewportView(table)
        add(scrollPanel, BorderLayout.CENTER)
        table.fillsViewportHeight = true
        table.showHorizontalLines = true
        table.showVerticalLines = true
        table.model = tableModel
        table.putClientProperty("terminateEditOnFocusLost", true)
    }

    fun addChangeListener(listener: (TableModelEvent) -> Unit) {
        tableModel.addTableModelListener {
            listener.invoke(it)
        }
    }

    fun setToolBarVisible(visible: Boolean) {
        editToolbar.isVisible = visible
    }

    fun setData(data: MutableList<MutableList<String>>) {
        tableModel.setData(data)
        tableModel.fireTableDataChanged()
        table.updateUI()
    }

    fun setPairData(data: Map<String, String>?) {
        val dataList = mutableListOf<MutableList<String>>()
        data?.forEach { (t, u) ->
            dataList.add(mutableListOf(t, u))
        }
        tableModel.setData(dataList)
        tableModel.fireTableDataChanged()
        table.updateUI()
    }

    fun stopEdit() {
        if (table.isEditing) {
            table.cellEditor.stopCellEditing()
        }
    }

    fun getPairResult(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        for (i in 0 until table.rowCount) {
            val key = table.getValueAt(i, 0).toString()
            val value = table.getValueAt(i, 1).toString()
            if (key.isBlank()) {
                continue
            }
            result[key] = value
        }
        return result
    }

    internal class TableModel(private val header: Array<String>, private val colEditable: Array<Boolean>) :
        DefaultTableModel() {

        fun setData(fileTemp: MutableList<MutableList<String>>?) {
            while (rowCount > 0) {
                removeRow(0)
            }
            if (fileTemp == null) {
                return
            }
            fileTemp.forEach {
                addRow(it.toTypedArray())
            }
        }

        fun add() {
            addRow(Array(header.size) { "" })
            fireTableDataChanged()
        }

        fun remove(row: Int) {
            removeRow(row)
            fireTableDataChanged()
        }

        fun copy(row: Int) {
            val r = mutableListOf<String>()
            for (c in 0 until columnCount) {
                r.add(getValueAt(row, c).toString())
            }
            addRow(r.toTypedArray())
            fireTableDataChanged()
        }

        override fun isCellEditable(row: Int, column: Int): Boolean {
            return if (colEditable.isEmpty()) super.isCellEditable(row, column) else colEditable[column]
        }

        override fun getColumnClass(columnIndex: Int): Class<*> {
            return String::class.java
        }

        override fun getColumnCount(): Int {
            return header.size
        }

        override fun getColumnName(column: Int): String {
            return if (header.isEmpty()) super.getColumnName(column) else header[column]
        }
    }
}