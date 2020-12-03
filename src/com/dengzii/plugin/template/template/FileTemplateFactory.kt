package com.dengzii.plugin.template.template

import com.intellij.icons.AllIcons
import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory
import com.intellij.openapi.fileTypes.FileTypeManager
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

        descriptor.addTemplate(getDescriptor("MainActivity.java", getFileIconByExt("java")))
        descriptor.addTemplate(getDescriptor("Manifest.xml", getFileIconByExt("xml")))
        descriptor.addTemplate(getDescriptor("Application.java", getFileIconByExt("java")))
        descriptor.addTemplate(getDescriptor("build.gradle", getFileIconByExt("gradle")))
        return descriptor
    }

    private fun getFileIconByExt(ext: String): Icon {
        return FileTypeManager.getInstance().getFileTypeByExtension(ext).icon ?: AllIcons.FileTypes.Unknown
    }

    private fun getDescriptor(templateName: String, icon: Icon?): FileTemplateDescriptor {
        return FileTemplateDescriptor(templateName, icon)
    }
}