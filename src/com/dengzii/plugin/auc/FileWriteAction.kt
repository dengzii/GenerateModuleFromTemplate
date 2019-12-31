package com.dengzii.plugin.auc

import com.intellij.util.ThrowableRunnable

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/12/31
 * desc   :
 * </pre>
 */
class FileWriteAction(var kit: PluginKit) : ThrowableRunnable<RuntimeException> {

    override fun run() {
        kit.createDir("helloworld")
    }
}