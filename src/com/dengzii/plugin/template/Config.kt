package com.dengzii.plugin.template

import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.template.AucTemplate
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
        return DEFAULT_MODULE_TEMPLATE.toMutableList()
    }

    fun saveModuleTemplates(templates: List<Module>) {

    }
}