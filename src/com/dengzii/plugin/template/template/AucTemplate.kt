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
                Pair("AndroidManifest.xml", "Template AndroidManifest.xml"),
                Pair("Application.java", "Template App.java"),
                Pair("build.gradle", "Template build.gradle"),
                Pair("\${APPLICATION_NAME}.java", "Template App.java")
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

        module_name {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    app_name.java
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
        module_name {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    main {}
                                    file("\${MODULE_NAME}ApiImpl.java")
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
        module_name {
            src {
                main {
                    java {
                        pkg_name {
                            feature_name {
                                module_name {
                                    dir("api") {
                                        file("\${MODULE_NAME}Api.java")
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