package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.utils.Logger
import com.google.gson.GsonBuilder
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
    private val TAG = Config::class.java.simpleName

    private const val KEY_TEMPLATES = "KEY_TEMPLATES"
    private const val KEY_INIT = "KEY_INIT"

    val GSON by lazy { GsonBuilder().setLenient().create() }
    private val STORE by lazy { PropertiesComponent.getInstance() }

    private fun clear() = STORE.unsetValue(KEY_TEMPLATES)

    fun loadModuleTemplates(): MutableList<Module> {
        val result = mutableListOf<Module>()
        val arr = STORE.getValues(KEY_TEMPLATES)

        if (STORE.getBoolean(KEY_INIT)) {
            Logger.i(TAG, "INIT... load AucFrame template")
            result.add(Module.getAucModule())
            result.add(Module.getAucApp())
            result.add(Module.getAucExport())
            result.add(Module.getAucPkg())
        }
        if (arr.isNullOrEmpty()) {
            return result
        }
        Logger.i(TAG, "loadModuleTemplates")
        arr.filter {
            !it.isNullOrBlank()
        }.forEach {
            try {
                val module = GSON.fromJson(it, Module::class.java)
                module.initTemplate()
                result.add(module)
            } catch (e: Exception) {
                clear()
                Logger.e(TAG, e)
                return result
            }
        }

        return result
    }

    fun saveModuleTemplates(templates: List<Module>) {
        if (templates.isEmpty()) {
            clear()
            return
        }
        val t = mutableListOf<String>()
        templates.forEach {
            t.add(GSON.toJson(it))
            Logger.d(TAG, "saveModuleTemplates ${t[t.lastIndex]}")
        }
        STORE.setValue(KEY_INIT, false, false)
        STORE.setValues(KEY_TEMPLATES, t.toTypedArray())
    }
}