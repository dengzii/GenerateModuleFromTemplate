package com.dengzii.plugin.template.ui

import com.dengzii.plugin.template.Config
import com.dengzii.plugin.template.Config.GSON
import com.dengzii.plugin.template.CreateModuleAction.Companion.project
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.model.Module.Companion.getAndroidApplication
import com.dengzii.plugin.template.model.Module.Companion.getAndroidMvp
import com.dengzii.plugin.template.model.Module.Companion.getAucApp
import com.dengzii.plugin.template.model.Module.Companion.getAucExport
import com.dengzii.plugin.template.model.Module.Companion.getAucModule
import com.dengzii.plugin.template.model.Module.Companion.getAucPkg
import com.dengzii.plugin.template.model.Module.Companion.getEmpty
import com.dengzii.plugin.template.tools.ui.ActionToolBarUtils
import com.dengzii.plugin.template.tools.ui.PopMenuUtils
import com.intellij.icons.AllIcons
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
    private var modified = false

    init {
        panelActionBar.add(ActionToolBarUtils.create("ActionBar1", true, listOf(
                ActionToolBarUtils.Action(AllIcons.General.Add) {
//                    onAddConfig()
                },
                ActionToolBarUtils.Action(AllIcons.General.Remove) {
                    onRemoveConfig()
                },
                ActionToolBarUtils.Action(AllIcons.General.CopyHovered) {
                    onCopyConfig()
                },
                ActionToolBarUtils.Action(AllIcons.Actions.Download) {
                    onExportTemplate()
                }
        )))
        initComponent()
        loadConfig()
        initData()
    }

    fun isModified() = modified

    fun cacheConfig() {
        currentConfig ?: return
        currentConfig!!.template.run {
            removeAllPlaceHolderInTree()
            removeAllTemplateInTree()
            fileTemplates = tableFileTemp.getPairResult()
            placeholders = tablePlaceholder.getPairResult()
        }
    }

    fun saveConfig() {
        Config.saveModuleTemplates(configs!!)
        modified = false
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
        tableFileTemp.addChangeListener {
            modified = true
        }
        tableFileTemp.addChangeListener {
            modified = true
        }
        panelPreview.setOnTreeUpdateListener {
            modified = true
        }
        cbPlaceholder.addChangeListener {
            panelPreview.setReplacePlaceholder(cbPlaceholder.isSelected)
        }
        tabbedPane.addChangeListener {
            currentConfig ?: return@addChangeListener
            when (tabbedPane.selectedIndex) {
                0 -> {
                    currentConfig!!.template.placeholders = tablePlaceholder.getPairResult()
                    currentConfig!!.template.fileTemplates = tableFileTemp.getPairResult()
                }
                1 -> tableFileTemp.setPairData(currentConfig!!.template.getAllTemplateMap())
                2 -> tablePlaceholder.setPairData(currentConfig!!.template.getAllPlaceholdersMap().toMutableMap())
            }
            panelPreview.setModuleConfig(currentConfig!!)
        }
        listTemplate.selectionMode = ListSelectionModel.SINGLE_SELECTION
        listTemplate.addListSelectionListener {
            if (isNoConfigSelected()) return@addListSelectionListener
            onConfigListSelected()
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

    private fun addModuleTemplate(module: Module) {
        configs!!.forEach {
            if (it.templateName == module.templateName) {
                module.templateName = module.templateName + "_New"
            }
        }

        configs!!.add(module)
        templateListModel!!.addElement(module.templateName)
        listTemplate.doLayout()
        listTemplate.selectedIndex = configs!!.indexOf(module)
        onConfigListSelected()
    }

    private fun onRemoveConfig() {
        if (isNoConfigSelected()) {
            return
        }
        val selectedIndex = getSelectedConfigIndex()
        configs!!.removeAt(selectedIndex)
        templateListModel!!.remove(selectedIndex)
        if (!listTemplate.isEmpty) {
            panelPreview.isEnabled = true
            listTemplate.selectedIndex = 0
            onConfigListSelected()
        } else {
            currentConfig = null
            tablePlaceholder.setPairData(mapOf())
            tableFileTemp.setPairData(mapOf())
            panelPreview.setModuleConfig(getEmpty())
            panelPreview.isEnabled = false
        }
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
        configs = Config.loadModuleTemplates()
        templateListModel = DefaultListModel()
        configs!!.forEach(Consumer { module: Module -> templateListModel!!.addElement(module.templateName) })
        if (templateListModel!!.size() > 0) {
            listTemplate.selectedIndex = 0
        }
    }

    private fun onConfigListSelected() {
        val index = getSelectedConfigIndex()
        if (currentConfig == configs!![index]) {
            return
        }
        cacheConfig()
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
        PopMenuUtils.show(e, linkedMapOf(
                "Empty Template" to { addModuleTemplate(getEmpty()) },
                "* Import From File" to { onImportTemplate() },
                "Android Application" to { addModuleTemplate(getAndroidApplication()) },
                "Android Mvp" to { addModuleTemplate(getAndroidMvp()) },
                "Auc Module" to { addModuleTemplate(getAucModule()) },
                "Auc App" to { addModuleTemplate(getAucApp()) },
                "Auc Pkg" to { addModuleTemplate(getAucPkg()) },
                "Auc Export" to { addModuleTemplate(getAucExport()) }
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