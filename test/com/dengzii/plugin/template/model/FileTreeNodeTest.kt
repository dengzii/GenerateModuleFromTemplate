package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.Config
import junit.framework.TestCase
import org.apache.velocity.VelocityContext
import org.apache.velocity.util.StringUtils
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


    fun testAppcacheVelocity() {
        val r = """
            !{StringUtils.removeAndHump(!{FEATURE_NAME.replaceAll("[^\w]", "_")})}Module.kt
        """.replace("!", "\$")

        val dsl = FileTreeDsl {
            placeholder("FT", "com-example")
            file("\${StringUtils.removeAndHump(\${FT.replaceAll(\"[_-]\", \"_\")})}")
            file(r)
            file("\${FT.replaceAll(\"[-]\", \"_\")}")
        }
        val module = Config.GSON.fromJson("", Module::class.java)

        val m = Module.create(dsl, "test")
        m.enableApacheVelocity = true
        m.template.context = VelocityContext().apply {
            put("StringUtils", StringUtils::class.java)
            put("FT", "com-example")
        }
        m.template.resolve()
        println(m.template.getTreeGraph())
    }

    fun testExpandPkg() {

        val dsl = FileTreeDsl {
            placeholder("FT", "com_demo")
            fileTemplate("\${FT.replaceAll(\"[_]\", \".\")}.java", "Example")
            fileTemplate("\${StringUtils.removeAndHump(\${FT.replaceAll(\"[_]\", \"-\")})}Module.kt", "ExampleModule")
            dir("root") {
                dir("\${FT.replaceAll(\"[_]\", \".\")}") {
                    file("a.txt")
                }
                file("\${FT.replaceAll(\"[_]\", \".\")}.java")
                file("\${StringUtils.removeAndHump(\${FT.replaceAll(\"[_]\", \"-\")})}Module.kt")
            }
        }

        val m = Module.create(dsl, "test")
        m.packageNameToDir = true
        m.packageNameToDir = true
        m.enableApacheVelocity = true

        m.template.context = VelocityContext().apply {
            put("StringUtils", StringUtils::class.java)
            m.template.getPlaceholderInherit()?.forEach { (k, v) ->
                put(k, v)
            }
        }

        m.template.resolveTreeFileName()
        m.template.resolveFileTemplate()
        m.template.resolve()

        m.template.children.last().children.last().let {
            println(it.name)
            println(it.fileTemplates)
            println(it.getTemplateFile())
        }

        m.template.expandPkgName(true)
        println(m.template.getTreeGraph(templateFile = true))
    }
}