package test

import com.dengzii.plugin.template.template.AucTemplate
import org.junit.Test

class AucTemplateTest {

    @Test
    fun aucAppModuleTest() {
        println(AucTemplate.APP.getTreeGraph())
    }
}