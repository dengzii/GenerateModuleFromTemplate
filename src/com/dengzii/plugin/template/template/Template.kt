package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeDsl
import com.dengzii.plugin.template.model.FileTreeNode

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
object Template {

    val ANDROID_RES = FileTreeDsl {
        res {
            drawable { }
            layout { }
            values { }
        }
    }
    val ANDROID_TEST = FileTreeDsl {
        dir("AndroidTest") {

        }
    }

    val JUNIT_TEST = FileTreeDsl {
        dir("test") {

        }
    }

    val EMPTY = FileTreeDsl {
        src
    }

    val ANDROID_APP = FileTreeDsl {
        placeholder("MODULE_NAME", "app")
        placeholder("PACKAGE_NAME", "com.example")

        fileTemplate("MainActivity.java", "Template MainActivity.java")
        fileTemplate("AndroidManifest.xml", "Template Manifest.xml")
        fileTemplate("build.gradle", "Template build.gradle")

        module_name {
            src {
                include(ANDROID_TEST)
                main {
                    java {
                        pkg_name {
                            module_name {
                                file("MainActivity.java")
                            }
                        }
                    }
                    include(ANDROID_RES)
                    AndroidManifest.xml
                }
                include(JUNIT_TEST)
            }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }
}