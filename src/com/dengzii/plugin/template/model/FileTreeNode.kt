package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.template.Placeholder
import com.dengzii.plugin.template.template.replacePlaceholder
import com.dengzii.plugin.template.utils.Logger
import java.io.File

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
open class FileTreeNode private constructor() {

    var name: String
        get() = realName.replacePlaceholder(placeHolderMap)
        set(value) {
            realName = value
        }

    var isDir = true
    val children by lazy { mutableSetOf<FileTreeNode>() }
    var placeHolderMap: MutableMap<String, String>? = null
        get() = field ?: parent?.placeHolderMap

    // template for node, higher priority than fileTemplates
    var template: String? = null

    // template of filename
    var fileTemplates: MutableMap<String, String>? = null
        get() = field ?: parent?.fileTemplates

    // the origin name with original placeholder
    private var realName: String = ""
    private var parent: FileTreeNode? = null

    companion object {

        private val TAG = FileTreeNode::class.java.simpleName

        fun root(path: String): FileTreeNode {
            val root = File(path)
            if (!root.isDirectory) {
                throw RuntimeException("The root must be a directory.")
            }
            return with(FileTreeNode(null, path, true)) {
                this.parent = this
                this
            }
        }
    }

    constructor(block: FileTreeNode.() -> Unit) : this() {
        invoke(block)
    }

    constructor(parent: FileTreeNode?, name: String, isDir: Boolean) : this() {
        this.name = name
        this.parent = parent
        this.isDir = isDir
    }

    operator fun invoke(block: FileTreeNode.() -> Unit): FileTreeNode {
        this.block()
        return this
    }

    fun getParent(): FileTreeNode? {
        return parent
    }

    fun fileTemplate(fileName: String, template: String) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
    }

    fun hasTemplate(): Boolean {
        return template != null || fileTemplates?.containsKey(realName) == true
    }

    fun getTemplateName(): String? {
        return template ?: fileTemplates?.get(realName)
    }

    fun placeholder(placeholder: Placeholder, value: String) {
        if (this.placeHolderMap == null) {
            this.placeHolderMap = kotlin.collections.mutableMapOf()
        }
        placeHolderMap!![placeholder.getPlaceholder()] = value
    }

    /**
     * set current node as root.
     * the file of specified path must be a directory and exist.
     * the root's 'name' is the root path of entire file tree.
     *
     * @param path: The root directory path of whole file tree
     */
    fun setRoot(path: String): FileTreeNode {
        if (!File(path).isDirectory) {
            throw RuntimeException("The root must be a directory.")
        }
        parent = this
        name = path
        return this
    }

    /**
     * traversal and create file tree, must be called from the root node.
     * the existing files will be skipped
     */
    fun create() {
        if (!isRoot()) {
            throw RuntimeException("Must create structure from root node.")
        }
        createChild()
    }

    /**
     * traverse and call back each node of the entire file tree.
     */
    fun traversal(block: (it: FileTreeNode, depth: Int) -> Unit, depth: Int = 0) {
        if (!isDir) return

        children.forEach {
            block(it, depth)
            it.traversal(block, depth + 1)
        }
    }

    /**
     * merge all children of another node to 'this.children'.
     */
    fun include(other: FileTreeNode) {
        if (!isDir) return
        other.children.forEach {
            val child = it.clone()
            child.parent = this
            children.add(child)
        }
    }

    fun dir(path: String, block: FileTreeNode.() -> Unit = {}) {
        if (!isDir) return
        val dirs = path.split("/").filter { it.isNotBlank() }.toMutableList()
        createDirs(dirs, this)(block)
    }

    private fun createDirs(dirs: MutableList<String>, parent: FileTreeNode): FileTreeNode {
        if (dirs.isEmpty()) {
            return parent
        }
        val current = dirs[0]
        dirs.removeAt(0)
        val dirNode = FileTreeNode(parent, current, true)
        parent.children.add(dirNode)
        return createDirs(dirs, dirNode)
    }

    fun file(name: String) {
        if (!isDir) return
        children.add(FileTreeNode(this, name, false))
    }

    /**
     * get path of current node.
     * if the current node is the root node, it will return absolute path,
     * otherwise return relative path.
     */
    fun getPath(): String {
        if (isRoot() || parent == null || parent!!.name == "") {
            return name
        }
        return parent!!.getPath() + "/" + name
    }


    fun isRoot(): Boolean {
        return this == parent
    }

    fun getTreeGraph(): String {
        val strBuilder = StringBuilder()
        var line = 0x1
        traversal({ i, dep ->
            val head = if (i.isRoot()) "┌" else if (children.last() == i) "└" else "├"
            strBuilder.append("│   ".repeat(dep) + "$head─" + i.name).append("\n")
        })
        return strBuilder.toString()
    }

    fun clone(): FileTreeNode {
        val cl = FileTreeNode(null, name, isDir)
        cl.fileTemplates = fileTemplates?.toMutableMap()
        cl.placeHolderMap = placeHolderMap?.toMutableMap()
        children.forEach {
            val child = it.clone()
            child.parent = cl
            cl.children.add(child)
        }
        return cl
    }


    private fun getNodeGraph(head: StringBuilder = StringBuilder()): StringBuilder {
        val graph = StringBuilder(head)

        graph.append(when (this) {
            parent?.children?.last() -> "└─"
            parent?.children?.first() -> "├─"
            else -> "┌─"
        })

        graph.append(name).append("\n")
        if (isDir && !children.isNullOrEmpty()) {
            for (i in 0 until children.size) {

            }
        }
        return graph
    }

    private fun createChild() {
        children.forEach {
            val file = File(it.getPath())
            if (file.exists()) {
                Logger.d(TAG, "${file.absolutePath} already exists.")
            } else {
                Logger.d(TAG, "create ${file.absolutePath}")
                if (it.isDir) {
                    file.mkdir()
                } else {
                    file.createNewFile()
                }
            }
            if (it.isDir) {
                it.createChild()
            }
        }
    }

    override fun toString(): String {
        return "FileTreeNode(path='${getPath()}')"
    }
}