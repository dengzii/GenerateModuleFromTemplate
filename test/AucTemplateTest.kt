package test

import com.dengzii.plugin.template.template.AucTemplate
import org.junit.Test

class AucTemplateTest {

    @Test
    fun aucAppModuleTest() {
        val app = AucTemplate.APP
        app {
            placeholder("PACKAGE_NAME", "com.dengzii.plugin")
        }
        println(app.placeholders)
//        println(app.getAllPlaceholderInTree())

        app.build()
        println(app)
        println(app.getTreeGraph())
    }

    @Test
    fun aucModuleTemplateTest() {
        val module = AucTemplate.MODULE {
            placeholder("FEATURE_NAME","plugin")
            placeholder("PACKAGE_NAME","com.dengzi")
        }
        module.build()
        println(module.getTreeGraph())
    }
}