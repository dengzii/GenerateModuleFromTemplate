package com.dengzii.plugin.auc.template

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

    PACKAGE_NAME, CLASS_NAME, FILE_NAME, PROJECT_NAME, BASE_APPLICATION;

    fun getPlaceholder(): String {
        return "\${$name}"
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
    if (placeholders.isNullOrEmpty()) {
        return this
    }
    placeholders.forEach { (k, v) ->
        replace(k.getPlaceholder(), v, true)
    }
    return this
}
