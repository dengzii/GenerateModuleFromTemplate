package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeNode

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
object AucTemplate {

    private val aucFileTemplates: () -> MutableMap<String, String> = {
        mutableMapOf(
                Pair("AndroidManifest.xml", "Template Manifest.xml"),
                Pair("Application.java", "Template Application.java"),
                Pair("build.gradle", "Template build.gradle"),
                Pair("\${APPLICATION_NAME}.java", "Template Application.java")
        )
    }

    private val aucPlaceholders: () -> MutableMap<String, String> = {
        mutableMapOf(
                Pair("PACKAGE_NAME", "com.example"),
                Pair("FEATURE_NAME", "feature"),
                Pair("APPLICATION_NAME", "App")
        )
    }

    val APP = FileTreeNode {
        fileTemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        placeholder("MODULE_NAME", "app")

        dir("app") {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    app_name.java
                                    file("MainActivity.java")
                                }
                            }
                        }
                    }
                    include(Template.ANDROID_RES)
                    AndroidManifest.xml
                }
                test {}
            }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val PKG = FileTreeNode {
        fileTemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        placeholder("MODULE_NAME", "pkg")
        dir("pkg") {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    file("\${FEATURE_NAME}ApiImpl.java")
                                }

                            }
                        }
                    }
                    include(Template.ANDROID_RES)
                    AndroidManifest.xml
                }
            }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val EXPORT = FileTreeNode {
        fileTemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        placeholder("MODULE_NAME", "export")
        dir("export") {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    dir("api") {
                                        file("\${FEATURE_NAME}Api.java")
                                    }
                                    dir("bean")
                                }
                            }
                        }
                    }
                    AndroidManifest.xml
                }
            }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val MODULE = FileTreeNode {
        fileTemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        feature_name {
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}