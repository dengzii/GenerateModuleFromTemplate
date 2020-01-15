package com.dengzii.plugin.template.template

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

    private val BASE = AucFrame {

        fileTemplates = aucFileTemplates()
        placeholder(Placeholder.PACKAGE_NAME.value("com.example"))
        placeholder(Placeholder.MODULE_NAME.value("feature"))
        placeholder(Placeholder.APPLICATION_NAME.value("App"))
    }

    val APP = (BASE.clone()) {
        placeholder(Placeholder.MODULE_NAME.value("feature"))

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

    val PKG = (BASE.clone()) {
        placeholder(Placeholder.MODULE_NAME.value("feature"))
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

    val EXPORT = (BASE.clone()) {
        placeholder(Placeholder.MODULE_NAME.value("feature"))
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

    val MODULE = (BASE.clone()) {
        placeholder(Placeholder.MODULE_NAME.value("feature"))
        module_name {
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}