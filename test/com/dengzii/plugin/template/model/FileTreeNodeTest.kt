package com.dengzii.plugin.template.model

import junit.framework.TestCase
import org.junit.Test

/**
 *
 * @author https://github.com/dengzii
 */
class FileTreeNodeTest : TestCase() {

    @Test
    fun testGetPlaceholders() {
        val root = FileTreeNode(null, "\${AAA}_\${BBB}_\${CCC_\${DDD}}", true)
        println(root.name)
        println(root.getPlaceholderInNodeName().joinToString(","))
        root.putPlaceholders(
            mapOf(
                Pair("AAA", "1"),
                Pair("BBB", "2"),
                Pair("CCC_4", "3"),
                Pair("DDD", "4"),
            )
        )
        println(root.getRealName())
    }

    @Test
    fun testPlaceholderCycle() {

    }

    @Test
    fun testExpandPath() {
        val dsl = FileTreeDsl {
            dir("root") {
                dir("a/b") {
                    dir("com.example") {

                    }
                }
            }
        }
        val m = Module.create(dsl, "test")
        m.packageNameToDir = true
        println(m.template.getTreeGraph())
        m.template.expandPath()
        println(m.template.getTreeGraph())
        m.template.expandPkgName(true)
        println(m.template.getTreeGraph())
    }
}