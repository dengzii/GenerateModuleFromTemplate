package com.dengzii.plugin.template.template

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/2
 * desc   :
 * </pre>
 */

class Placeholder(var name: String, var value: String) {

    val placeholder = "\${${name}}"

    companion object {
        val MODULE_NAME = Placeholder("MODULE_NAME", "module")
        val PACKAGE_NAME = Placeholder("PACKAGE_NAME", "com.example")
        val APPLICATION_NAME = Placeholder("APPLICATION_NAME", "App")
    }

    fun value(value: String): Placeholder {
        this.value = value
        return this
    }

    override fun toString(): String {
        return "Placeholder(name='$name', value='$value')"
    }
}

fun String.replacePlaceholder(placeholders: Map<String, String>?): String {
    var after = this
    if (placeholders.isNullOrEmpty()) {
        return this
    }
    placeholders.forEach { (k, v) ->
        after = after.replace(k, v)
    }
    return after
}
