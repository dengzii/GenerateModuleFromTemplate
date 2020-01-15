package com.dengzii.plugin.template.ui

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
class EditableTable(private val header: Array<String>) : JPanel() {

    private val scrollPanel = JBScrollPane()
    private val editToolbar = EditToolbar()
    private val table = JBTable()
    private var tableModel = TableModel(header)

    init {
        layout = BorderLayout()

        add(editToolbar, BorderLayout.NORTH)
        scrollPanel.setViewportView(table)
        add(scrollPanel, BorderLayout.CENTER)
        table.fillsViewportHeight = true
        table.showHorizontalLines = true
        table.showVerticalLines = true
        table.model = tableModel
        initListener()
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

    fun getPairResult(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        for (i in 0 until table.rowCount) {
            val key = table.getValueAt(i, 0).toString()
            val value = table.getValueAt(i, 1).toString()
            if (key.isBlank() || value.isBlank()) {
                continue
            }
            result[key] = value
        }
        return result
    }

    private fun initListener() {

        editToolbar.onAdd {
            tableModel.add()
            table.updateUI()
        }
        editToolbar.onCopy {
            if (table.selectedRow == -1) {
                return@onCopy
            }
            tableModel.copy(table.selectedRow)
            table.updateUI()
        }
        editToolbar.onRemove {
            if (table.selectedRow == -1) {
                return@onRemove
            }
            tableModel.remove(table.selectedRow)
            table.updateUI()
        }
    }

    internal class TableModel(private val header: Array<String>) : DefaultTableModel() {

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