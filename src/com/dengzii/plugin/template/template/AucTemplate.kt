package com.dengzii.plugin.template.template

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */

fun main() {
    AucTemplate.MODULE.traversal({ i, d ->
        println(i.getPath())
    })
}

object AucTemplate {

    private val res = AucFrame {
        res {
            drawable {}
            layout {}
            values {}
        }
    }

    val DEFAULT_PLACEHOLDER = mapOf(
            Pair(Placeholder.APPLICATION_NAME, "App"),
            Pair(Placeholder.MODULE_NAME, "module"),
            Pair(Placeholder.PACKAGE_NAME, "com.example.app"),
            Pair(Placeholder.PROJECT_NAME, "Example")
    )

    val APP = AucFrame {
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
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val PKG = AucFrame {
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
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val EXPORT = AucFrame {
        export {
            src { main {
                    java { pkg_name { module_name { export {
                                    dir("api") {
                                        file("\${MODULE_NAME}Api.java")
                                    }
                                    dir("bean")
                    } } } }
                    AndroidManifest.xml
            } }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }

    val MODULE = AucFrame {
        module_name {
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}