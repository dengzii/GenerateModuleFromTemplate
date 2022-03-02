package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.Config
import junit.framework.TestCase

/**
 *
 * @author https://github.com/dengzii
 */
class ModuleTest : TestCase() {

    fun testPersist() {
        val t = FileTreeDsl {
            dir("root") {

            }
        }
        val create = Module.create(t, "test")
        create.lowercaseDir = true
        val j = Config.GSON.toJson(create)
        println(j)
    }
}