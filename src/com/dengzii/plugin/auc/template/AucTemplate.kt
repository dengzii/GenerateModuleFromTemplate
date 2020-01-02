package com.dengzii.plugin.auc.template

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */

object AucTemplate {

    val APP = AucFrame {
        app {
            src {
                main {
                    java {
                        pkg_name {
                            App.java
                        }
                    }
                    res {
                        drawable {}
                        layout {}
                        values {}
                    }
                    AndroidManifest.xml
                }
                test {}
            }
            gitignore
            build.gradle
            proguard_rules.pro
        }
    }
}