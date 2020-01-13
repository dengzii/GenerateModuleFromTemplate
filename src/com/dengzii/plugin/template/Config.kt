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
                val module = GSON.fromJson(it, Module::class.java)
                setParent(module.template)
                result.add(module)
            } catch (e: Exception) {
                clear()
                Logger.e(Config::class.java.simpleName, e)
                return result
            }
        }
        return result
    }

    fun saveModuleTemplates(templates: List<Module>) {
        val t = mutableListOf<String>()
        templates.forEach {
            t.add(GSON.toJson(it))
            Logger.d(Config::class.java.simpleName, "saveModuleTemplates ${t[t.lastIndex]}")
        }
        PropertiesComponent.getInstance().setValues(KEY_TEMPLATES, t.toTypedArray())
    }

    private fun setParent(node: FileTreeNode) {
        node.children.forEach {
            it.parent = node
            if (it.isDir) {
                setParent(it)
            }
        }
    }
}