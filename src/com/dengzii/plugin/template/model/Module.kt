package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.template.AucTemplate
import com.dengzii.plugin.template.template.Template
import com.dengzii.plugin.template.utils.Logger

class Module(
        var template: FileTreeNode,
        var language: String,
        var templateName: String
) {

    companion object {

        fun create(template: FileTreeNode, templateName: String): Module {
            Logger.i(Module::class.java.simpleName, "create module. templateName=$templateName")
            return Module(template, "java", templateName)
        }

        fun getAndroidApplication(): Module {
            return create(Template.ANDROID_APP.clone(), "AndroidApp")
        }

        fun getEmpty(): Module {
            return create(Template.EMPTY.clone(), "EmptyModule")
        }

        fun getAucModule(): Module {
            return create(AucTemplate.MODULE.clone(), "Auc Module")
        }

        fun getAucApp(): Module {
            return create(AucTemplate.APP.clone(), "Auc App")
        }

        fun getAucPkg(): Module {
            return create(AucTemplate.PKG.clone(), "Auc Pkg")
        }

        fun getAucExport(): Module {
            return create(AucTemplate.EXPORT.clone(), "Auc Export")
        }

        fun getLangList() = Language.values().map { it.name.toLowerCase() }.toTypedArray()
    }

    enum class Language {
        JAVA;
    }

    fun clone(): Module {
        return create(template.clone(), templateName)
    }

}