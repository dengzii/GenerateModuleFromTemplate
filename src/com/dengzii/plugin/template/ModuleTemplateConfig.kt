package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.ModuleConfig
import com.dengzii.plugin.template.ui.ConfigurePanel
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/7
 * desc   :
 * </pre>
 */
@State(
        name = "ModuleTemplateConfig",
        storages = [Storage("ModuleTemplateConfig.xml")]
)
class ModuleTemplateConfig : PersistentStateComponent<List<ModuleConfig>> {

    override fun getState(): List<ModuleConfig>? {
        return null
    }

    override fun loadState(p0: List<ModuleConfig>) {

    }
}

class ModuleConfig:SearchableConfigurable{
    override fun isModified(): Boolean {
        return false
    }

    override fun getId(): String {
        return "this.is.id"
    }

    override fun getDisplayName(): String {
        return "display name"
    }

    override fun apply() {
        println("apply")
    }

    override fun createComponent(): JComponent? {
        return ConfigurePanel()
    }

}