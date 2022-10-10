package com.dengzii.plugin.template.utils

import com.dengzii.plugin.template.tools.NotificationUtils
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.impl.FileTemplateManagerImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.util.*

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/12/31
 * desc   :
 * </pre>
 */
class PluginKit private constructor(e: AnActionEvent) {

    private val event = e
    lateinit var project: Project

    init {
        if (e.project != null) {
            project = e.project!!
        }
    }

    private inline fun requireProject(action: () -> Unit) {
        if (!isProjectValid()) {
            NotificationUtils.showError("Plugin GenerateModuleFromTemplate require a project.")
            action.invoke()
        }
    }

    fun isProjectValid(): Boolean {
        return project.isOpen && project.isInitialized
    }

    fun getModules(): Array<Module> {
        return ModuleManager.getInstance(project).modules
    }

    fun getCurrentPsiFile(): PsiFile? {
        return event.getData(PlatformDataKeys.PSI_FILE)
    }

    fun getCurrentPsiDirectory(): PsiDirectory? {
        val e = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (e is PsiDirectory) {
            return e
        }
        return null
    }

    fun getPsiDirectoryByPath(path: String): PsiDirectory? {
        val vf = VirtualFileManager.getInstance().findFileByUrl("") ?: return null
        return PsiManager.getInstance(project).findDirectory(vf)
    }

    fun getPsiDirectoryByVirtualFile(vf: VirtualFile): PsiDirectory? {
        if (!vf.isDirectory) {
            Logger.e(TAG, "${vf.path} is not a directory.")
            return null
        }
        return PsiManager.getInstance(project).findDirectory(vf)
    }

    fun getVirtualFile(): VirtualFile? {
        return event.getData(PlatformDataKeys.VIRTUAL_FILE)
    }

    fun createDir(name: String, vf: VirtualFile? = getVirtualFile()): VirtualFile? {
        if (vf == null || !vf.isDirectory) {
            Logger.e(TAG, "target is null or is not a directory.")
            return null
        }
        if (vf.findChild(name)?.exists() == true) {
            return vf.findChild(name)
        }
        return vf.createChildDirectory(null, name)
    }

    fun createFile(name: String, vf: VirtualFile? = getVirtualFile()): Boolean {
        if (!checkCreateFile(name, vf)) {
            return false
        }
        return try {
            vf!!.createChildData(null, name)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun calculateTemplate(templateContent: String): Array<String>? {
        requireProject {
            return null
        }
        return FileTemplateUtil.calculateAttributes(templateContent, Properties(), true, project)
    }

    fun createFileFromTemplate(
        fileName: String,
        templateName: String,
        propertiesMap: Map<String, String>,
        directory: VirtualFile
    ): PsiElement? {

        val fileTemplateManager = FileTemplateManager.getInstance(project)
        val properties = Properties(fileTemplateManager.defaultProperties)
        propertiesMap.forEach { (t, u) ->
            properties.setProperty(t.strip(), u)
        }
        val template = fileTemplateManager.getTemplate(templateName) ?: return null
        val psiDirectory = getPsiDirectoryByVirtualFile(directory) ?: return null
        return try {
            FileTemplateUtil.createFromTemplate(template, fileName, properties, psiDirectory)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun addTemplate(name: String, template: String) {
        FileTemplateManager.getInstance(project).addTemplate(name, template)
    }

    private fun String.strip(): String {
        var result = this
        if (startsWith("\${") && endsWith("}")) {
            result = result.substring(2, length - 1)
        }
        return result
    }

    private fun checkCreateFile(name: String, vf: VirtualFile?): Boolean {
        if (vf == null || !vf.isDirectory) {
            Logger.e(TAG, "target is null or is not a directory.")
            return false
        }
        if (vf.findChild(name)?.exists() == true) {
            Logger.e(TAG, "directory already exists.")
            return false
        }
        return true
    }

    companion object {

        private val TAG = PluginKit::class.java.simpleName

        private lateinit var INSTANCE: PluginKit

        fun init(e: AnActionEvent): PluginKit {
            INSTANCE = PluginKit(e)
            return INSTANCE
        }

        fun getAllFileTemplate(): Array<FileTemplate> = FileTemplateManagerImpl.getDefaultInstance().allTemplates

        fun getFileTemplate(name: String): FileTemplate? = FileTemplateManagerImpl.getDefaultInstance().getTemplate(name)
    }
}