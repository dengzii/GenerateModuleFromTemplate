package com.dengzii.plugin.auc.model

import com.dengzii.plugin.auc.template.AucFrame

data class ModuleConfig(
        var template: AucFrame,
        var name: String,
        var packageName: String,
        var language: String
)