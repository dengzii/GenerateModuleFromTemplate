package com.dengzii.plugin.template.model

class ModuleConfig(
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
                   packageName: String, language: String, templateName: String): ModuleConfig {
            return ModuleConfig(template, moduleName, packageName, language, templateName)
        }

        fun getLangList() = Language.values().map { it.name.toLowerCase() }.toTypedArray()
    }
}