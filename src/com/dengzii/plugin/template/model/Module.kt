package com.dengzii.plugin.template.model

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
            return Module(template, moduleName, packageName, language, templateName)
        }

        fun getLangList() = Language.values().map { it.name.toLowerCase() }.toTypedArray()
    }
}