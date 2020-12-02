package com.dengzii.plugin.template.ui

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.tools.ui.PopMenuUtils
import com.dengzii.plugin.template.tools.ui.onRightMouseButtonClicked
import com.dengzii.plugin.template.utils.Logger
import com.intellij.icons.AllIcons
import com.intellij.packageDependencies.ui.TreeModel
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.IconUtil
import java.awt.BorderLayout
import java.awt.event.*
import java.util.*
import java.util.function.Consumer
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class PreviewPanel : JPanel() {

    private var fileTree: Tree = Tree()

    private lateinit var module: Module
    private val fileIconMap: MutableMap<String, Icon> = HashMap()
    private var replacePlaceholder = true
    private var onTreeUpdateListener: (() -> Unit)? = null

    // render tree node icon, title
    private val treeCellRenderer = object : ColoredTreeCellRenderer() {
        override fun customizeCellRenderer(jTree: JTree, value: Any, b: Boolean, b1: Boolean, b2: Boolean, i: Int, b3: Boolean) {
            if (value is DefaultMutableTreeNode) {
                val node = value.userObject
                if (node is FileTreeNode) {
                    icon = IconUtil.getAddClassIcon()
                    this.append(if (replacePlaceholder) node.getRealName() else node.name)
                    if (node.isDir) {
                        icon = AllIcons.Nodes.Package
                    } else {
                        var suffix = ""
                        if (node.name.contains(".")) {
                            suffix = node.name.substring(node.name.lastIndexOf("."))
                        }
                        icon = fileIconMap.getOrDefault(suffix, AllIcons.FileTypes.Text)
                    }
                } else {
                    icon = AllIcons.FileTypes.Unknown
                }
            }
        }
    }

    init {
        layout = BorderLayout()
        add(JBScrollPane().apply {
            setViewportView(fileTree)
        }, BorderLayout.CENTER)
        initPanel()
    }

    fun setReplacePlaceholder(replace: Boolean) {
        if (replace != replacePlaceholder) {
            replacePlaceholder = replace
            fileTree.updateUI()
        }
    }

    /**
     * Preview file tree, clone and build tree.
     */
    fun setModuleConfigPreview(module: Module) {
        val clone = module.clone()
        clone.template.build()
        setModuleConfig(clone)
    }

    fun setModuleConfig(module: Module) {
        Logger.i("PreviewPanel", "setModuleConfig")
        this.module = module
        fileTree.model = getTreeModel(this.module.template)
        fileTree.doLayout()
        fileTree.updateUI()
        expandAll(fileTree, TreePath(fileTree.model.root), true)
    }

    /**
     * Set the callback of tree edit, delete, add event.
     */
    fun setOnTreeUpdateListener(listener: (() -> Unit)?) {
        onTreeUpdateListener = listener
    }

    /**
     * init Tree icon, node title, mouse listener
     */
    private fun initPanel() {
        fileIconMap[".java"] = AllIcons.Nodes.Class
        fileIconMap[".kt"] = AllIcons.Nodes.Class
        fileIconMap[".xml"] = AllIcons.FileTypes.Xml
        fileIconMap[".gradle"] = AllIcons.FileTypes.Config
        fileIconMap[".gitignore"] = AllIcons.FileTypes.Config
        fileIconMap[".pro"] = AllIcons.FileTypes.Config
        fileTree.onRightMouseButtonClicked { e ->
            val row = fileTree.getRowForLocation(e.x, e.y)
            if (row != -1) {
                val treeNode = fileTree.lastSelectedPathComponent as DefaultMutableTreeNode
                val nodes = treeNode.userObjectPath
                // has selected node and the node is FileTreeNode
                if (nodes.isEmpty() || nodes[0] !is FileTreeNode) {
                    return@onRightMouseButtonClicked
                }
                val selectedNode = nodes[nodes.size - 1] as FileTreeNode
                showEditMenu(e, selectedNode)
                Logger.d("PreviewPanel", selectedNode.toString())
            }
        }
        fileTree.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_DELETE) {
                    fileTree.getSelectedNodes(DefaultMutableTreeNode::class.java, null).forEach {
                        (it.userObject as? FileTreeNode)?.removeFromParent()
                        it.removeFromParent()
                    }
                    updateFileTreeUI()
                }
            }
        })
        fileTree.isEditable = true
        fileTree.cellRenderer = treeCellRenderer
    }

    private fun showEditMenu(anchor: MouseEvent, current: FileTreeNode) {
        val item = if (current.isDir) {
            mutableMapOf(
                    "New Directory" to {
                        FileDialog.showForCreate(current, true) { fileTreeNode: FileTreeNode ->
                            addTreeNode(current, fileTreeNode)
                        }
                    },
                    "New File" to {
                        FileDialog.showForCreate(current, false) { fileTreeNode: FileTreeNode ->
                            addTreeNode(current, fileTreeNode)
                        }
                    }
            )
        } else {
            mutableMapOf()
        }
        if (!current.isRoot()) {
            item["Rename"] = {
                FileDialog.showForRefactor(current) { fileTreeNode: FileTreeNode? ->
                    (fileTree.lastSelectedPathComponent as DefaultMutableTreeNode).userObject = fileTreeNode
                    updateFileTreeUI()
                }
            }
            item["Delete"] = {
                if (current.removeFromParent()) {
                    (fileTree.lastSelectedPathComponent as DefaultMutableTreeNode).removeFromParent()
                    ((fileTree.lastSelectedPathComponent as DefaultMutableTreeNode).userObject as? FileTreeNode)?.removeFromParent()
                    updateFileTreeUI()
                }
            }
        }
        PopMenuUtils.show(anchor, item)
    }

    private fun updateFileTreeUI() {
        fileTree.updateUI()
        onTreeUpdateListener?.invoke()
    }

    private fun addTreeNode(parent: FileTreeNode, node: FileTreeNode) {
        parent.addChild(node, true)
        module.template.addPlaceholders(node.placeholders.orEmpty())
        module.template.addFileTemplates(node.fileTemplates.orEmpty())
        node.fileTemplates?.clear()
        node.placeholders?.clear()
        (fileTree.lastSelectedPathComponent as DefaultMutableTreeNode).add(DefaultMutableTreeNode(node))
        updateFileTreeUI()
    }

    private fun getTreeModel(fileTreeNode: FileTreeNode): TreeModel {
        return TreeModel(getTree(fileTreeNode))
    }

    // convert FileTreeNode to JTree TreeNode
    private fun getTree(treeNode: FileTreeNode): DefaultMutableTreeNode {
        val result = DefaultMutableTreeNode(treeNode, true)
        if (treeNode.isDir) {
            treeNode.children.forEach(Consumer { i: FileTreeNode -> result.add(getTree(i)) })
        }
        return result
    }

    // expand all nodes
    private fun expandAll(tree: JTree, parent: TreePath, expand: Boolean) {
        val node = parent.lastPathComponent as TreeNode
        if (node.childCount >= 0) {
            val e = node.children()
            while (e.hasMoreElements()) {
                val n = e.nextElement() as TreeNode
                val path = parent.pathByAddingChild(n)
                expandAll(tree, path, expand)
            }
        }
        if (expand) {
            tree.expandPath(parent)
        } else {
            tree.collapsePath(parent)
        }
    }
}