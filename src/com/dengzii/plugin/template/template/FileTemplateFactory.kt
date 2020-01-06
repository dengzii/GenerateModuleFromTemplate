package com.dengzii.plugin.template.template

import com.intellij.icons.AllIcons
import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory
import com.intellij.openapi.fileTypes.StdFileTypes


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
        val manifestDesc = FileTemplateDescriptor("AndroidManifest.xml", StdFileTypes.XML.icon)
        val applicationDesc = FileTemplateDescriptor("Application.java", StdFileTypes.JAVA.icon)
        val applicationDescKt = FileTemplateDescriptor("Application.kt", StdFileTypes.JAVA.icon)
        descriptor.addTemplate(applicationDesc)
        descriptor.addTemplate(manifestDesc)
        return descriptor
    }
}