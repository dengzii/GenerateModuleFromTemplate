package com.dengzii.plugin.template.tools

import com.intellij.openapi.application.ApplicationManager

fun invokeLater(action: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(action)
}