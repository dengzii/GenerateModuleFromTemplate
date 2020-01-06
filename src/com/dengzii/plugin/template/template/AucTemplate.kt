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
fun main() {
    val temp = AucFrame {
        dir("src/main/") {
            dir("java") {
                dir("com/example"){
                    dir("foo"){
                        file("bar.txt")
                    }
                    dir("app/c")
                }
            }
            dir("res") {
                dir("img/png"){

                }
            }
        }
        build.gradle
    }
    println(temp.getTreeGraph())
}

object AucTemplate {

    private val res = AucFrame {
        res {
            drawable {}
            layout {}
            values {}
        }
    }

    private val aucFileTemplates: () -> MutableMap<String, String> = {
        mutableMapOf(
                Pair("AndroidManifest.xml", "Template AndroidManifest.xml"),
                Pair("Application.java", "Template App.java"),
                Pair("build.gradle", "Template build.gradle"),
                Pair("${Placeholder.APPLICATION_NAME.getPlaceholder()}.java", "Template App.java")
        )
    }

    private val BASE = AucFrame {

        fileTemplates = aucFileTemplates()
        placeholder(Placeholder.PACKAGE_NAME, "com.example")
        placeholder(Placeholder.MODULE_NAME, "app")
        placeholder(Placeholder.APPLICATION_NAME, "App")

        gitignore
        build.gradle
        proguard_rules.pro
    }

    val APP = (BASE.clone()) {
        fileTemplates = aucFileTemplates()
        placeholder(Placeholder.PACKAGE_NAME, "com.example")
        placeholder(Placeholder.MODULE_NAME, "app")

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
                    include(res)
                    AndroidManifest.xml
                }
                test {}
            }
        }
    }

    val PKG = (BASE.clone()) {
        placeholder(Placeholder.MODULE_NAME, "pkg")
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
                    include(res)
                    AndroidManifest.xml
                }
            }
        }
    }

    val EXPORT = AucFrame {
        placeholder(Placeholder.MODULE_NAME, "export")
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
        }
    }

    val MODULE = AucFrame {
        module_name {
            placeholder(Placeholder.MODULE_NAME, "feature")
            placeholder(Placeholder.PACKAGE_NAME, "com.example")
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}