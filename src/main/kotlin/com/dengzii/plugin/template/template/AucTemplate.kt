package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeDsl

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
object AucTemplate {

    private val aucFileTemplates: () -> MutableMap<String, String> = {
        mutableMapOf(
                Pair("AndroidManifest.xml", "Template Manifest.xml"),
                Pair("build.gradle", "Template build.gradle"),
                Pair("MainActivity.java", "Template MainActivity.java"),
                Pair("\${FEATURE_NAME}App.java", "Template Application.java"),
                Pair("\${FEATURE_NAME}Api.java", "Template AucApiClass.java"),
                Pair("\${FEATURE_NAME}ApiImpl.java", "Template AucApiImplClass.java")
        )
    }

    private val aucPlaceholders: () -> MutableMap<String, String> = {
        mutableMapOf(
                Pair("PACKAGE_NAME", "com.example"),
                Pair("FEATURE_NAME", "Feature1")
        )
    }

    val APP = FileTreeDsl {
        addFileTemplates(aucFileTemplates())
        putPlaceholders(aucPlaceholders())
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
        putPlaceholders(aucPlaceholders())
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
        putPlaceholders(aucPlaceholders())
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
        putPlaceholders(aucPlaceholders())
        dir("\${FEATURE_NAME}") {

            include(APP {
                placeholders?.remove("\${FEATURE_NAME}")
                placeholders?.remove("\${PACKAGE_NAME}")
            })
            include(PKG {
                placeholders?.remove("\${FEATURE_NAME}")
                placeholders?.remove("\${PACKAGE_NAME}")
            })
            include(EXPORT {
                placeholders?.remove("\${FEATURE_NAME}")
                placeholders?.remove("\${PACKAGE_NAME}")
            })
        }
    }
}