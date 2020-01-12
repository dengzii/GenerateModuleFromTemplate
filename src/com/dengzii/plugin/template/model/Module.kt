package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.utils.Logger

class Module(
        var template: FileTreeNode,
        var name: String,
        var packageName: String,
        var language: String,
        var templateName: String
) {

    enum class Language {
        JAVA, KOTLIN;
    }

    companion object {
        fun create(template: FileTreeNode, moduleName: String,
                   packageName: String, language: String, templateName: String): Module {
            Logger.i(Module::class.java.simpleName, "create module.  $template moduleName=$moduleName, templateName=$templateName")
            return Module(template, moduleName, packageName, language, templateName)
        }

        fun getLangList() = Language.values().map { it.name.toLowerCase() }.toTypedArray()
    }
}