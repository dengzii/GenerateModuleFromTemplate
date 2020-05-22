package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.model.FileTreeDsl

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
        dir("res") {
            dir("drawable") { }
            dir("layout") { }
            dir("values") { }
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
        file("src")
    }

    val ANDROID_MVP = FileTreeDsl {
        placeholder("MVP_NAME", "Example")

//        fileTemplate("", "")

        file("\${MVP_NAME}Contract.java")
        file("\${MVP_NAME}View.java")
        file("\${MVP_NAME}Presenter.java")
        file("\${MVP_NAME}Model.java")
    }

    val ANDROID_APP = FileTreeDsl {
        placeholder("MODULE_NAME", "app")
        placeholder("PACKAGE_NAME", "com.example")

        fileTemplate("MainActivity.java", "Template MainActivity.java")
        fileTemplate("AndroidManifest.xml", "Template Manifest.xml")
        fileTemplate("build.gradle", "Template build.gradle")

        dir("\${MODULE_NAME}") {
            dir("src") {
                include(ANDROID_TEST)
                dir("main") {
                    dir("java") {
                        dir("\${PACKAGE_NAME}") {
                            dir("\${MODULE_NAME}") {
                                file("MainActivity.java")
                            }
                        }
                    }
                    include(ANDROID_RES)
                    file("AndroidManifest.xml")
                }
                include(JUNIT_TEST)
            }
            file(".gitignore")
            file("build.gradle")
            file("ProGuard.pro")
        }
    }
}