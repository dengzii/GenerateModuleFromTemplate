package com.dengzii.plugin.template.template

import com.intellij.icons.AllIcons
import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory
import com.intellij.openapi.fileTypes.StdFileTypes
import javax.swing.Icon


/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/2
 * desc   :
 * </pre>
 */
class FileTemplateFactory : FileTemplateGroupDescriptorFactory {

    override fun getFileTemplatesDescriptor(): FileTemplateGroupDescriptor {

        val descriptor = FileTemplateGroupDescriptor("Module Template Plugin Descriptor", AllIcons.Nodes.Plugin)

        descriptor.addTemplate(getDescriptor("MainActivity", StdFileTypes.JAVA.icon))
        descriptor.addTemplate(getDescriptor("Manifest.xml", StdFileTypes.XML.icon))
        descriptor.addTemplate(getDescriptor("Application.java", StdFileTypes.JAVA.icon))
        descriptor.addTemplate(getDescriptor("build.gradle", StdFileTypes.PLAIN_TEXT.icon))

        return descriptor
    }

    private fun getDescriptor(templateName: String, icon: Icon?): FileTemplateDescriptor {
        return FileTemplateDescriptor(templateName, icon)
    }
}