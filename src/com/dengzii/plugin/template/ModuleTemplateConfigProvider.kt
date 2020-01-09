package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
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

@State(
        name = "ConfigProvider",
        storages = [Storage("config1.xml")]
)
class ConfigProvider : PersistentStateComponent<ConfigProvider> {

    private var serialModuleTemplates = mutableListOf<SerialModule>()

    companion object {
        fun getService(): ConfigProvider {
            return ServiceManager.getService(ConfigProvider::class.java)
        }
    }

    fun getModuleTemplate(): MutableList<Module> {
        val result = mutableListOf<Module>()
        serialModuleTemplates.forEach {
            result.add(deserializeModuleTemplate(it))
        }
        return result
    }

    fun setModuleTemplate(module: List<Module>) {
        serialModuleTemplates.clear()
        module.forEach {
            serialModuleTemplates.add(serializeModuleTemplate(it))
        }
    }

    fun addModuleTemplate(module: Module) {
        serialModuleTemplates.add(serializeModuleTemplate(module))
    }

    fun removeModuleTemplate(index: Int) {
        serialModuleTemplates.removeAt(index)
    }

    private fun serializeModuleTemplate(module: Module): SerialModule {
        val serialTreeNode = serializeTreeNode(module.template)
        return SerialModule(serialTreeNode, module.name, module.packageName, module.language, module.templateName)
    }

    private fun deserializeModuleTemplate(serialModule: SerialModule): Module {
        val fileTreeNode = deserializeTreeNode(serialModule.template)
        return Module(fileTreeNode, serialModule.name, serialModule.packageName,
                serialModule.language, serialModule.templateName)
    }

    private fun deserializeTreeNode(treeNode: SerialTreeNode): FileTreeNode {
        val fileTreeNode = FileTreeNode {
            name = treeNode.name
            isDir = treeNode.isDir
            template = treeNode.template
            fileTemplates = treeNode.fileTemplate
            placeHolderMap = treeNode.placeholders
        }
        if (treeNode.isDir) {
            treeNode.children?.forEach {
                val node = deserializeTreeNode(it)
                node.parent = fileTreeNode
                fileTreeNode.children.add(node)
            }
        }
        return fileTreeNode
    }

    private fun serializeTreeNode(treeNode: FileTreeNode): SerialTreeNode {
        val serialTreeNode = SerialTreeNode(
                treeNode.getRealName(),
                treeNode.isDir,
                null,
                treeNode.template,
                treeNode.placeHolderMap,
                treeNode.fileTemplates
        )
        if (treeNode.isDir && treeNode.children.isNotEmpty()) {
            serialTreeNode.children = mutableListOf()
            treeNode.children.forEach {
                serialTreeNode.children?.add(serializeTreeNode(it))
            }
        }
        return serialTreeNode
    }

    override fun getState(): ConfigProvider? {
        Logger.i(ConfigProvider::class.java.simpleName, "getState")
        return this
    }

    override fun loadState(p0: ConfigProvider) {
        Logger.i(ConfigProvider::class.java.simpleName, "loadState")
        XmlSerializerUtil.copyBean(p0, this)
    }
}

class SerialModule(
        var template: SerialTreeNode,
        var name: String,
        var packageName: String,
        var language: String,
        var templateName: String
)

class SerialTreeNode(
        var name: String,
        var isDir: Boolean,
        var children: MutableList<SerialTreeNode>?,
        var template: String?,
        var placeholders: MutableMap<String, String>?,
        var fileTemplate: MutableMap<String, String>?
)

