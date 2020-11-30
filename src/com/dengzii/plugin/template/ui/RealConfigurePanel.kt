package com.dengzii.plugin.template.ui

import com.dengzii.plugin.template.Config.GSON
import com.dengzii.plugin.template.Config.loadModuleTemplates
import com.dengzii.plugin.template.Config.saveModuleTemplates
import com.dengzii.plugin.template.CreateModuleAction.Companion.project
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.model.Module.Companion.getAndroidApplication
import com.dengzii.plugin.template.model.Module.Companion.getAndroidMvp
import com.dengzii.plugin.template.model.Module.Companion.getAucApp
import com.dengzii.plugin.template.model.Module.Companion.getAucExport
import com.dengzii.plugin.template.model.Module.Companion.getAucModule
import com.dengzii.plugin.template.model.Module.Companion.getAucPkg
import com.dengzii.plugin.template.model.Module.Companion.getEmpty
import com.dengzii.plugin.template.tools.ui.PopMenuUtils
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.ui.DocumentAdapter
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import java.io.*
import java.util.*
import java.util.function.Consumer
import javax.swing.DefaultListModel
import javax.swing.ListSelectionModel
import javax.swing.event.DocumentEvent

class RealConfigurePanel : ConfigurePanel() {


    private var configs: MutableList<Module>? = null
    private var templateListModel: DefaultListModel<String>? = null

    private var currentConfig: Module? = null

    private lateinit var panelPreview: PreviewPanel

    private lateinit var tablePlaceholder: EditableTable
    private lateinit var tableFileTemp: EditableTable

    init {
        initComponent()
        loadConfig()
        initData()
    }

    fun cacheConfig() {
        if (currentConfig == null) return
        currentConfig!!.template.fileTemplates = tableFileTemp.getPairResult()
        currentConfig!!.template.placeholders = tablePlaceholder.getPairResult()
    }

    fun saveConfig() {
        saveModuleTemplates(configs!!)
    }

    private fun initComponent() {
        layout = BorderLayout()
        add(contentPane)
        panelPreview = PreviewPanel()
        tablePlaceholder = EditableTable(arrayOf("Placeholder", "Default Value"), arrayOf(true, true))
        tableFileTemp = EditableTable(arrayOf("FileName", "Template"), arrayOf(true, true))
        panelStructure.add(panelPreview)
        panelPlaceholder.add(tablePlaceholder, BorderLayout.CENTER)
        panelFileTemp.add(tableFileTemp, BorderLayout.CENTER)
    }


    private fun initData() {
        actionbar.onAdd { e ->
            if (e != null) {
                onAddConfig(e)
            }
        }
        actionbar.onRemove(this::onRemoveConfig)
        actionbar.onCopy(this::onCopyConfig)
        actionbar.onExport(this::onExportTemplate)
        cbPlaceholder.addChangeListener {
            panelPreview.setReplacePlaceholder(cbPlaceholder.isSelected)
        }
        tabbedPane.addChangeListener {
            onChangeTab()
        }
        listTemplate.selectionMode = ListSelectionModel.SINGLE_SELECTION
        listTemplate.addListSelectionListener {
            if (isNoConfigSelected()) return@addListSelectionListener
            setCurrentConfigIndex(getSelectedConfigIndex())
        }
        listTemplate.setModel(templateListModel)
        tfName.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(documentEvent: DocumentEvent) {
                if (currentConfig != null) currentConfig!!.templateName = tfName.text
            }
        })
        if (templateListModel!!.size() > 1) {
            listTemplate.selectedIndex = 0
        }
    }

    private fun onChangeTab() {
        if (0 == tabbedPane.selectedIndex) {
            currentConfig!!.template.placeholders = tablePlaceholder.getPairResult()
        }
        if (2 == tabbedPane.selectedIndex) {
            val mergedPlaceholder = currentConfig!!.template.getAllPlaceholdersMap().toMutableMap()
            val allPlaceholders = currentConfig!!.template.getAllPlaceholderInTree()
            allPlaceholders.forEach(Consumer { s: String ->
                if (!mergedPlaceholder.containsKey(s)) {
                    mergedPlaceholder[s] = ""
                }
            })
            tablePlaceholder.setPairData(mergedPlaceholder)
        }
        panelPreview.setModuleConfig(currentConfig!!)
    }

    private fun addModuleTemplate(module: Module) {
        configs!!.add(module)
        currentConfig = module
        templateListModel!!.addElement(module.templateName)
        listTemplate.doLayout()
        listTemplate.selectedIndex = configs!!.indexOf(module)
    }

    private fun onRemoveConfig() {
        if (isNoConfigSelected()) {
            return
        }
        val selectedIndex = getSelectedConfigIndex()
        configs!!.removeAt(selectedIndex)
        templateListModel!!.remove(selectedIndex)
        listTemplate.doLayout()
    }

    private fun onCopyConfig() {
        if (isNoConfigSelected()) {
            return
        }
        val newConfig = currentConfig!!.clone()
        configs!!.add(newConfig)
        templateListModel!!.addElement(newConfig.templateName)
        listTemplate.doLayout()
        listTemplate.selectedIndex = configs!!.indexOf(newConfig)
    }

    private fun loadConfig() {
        configs = loadModuleTemplates()
        templateListModel = DefaultListModel()
        configs!!.forEach(Consumer { module: Module -> templateListModel!!.addElement(module.templateName) })
        if (templateListModel!!.size() > 0) {
            listTemplate.selectedIndex = 0
        }
    }

    private fun setCurrentConfigIndex(index: Int) {
        if (currentConfig == configs!![index]) {
            return
        }
        if (currentConfig != null) {
            cacheConfig()
        }
        currentConfig = configs!![index]
        tfName.text = currentConfig!!.templateName

        // update tree, file template and placeholder table
        panelPreview.setModuleConfig(currentConfig!!)
        tableFileTemp.setPairData(currentConfig!!.template.fileTemplates)
        tablePlaceholder.setPairData(currentConfig!!.template.placeholders)
    }

    private fun getSelectedConfigIndex() = listTemplate.selectedIndex

    private fun isNoConfigSelected() = getSelectedConfigIndex() == -1

    private fun onAddConfig(e: MouseEvent) {
        PopMenuUtils.show(e, mapOf(
                "Empty Template" to { addModuleTemplate(getEmpty()) },
                "Android Application" to { addModuleTemplate(getAndroidApplication()) },
                "Auc Module" to { addModuleTemplate(getAucModule()) },
                "Auc app" to { addModuleTemplate(getAucApp()) },
                "Auc Pkg" to { addModuleTemplate(getAucPkg()) },
                "Auc Export" to { addModuleTemplate(getAucExport()) },
                "Android Mvp" to { addModuleTemplate(getAndroidMvp()) },
                "=> Import From File" to { onImportTemplate() }
        ))
    }

    private fun onExportTemplate() {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = "Save Template to File"
        val vf = FileChooser.chooseFile(descriptor, project, null)
        if (vf != null && vf.isWritable) {
            val config = GSON.toJson(currentConfig)
            val file = File(vf.path, currentConfig!!.templateName + ".json")
            var outputStream: OutputStreamWriter? = null
            try {
                outputStream = OutputStreamWriter(FileOutputStream(file))
                outputStream.write(config)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    outputStream?.flush()
                    outputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun onImportTemplate() {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json")
        descriptor.title = "Import Template From File"
        val vf = FileChooser.chooseFile(descriptor, project, null)
        if (vf != null && vf.exists()) {
            val file = File(vf.path)
            var inputStream: BufferedInputStream? = null
            try {
                inputStream = BufferedInputStream(FileInputStream(file))
                val bytes = ByteArray(1024)
                var len = 0
                val stringBuilder = StringBuilder()
                while (inputStream.read(bytes).also { len = it } > 0) {
                    stringBuilder.append(String(bytes, 0, len))
                }
                val template = GSON.fromJson(stringBuilder.toString(), Module::class.java)
                template.initTemplate(template.template)
                addModuleTemplate(template)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}