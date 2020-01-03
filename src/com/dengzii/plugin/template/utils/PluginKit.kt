package com.dengzii.plugin.template.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

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

    fun isProjectValid(): Boolean {
        return project.isOpen && project.isInitialized
    }

    fun getModules(): Array<Module> {
        return ModuleManager.getInstance(project).modules
    }

    fun getPsiFile(): PsiFile? {
        return event.getData(PlatformDataKeys.PSI_FILE)
    }

    fun getVirtualFile(): VirtualFile? {
        return event.getData(PlatformDataKeys.VIRTUAL_FILE)
    }

    fun createDir(name: String, vf: VirtualFile? = getVirtualFile()) {
        if (!checkCreateFile(name, vf)) {
            return
        }
        vf!!.createChildDirectory(null, name)
    }

    fun createFile(name: String, vf: VirtualFile? = getVirtualFile()) {
        if (!checkCreateFile(name, vf)) {
            return
        }
        vf!!.createChildData(null, name)
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

        private val TAG = PluginKit::class.java.simpleName;

        fun get(e: AnActionEvent): PluginKit {
            return PluginKit(e)
        }
    }
}