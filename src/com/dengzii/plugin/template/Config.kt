package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.template.AucTemplate
import com.dengzii.plugin.template.template.Template
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
    private const val KEY_INIT = "KEY_INIT"

    private val GSON = Gson()
    private val STORE by lazy { PropertiesComponent.getInstance() }

    private val DEFAULT_MODULE_TEMPLATES = listOf(
            Module.create(AucTemplate.MODULE, "feature", "com.example", "Auc Feature Module"),
            Module.create(AucTemplate.APP, "app", "com.example", "Auc App Module"),
            Module.create(AucTemplate.PKG, "pkg", "com.example", "Auc Pkg Module"),
            Module.create(AucTemplate.EXPORT, "export", "com.example", "Auc Export Module")
    )

    val MODULE_ANDROID_APPLICATION =
            Module.create(Template.ANDROID_APP, "app", "com.example", "Android Application")

    fun clear() = STORE.unsetValue(KEY_TEMPLATES)

    fun loadModuleTemplates(): MutableList<Module> {
        val result = mutableListOf<Module>()
        val arr = STORE.getValues(KEY_TEMPLATES)

        if (STORE.getBoolean(KEY_INIT, true)) {
            result.addAll(DEFAULT_MODULE_TEMPLATES)
        }
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
        STORE.setValues(KEY_TEMPLATES, t.toTypedArray())
        STORE.setValue(KEY_INIT, false)
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