package com.dengzii.plugin.template.utils

import com.jetbrains.rd.util.printlnError

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/12/31
 * desc   :
 * </pre>
 */
object Logger {

    var enable: Boolean = true

    fun e(tag: String, log: String) {
        log("e", tag, log)
    }

    fun e(tag: String, e: Throwable) {
        log("e", tag, e.toString())
        e.printStackTrace()
    }

    fun i(tag: String, log: String) {
        log("i", tag, log)
    }

    fun d(tag: String, log: String) {
        log("d", tag, log)
    }

    private fun log(level: String, tag: String, log: String) {
        if (!enable) {
            return
        }
        val logStr = "${level.toUpperCase()}/$tag: $log"
        if (level == "e") {
            printlnError(logStr)
            return
        }
        println(logStr)
    }
}