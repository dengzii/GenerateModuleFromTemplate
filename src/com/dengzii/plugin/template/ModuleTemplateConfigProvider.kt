package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.utils.Logger
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

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
        storages = [Storage("ModuleTemplateConfig3.xml")]
)
class ModuleTemplateConfigProvider : PersistentStateComponent<ModuleTemplateConfigProvider> {

    var moduleConfig = Config.loadModuleTemplates()

    companion object {
        fun getService(): ModuleTemplateConfigProvider {
            return ServiceManager.getService(ModuleTemplateConfigProvider::class.java)
        }
    }

    override fun getState(): ModuleTemplateConfigProvider? {
        Logger.i(ModuleTemplateConfigProvider::class.java.simpleName, "getState")
        return this
    }

    override fun loadState(p0: ModuleTemplateConfigProvider) {
        Logger.i(ModuleTemplateConfigProvider::class.java.simpleName, "loadState")
        XmlSerializerUtil.copyBean(p0, this)
    }
}