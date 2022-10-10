package com.dengzii.plugin.template

import com.dengzii.plugin.template.TemplateConfigurable.OnApplyListener
import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.utils.Logger
import com.dengzii.plugin.template.utils.PluginKit
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2022/10/10
 * desc   :
</pre> *
 */
class CreateTemplateFromDirAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val kit = PluginKit.init(e)
        if (!kit.isProjectValid()) {
            Logger.d(CreateTemplateFromDirAction::class.java.simpleName, "Project is not valid.")
            return
        }

        val currentPsiDirectory = kit.getCurrentPsiDirectory() ?: return
        val treeNode = FileTreeNode(null, currentPsiDirectory.name, true)
        val root = FileTreeNode(null, "", true)
        root.addChild(treeNode)
        buildTreeNode(treeNode, currentPsiDirectory)

        ShowSettingsUtil.getInstance().editConfigurable(e.project, TemplateConfigurable(null, root))
    }

    private fun buildTreeNode(parent: FileTreeNode, psiDirectory: PsiDirectory) {
        for (psiFile in psiDirectory.children) {
            if (psiFile !is PsiFileSystemItem) {
                continue
            }
            val node = FileTreeNode(parent, psiFile.name, psiFile.isDirectory)
            if (psiFile.isDirectory) {
                buildTreeNode(node, psiFile as PsiDirectory)
            }
            parent.addChild(node)
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = e.getData(PlatformDataKeys.PSI_ELEMENT) is PsiDirectory
    }
}