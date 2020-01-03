package com.dengzii.plugin.template.template

import com.dengzii.plugin.template.utils.Logger

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/2
 * desc   :
 * </pre>
 */

enum class Placeholder {

    MODULE_NAME,
    PACKAGE_NAME,
    CLASS_NAME,
    FILE_NAME,
    APPLICATION_NAME,
    PROJECT_NAME,
    BASE_APPLICATION;

    fun getPlaceholder(): String {
        return "\${$name}"
    }

    override fun toString(): String {
        return name
    }
}

fun String.findPlaceholder(): List<Placeholder> {
    val result = mutableListOf<Placeholder>()
    Placeholder.values().forEach {
        if (contains(it.name, true)) {
            result.add(it)
        }
    }
    return result
}

fun String.replacePlaceholder(placeholders: Map<Placeholder, String>?): String {
    var after = this
    if (placeholders.isNullOrEmpty()) {
        return this
    }
    placeholders.forEach { (k, v) ->
        after = after.replace(k.getPlaceholder(), v)
    }
    if (this != after) {
        Logger.d("String.replacePlaceholder", "before: $this => after: $after")
    }
    return after
}
