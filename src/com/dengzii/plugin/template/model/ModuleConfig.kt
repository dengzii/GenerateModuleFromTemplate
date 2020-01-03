package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.template.AucFrame

data class ModuleConfig(
        var template: AucFrame,
        var name: String,
        var packageName: String,
        var language: String
)