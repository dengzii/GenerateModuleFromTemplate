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
    fun testFileTemplate() {
        FileTreeDsl {
            placeholder("PKG", "com.example")
            placeholder("F3", "f3")

            fileTemplate("f1.java", "file1.java")
            fileTemplate("f2.java", "ff2.java")
            fileTemplate("\${PKG}/f2.java", "file2.java")
            fileTemplate("f3.java", "file3.java")

            dir("root") {
                dir("a/b") {
                    dir("\${PKG}") {
                        file("f1.java")
                        file("f2.java")
                        file("\${F3}.java")

                    }
                    dir("c") {
                        file("f2.java")
                    }
                }
            }

            // parse file template file name use placeholder, and support mat

            resolveFileTemplate()
            println(getAllTemplateMap())
            println(getTreeGraph(templateFile = true))
        }
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