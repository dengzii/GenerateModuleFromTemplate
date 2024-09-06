package com.dengzii.plugin.template.utils

import com.jetbrains.rd.util.printlnError
import java.text.SimpleDateFormat

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzixx@gmail.com
 * github : https://github.com/dengzii
 * time   : 2019/12/31
 * desc   :
 * </pre>
 */
object Logger {

    var enable: Boolean = true
    var timeFormatter = SimpleDateFormat("MM-dd hh:mm:ss")

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

    fun w(tag: String, log: String) {
        log("w", tag, log)
    }

    private fun log(level: String, tag: String, log: String) {
        if (!enable) {
            return
        }

        val logStr = "${getTime()} ${level.toUpperCase()}/$tag: $log"
        if (level == "e") {
            printlnError(logStr)
            return
        }
        println(logStr)
    }

    private fun getTime(): String {
        return timeFormatter.format(System.currentTimeMillis())
    }
}