package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.template.AucTemplate
import com.dengzii.plugin.template.utils.Logger
import com.google.gson.Gson
import com.intellij.ide.util.PropertiesComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/3
 * desc   :
 * </pre>
 */

object Config {

    private const val KEY_TEMPLATES = "KEY_TEMPLATES"

    private val GSON = Gson()
    val DEFAULT_MODULE_TEMPLATE = listOf(
            Module.create(AucTemplate.MODULE, "feature", "com.example.feature", "Java", "Auc Feature Module"),
            Module.create(AucTemplate.APP, "app", "com.example.feature", "Java", "Auc App Module"),
            Module.create(AucTemplate.PKG, "pkg", "com.example.feature", "Java", "Auc Pkg Module"),
            Module.create(AucTemplate.EXPORT, "export", "com.example.feature", "Java", "Auc Export Module")
    )

    fun clear() {
        PropertiesComponent.getInstance().unsetValue(KEY_TEMPLATES)
    }

    fun loadModuleTemplates(): MutableList<Module> {
        val result = mutableListOf<Module>()
        val arr = PropertiesComponent.getInstance().getValues(KEY_TEMPLATES)

        if (arr.isNullOrEmpty()) {
            return result
        }
        Logger.d(Config::class.java.simpleName, "loadModuleTemplates")
        arr.forEach {
            try {
                result.add(Serializer.deserializeModuleTemplate(it))
            } catch (e: Exception) {
                clear()
                e.printStackTrace()
                return result
            }
        }
        return result
    }

    fun saveModuleTemplates(templates: List<Module>) {
        val t = mutableListOf<String>()
        templates.forEach {
            t.add(Serializer.serializeModuleTemplate(it))
            Logger.d(Config::class.java.simpleName, "saveModuleTemplates ${t[t.lastIndex]}")
        }
        PropertiesComponent.getInstance().setValues(KEY_TEMPLATES, t.toTypedArray())
    }


    private object Serializer {

        fun serializeModuleTemplate(module: Module): String {
            val serialTreeNode = serializeTreeNode(module.template)
            val result = SerialModule(serialTreeNode, module.name, module.packageName, module.language, module.templateName)
            return GSON.toJson(result)
        }

        fun deserializeModuleTemplate(json: String): Module {
            val serialModule = GSON.fromJson(json, SerialModule::class.java)
            val fileTreeNode = deserializeTreeNode(serialModule.template)
            return Module.create(fileTreeNode, serialModule.name, serialModule.packageName,
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

        private class SerialModule(
                var template: SerialTreeNode,
                var name: String,
                var packageName: String,
                var language: String,
                var templateName: String
        )

        private class SerialTreeNode(
                var name: String,
                var isDir: Boolean,
                var children: MutableList<SerialTreeNode>?,
                var template: String?,
                var placeholders: MutableMap<String, String>?,
                var fileTemplate: MutableMap<String, String>?
        )
    }
}