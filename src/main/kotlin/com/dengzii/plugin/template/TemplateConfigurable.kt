package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.ui.RealConfigurePanel
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/16
 * desc   :
 * </pre>
 */
class TemplateConfigurable() : SearchableConfigurable {

    private lateinit var panelConfig: RealConfigurePanel
    private var onApplyListener: OnApplyListener? = null

    private var add: FileTreeNode? = null

    constructor(onApplyListener: OnApplyListener?, addTemplate: FileTreeNode? = null) : this() {
        this.onApplyListener = onApplyListener
        this.add = addTemplate
    }

    override fun apply() {
        panelConfig.cacheConfig()
        panelConfig.saveConfig()
        onApplyListener?.onApply()
    }

    override fun getId(): String {
        return "preferences.ModuleTemplateConfig"
    }

    override fun createComponent(): JComponent {
        panelConfig = RealConfigurePanel()
        if (add != null) {
            panelConfig.createTempFromDir(add!!)
        }
        return panelConfig
    }

    override fun isModified(): Boolean {
        return panelConfig.isModified()
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Module Template Settings"
    }

    interface OnApplyListener {
        fun onApply()
    }
}