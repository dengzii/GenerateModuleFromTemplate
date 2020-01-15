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
                Pair("\${PACKAGE_NAME}", "com.example"),
                Pair("\${MODULE_NAME}", "feature"),
                Pair("\${APPLICATION_NAME}", "App")
        )
    }

    val APP = FileTreeNode {
        filtemplates(aucFileTemplates())
        placeholders(aucPlaceholders())

        app {
            src {
                main {
                    java {
                        pkg_name {
                            module_name {
                                app {
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
        filtemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        pkg {
            src {
                main {
                    java {
                        pkg_name {
                            module_name {
                                pkg {
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
        filtemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        export {
            src {
                main {
                    java {
                        pkg_name {
                            module_name {
                                export {
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
        filtemplates(aucFileTemplates())
        placeholders(aucPlaceholders())
        module_name {
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}