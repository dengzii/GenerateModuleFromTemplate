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

    private val res = AucFrame {
        res {
            drawable {}
            layout {}
            values {}
        }
    }

    val APP = AucFrame {
        placeholder(Placeholder.MODULE_NAME, "app")
        placeholder(Placeholder.PACKAGE_NAME, "com.example")
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
        placeholder(Placeholder.MODULE_NAME, "pkg")
        placeholder(Placeholder.PACKAGE_NAME, "com.example")
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
        placeholder(Placeholder.MODULE_NAME, "export")
        placeholder(Placeholder.PACKAGE_NAME, "com.example")
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
            placeholder(Placeholder.MODULE_NAME, "feature")
            placeholder(Placeholder.PACKAGE_NAME, "com.example")
            include(APP)
            include(PKG)
            include(EXPORT)
        }
    }
}