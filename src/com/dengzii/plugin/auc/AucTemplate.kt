package com.dengzii.plugin.auc

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
@Suppress("SpellCheckingInspection")
object AucTemplate {

    val APP = FileTree {
        dir("src") {
            dir("main") {
                dir("com.example.feature.app"){
                    file("App.java")
                }
            }
        }
        file(".gitignore")
        file("build.gradle")
        file("proguard-rules.pro")
    }

}