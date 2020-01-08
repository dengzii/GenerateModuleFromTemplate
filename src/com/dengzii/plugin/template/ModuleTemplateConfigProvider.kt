package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.utils.Logger
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/7
 * desc   :
 * </pre>
 */
@State(
        name = "ModuleTemplateConfig",
        storages = [Storage("ModuleTemplateConfig.xml")]
)
class ModuleTemplateConfigProvider : PersistentStateComponent<MutableList<Module>> {

    var moduleConfig = mutableListOf<Module>()

    companion object {
        fun getService(): ModuleTemplateConfigProvider {
            return ServiceManager.getService(ModuleTemplateConfigProvider::class.java)
        }
    }

    override fun getState(): MutableList<Module>? {
        Logger.i(ModuleTemplateConfigProvider::class.java.simpleName, "getState")
        return moduleConfig.toMutableList()
    }

    override fun loadState(p0: MutableList<Module>) {
        Logger.i(ModuleTemplateConfigProvider::class.java.simpleName, "loadState")
        moduleConfig = p0
    }
}