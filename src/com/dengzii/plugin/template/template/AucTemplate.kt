package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeDsl

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
                Pair("PACKAGE_NAME", "com/example"),
                Pair("FEATURE_NAME", "feature"),
                Pair("APPLICATION_NAME", "App")
        )
    }

    val APP = FileTreeDsl {
        addFileTemplates(aucFileTemplates())
        addPlaceholders(aucPlaceholders())
        placeholder("MODULE_NAME", "app")

        dir("app") {
            dir("src") {
                dir("main") {
                    dir("java") {
                        dir("\${PACKAGE_NAME}") {
                            dir("\${FEATURE_NAME}") {
                                dir("app") {
                                    file("MainActivity.java")
                                    file("\${FEATURE_NAME}App.java")
                                }
                            }
                        }
                    }
                    include(Template.ANDROID_RES)
                    file("AndroidManifest.xml")
                }
            }
            file(".gitignore")
            file("build.gradle")
            file("proguard-rules.pro")
        }
    }

    val PKG = FileTreeDsl {
        addFileTemplates(aucFileTemplates())
        addPlaceholders(aucPlaceholders())
        placeholder("MODULE_NAME", "pkg")
        dir("pkg") {
            dir("src") {
                dir("main") {
                    dir("java") {
                        dir("\${PACKAGE_NAME}") {
                            dir("\${FEATURE_NAME}") {
                                dir("pkg") {
                                    file("\${FEATURE_NAME}ApiImpl.java")
                                }
                            }
                        }
                    }
                    include(Template.ANDROID_RES)
                    file("AndroidManifest.xml")
                }
            }
            file(".gitignore")
            file("build.gradle")
            file("proguard-rules.pro")
        }
    }

    val EXPORT = FileTreeDsl {
        addFileTemplates(aucFileTemplates())
        addPlaceholders(aucPlaceholders())
        placeholder("MODULE_NAME", "export")
        dir("export") {
            dir("src") {
                dir("main") {
                    dir("java") {
                        dir("\${PACKAGE_NAME}") {
                            dir("\${FEATURE_NAME}") {
                                dir("export") {
                                    file("\${FEATURE_NAME}Api.java")
                                }
                            }
                        }
                    }
                    include(Template.ANDROID_RES)
                    file("AndroidManifest.xml")
                }
            }
            file(".gitignore")
            file("build.gradle")
            file("proguard-rules.pro")
        }
    }

    val MODULE = FileTreeDsl {
        addFileTemplates(aucFileTemplates())
        addPlaceholders(aucPlaceholders())
        dir("\${FEATURE_NAME}") {
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}