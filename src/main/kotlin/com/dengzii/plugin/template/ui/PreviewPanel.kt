package com.dengzii.plugin.template.ui

import com.dengzii.plugin.template.model.FileTreeNode
import com.dengzii.plugin.template.model.Module
import com.dengzii.plugin.template.tools.ui.PopMenuUtils
import com.dengzii.plugin.template.tools.ui.onRightMouseButtonClicked
import com.dengzii.plugin.template.utils.Logger
import com.dengzii.plugin.ui.FileDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.packageDependencies.ui.TreeModel
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import org.apache.velocity.VelocityContext
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.function.Consumer
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class PreviewPanel(preview: Boolean) : JPanel() {

    private val fileTree: Tree = Tree()
    private val showPlaceholder: JBCheckBox = JBCheckBox("Show placeholders")

    private lateinit var module: Module
    private var replacePlaceholder = true
    private var onTreeUpdateListener: (() -> Unit)? = null
    private var onPlaceholderUpdateListener: (() -> Unit)? = null

    private var context: VelocityContext = VelocityContext().apply {
        put("StringUtils", org.apache.velocity.util.StringUtils::class.java)
    }

    // render tree node icon, title
    private val treeCellRenderer = object : ColoredTreeCellRenderer() {
        override fun customizeCellRenderer(
            jTree: JTree, value: Any, b: Boolean, b1: Boolean, b2: Boolean, i: Int, b3: Boolean
        ) {
            if (value is DefaultMutableTreeNode) {
                val node = value.userObject
                if (node is FileTreeNode) {
                    val name = if (replacePlaceholder) {
                        node.getRealName(context)
                    } else {
                        node.name
                    }
                    this.append(name)
                    icon = if (node.isDir) {
                        AllIcons.Nodes.Package
                    } else {
                        getIconByFileName(name)
                    }
                }
            }
        }
    }

    init {
        layout = BorderLayout()
        add(JBScrollPane().apply {
            setViewportView(fileTree)
        }, BorderLayout.CENTER)
        showPlaceholder.isSelected = !replacePlaceholder
        if (preview) {
            add(showPlaceholder, BorderLayout.NORTH)
            showPlaceholder.addChangeListener {
                replacePlaceholder = !showPlaceholder.isSelected
                setModuleConfig(module)
            }
        }
        initPanel()
    }

    fun setPreviewMode(preview: Boolean) {
        if (preview != replacePlaceholder) {
            replacePlaceholder = preview
            showPlaceholder.isSelected = !replacePlaceholder
            fileTree.updateUI()
        }
    }

    fun onPlaceholderUpdate(l: (() -> Unit)?) {
        onPlaceholderUpdateListener = l
    }

    fun updateTree() {
        fileTree.updateUI()
    }

    fun setModuleConfig(module: Module) {
        Logger.i("PreviewPanel", "setModuleConfig")
        this.module = module
        fileTree.model = getTreeModel(this.module.template)
        context.apply {
            put("StringUtils", org.apache.velocity.util.StringUtils::class.java)
            module.template.getPlaceholderInherit()?.forEach {
                put(it.key, it.value)
            }
        }
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

    private fun getIconByFileName(fileName: String): Icon {
        return FileTypeManager.getInstance().getFileTypeByExtension(fileName.split(".").last()).icon
            ?: AllIcons.FileTypes.Text
    }

    /**
     * init Tree icon, node title, mouse listener
     */
    private fun initPanel() {
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
            val i = mutableMapOf("New Directory" to {
                FileDialog.showForCreate(current, true) { fileTreeNode: FileTreeNode ->
                    addTreeNode(current, fileTreeNode)
                }
            }, "New File" to {
                FileDialog.showForCreate(current, false) { fileTreeNode: FileTreeNode ->
                    addTreeNode(current, fileTreeNode)
                }
            })
            val parent = current.parent
            if (!current.isRoot() && current.children.isNotEmpty() && parent != null) {
                i["Remove This Node Only"] = {
                    current.removeFromParent()
                    for (child in current.children) {
                        addTreeNode(parent, child)
                    }
                    // TODO optimize update
                    setModuleConfig(module)
                }
            }
            i
        } else {
            mutableMapOf()
        }
        if (!current.isRoot()) {
            item["Rename"] = {
                val oldP = current.placeholders.orEmpty()
                val olds = current.placeholders?.keys?.joinToString()
                FileDialog.showForRefactor(current) { fileTreeNode: FileTreeNode ->
                    (fileTree.lastSelectedPathComponent as DefaultMutableTreeNode).userObject = fileTreeNode
                    updateFileTreeUI()
                    val newPlaceholder = fileTreeNode.getPlaceholderInNodeName()
                    if (olds != newPlaceholder.sorted().joinToString()) {
                        val appendPlaceholder = newPlaceholder.filter { !oldP.containsKey(it) }.associateWith { "" }
                        module.template.putPlaceholders(appendPlaceholder)
                        onPlaceholderUpdateListener?.invoke()
                    }
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
        module.template.putPlaceholders(node.getPlaceholderInNodeName().toTypedArray())
        module.template.putPlaceholders(node.placeholders.orEmpty())
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