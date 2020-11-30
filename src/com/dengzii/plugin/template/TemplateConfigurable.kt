package com.dengzii.plugin.template

import com.dengzii.plugin.template.ui.ConfigurePanel
import com.dengzii.plugin.template.ui.RealConfigurePanel
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/16
 * desc   :
 * </pre>
 */
class TemplateConfigurable() : SearchableConfigurable {

    private lateinit var panelConfig: RealConfigurePanel
    private var onApplyListener: OnApplyListener? = null

    constructor(onApplyListener: OnApplyListener) : this() {
        this.onApplyListener = onApplyListener
    }

    override fun apply() {
        panelConfig.cacheConfig()
        panelConfig.saveConfig()
        onApplyListener?.onApply()
    }

    override fun getId(): String {
        return "preferences.ModuleTemplateConfig"
    }

    override fun createComponent(): JComponent? {
        panelConfig = RealConfigurePanel()
        return panelConfig
    }

    override fun isModified(): Boolean {
        return true
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String? {
        return "Module Template Settings"
    }

    interface OnApplyListener {
        fun onApply()
    }
}