package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.template.AucTemplate
import com.dengzii.plugin.template.template.Template
import com.dengzii.plugin.template.utils.Logger
import java.util.*

class Module(
    var template: FileTreeNode,
    var language: String,
    var templateName: String,
    var lowercaseDir: Boolean = true,
    var capitalizeFile: Boolean = true,
    var packageNameToDir: Boolean = true,
    var enableApacheVelocity: Boolean = true,
) {

    companion object {

        fun create(template: FileTreeNode, templateName: String): Module {
            Logger.i(Module::class.java.simpleName, "create module. templateName=$templateName")
            return Module(
                template,
                "java",
                templateName,
                lowercaseDir = false,
                capitalizeFile = false,
                packageNameToDir = false
            ).apply {
                template.init(this)
            }
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

        fun getAndroidMvp(): Module {
            return create(Template.ANDROID_MVP, "Android MVP")
        }

        fun getLangList() = Language.values().map { it.name.lowercase(Locale.getDefault()) }.toTypedArray()
    }

    enum class Language {
        JAVA;
    }

    fun initTemplate(node: FileTreeNode = template) {
        node.init(this)
    }

    fun clone(): Module {
        return Module(template.clone(), language, templateName, lowercaseDir, capitalizeFile, packageNameToDir)
    }

}