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
//        app.expandPath()
//        app.expandPkgName(true)
        println(app)
        println(app.getTreeGraph())
    }

    @Test
    fun aucModuleTemplateTest() {
        val module = AucTemplate.MODULE
        module {
            placeholder("FEATURE_NAME", "plugin")
            placeholder("PACKAGE_NAME", "com.dengzi")
        }
        module.expandPath()
        println(module.getTreeGraph())
    }
}