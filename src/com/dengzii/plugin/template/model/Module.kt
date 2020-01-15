package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.utils.Logger

class Module(
        var template: FileTreeNode,
        var language: String,
        var templateName: String
) {

    enum class Language {
        JAVA;
    }

    fun clone(): Module {
        return create(template.clone(), templateName)
    }

    companion object {
        fun create(template: FileTreeNode, templateName: String): Module {
            Logger.i(Module::class.java.simpleName, "create module. templateName=$templateName")
            return Module(template, "java", templateName)
        }

        fun getLangList() = Language.values().map { it.name.toLowerCase() }.toTypedArray()
    }
}