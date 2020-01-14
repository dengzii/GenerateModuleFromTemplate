package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeNode

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/14
 * desc   :
 * </pre>
 */
object Template {

    val ANDROID_RES = FileTreeNode {
        res {
            drawable {}
            layout {}
            values {}
        }
    }

    val ANDROID_APP = FileTreeNode {
        placeholder(Placeholder.MODULE_NAME, "app")
        placeholder(Placeholder.PACKAGE_NAME, "com.example")

        dir("libs")
        src {
            dir("androidTest")
            main {
                java {
                    pkg_name {
                        module_name {
                            file("MainActivity.java}")
                        }
                    }
                }
                include(ANDROID_RES)
                AndroidManifest.xml
            }
            dir("test")
        }
        gitignore
        build.gradle
        proguard_rules.pro
    }
}