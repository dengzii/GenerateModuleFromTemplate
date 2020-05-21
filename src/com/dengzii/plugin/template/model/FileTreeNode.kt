@file:Suppress("unused")

package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.utils.Logger
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
open class FileTreeNode() {

    var name: String = ""

    var isDir = true
    var children
        set(value) {
            realChildren = value
            realChildren.forEach { labeledChildren[it.getLabel()] = it }
        }
        get() = realChildren

    var placeholders: MutableMap<String, String>? = null
    // all placeholder in tree node name
    val allPlaceholder by lazy { mutableListOf<String>() }

    // template for node, higher priority than fileTemplates
    private var template: String? = null

    // template of filename
    var fileTemplates: MutableMap<String, String>? = null

    private var realChildren = mutableSetOf<FileTreeNode>()

    // the label composed by 'name' and 'isDir'.
    @Transient
    private val labeledChildren = mutableMapOf<String, FileTreeNode>()
    @Transient
    var parent: FileTreeNode? = null

    companion object {

        var packageNameToDirs = true
        private val TAG = FileTreeNode::class.java.simpleName
        private val sPathSplitPattern = Pattern.compile("[./]")
        private val sPlaceholderPattern = Pattern.compile("\\$\\{([A-Za-z0-9_\\-]+)}")

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

    constructor(parent: FileTreeNode?, name: String, isDir: Boolean) : this() {
        this.name = name
        this.parent = parent
        this.isDir = isDir
        name.getPlaceholder().ifNotEmpty {
            allPlaceholder.addAll(this)
        }
    }

    fun removeFromParent(): Boolean {
        if (parent != null) {
            parent!!.labeledChildren.remove(getLabel())
            parent!!.realChildren.remove(this)
            parent = null
            return true
        }
        return false
    }

    /**
     * add child to this node
     *
     * @param child The child need be add
     * @param override Weather override the node with the same name and type
     */
    fun addChild(child: FileTreeNode, override: Boolean = false): Boolean {
        if (hasChild(child.getLabel())) {
            if (override) {
                Logger.d(TAG, "node has already exists $child")
                return false
            } else {
                Logger.w(TAG, "THE SAME CHILE ALREADY EXISTS IN $name: ${child.name}")
            }
        }
        child.parent = this
        realChildren.add(child)
        labeledChildren[child.getLabel()] = child
        return true
    }

    fun hasChild(name: String, isDir: Boolean): Boolean {
        realChildren.forEach {
            if (it.name == name && isDir == it.isDir) {
                return true
            }
        }
        return false
    }

    fun hasChild(label: String): Boolean {
        return labeledChildren.containsKey(label)
    }

    fun getChild(name: String, isDir: Boolean): FileTreeNode? {
        return labeledChildren["${name}_$isDir"]
    }

    /**
     * get the real name replace with placeholder
     */
    fun getRealName(): String {
        return getRealName(this.name)
    }

    fun getRealName(fileName: String = this.name): String {
        return fileName.replacePlaceholder(getPlaceholderInherit(), !isDir)
    }

    fun getFileTemplateInherit(): MutableMap<String, String>? {
        return fileTemplates ?: parent?.getFileTemplateInherit()
    }

    fun getPlaceholderInherit(): MutableMap<String, String>? {
        return placeholders ?: parent?.getPlaceholderInherit()
    }

    fun getTemplateFile(): String? {
        return template ?: getFileTemplateInherit()?.get(name)
    }

    fun setTemplate(name: String) {
        template = name
    }

    fun addPlaceholders(placeholders: Map<String, String>) {
        if (this.placeholders == null) {
            this.placeholders = mutableMapOf()
        }
        this.placeholders!!.putAll(placeholders)
    }


    fun addFileTemplates(placeholders: Map<String, String>) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!!.putAll(placeholders)
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

        realChildren.forEach {
            block(it, depth)
            it.traversal(block, depth + 1)
        }
    }

    /**
     * create directories tree from a list
     * the larger the index, the deeper the directory
     *
     * @param dirs The dirs list to create tree
     * @param parent The parent of current node
     */
    fun createDirs(dirs: MutableList<String>, parent: FileTreeNode): FileTreeNode {
        if (dirs.isEmpty()) {
            return parent
        }
        // the first dir
        val first = dirs.first()
        dirs.removeAt(0)
        val find = parent.getChild(first, true)
        if (find != null) {
            return createDirs(dirs, find)
        }
        val newNode = FileTreeNode(parent, first, true)
        parent.addChild(newNode)
        // create child dir
        return createDirs(dirs, newNode)
    }

    /**
     * get path of current node.
     * if the current node is the root node, it will return absolute path,
     * otherwise return relative path.
     *
     * @return The intact path of current node
     */
    fun getPath(): String {
        if (isRoot() || parent == null || parent!!.getRealName() == "") {
            return getRealName()
        }
        return parent!!.getPath() + "/" + getRealName()
    }

    fun isRoot(): Boolean {
        return this == parent || parent == null
    }

    /**
     * build file tree
     * replace placeholder in name
     * if this is directory and name is a path or package name
     * the name will expand to several dirs
     */
    fun build() {

        if (!isDir) {
            return
        }
        name = getRealName()
        if (!isRoot()) {
            val dir = if (packageNameToDirs) {
                name.split(sPathSplitPattern)
            } else {
                name.split("/")
            }.filter {
                it.isNotBlank()
            }.toMutableList()

            expandDirs(dir)
        }
        realChildren.forEach {
            it.build()
        }
    }

    /**
     * expand the dir list
     * all the children of this will move the new dir
     */
    private fun expandDirs(dirs: MutableList<String>) {
        if (dirs.isEmpty() || dirs.size == 1) {
            return
        }
        this.name = dirs.first()
        dirs.removeAt(0)

        var preNode: FileTreeNode = this
        var newChild: FileTreeNode

        val oldChildren = realChildren.toMutableList()
        oldChildren.forEach {
            it.removeFromParent()
        }
        dirs.forEach {
            newChild = FileTreeNode(preNode, it, true)
            preNode.addChild(newChild)
            preNode = newChild
        }
        oldChildren.forEach {
            preNode.addChild(it)
        }
    }

    fun getAllPlaceholderInTree(): List<String> {
        val result = mutableListOf<String>()
        result.addAll(allPlaceholder)
        traversal({ fileTreeNode: FileTreeNode, _: Int ->
            result.addAll(fileTreeNode.allPlaceholder)
        })
        return result
    }

    fun getAllPlaceholdersMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        result.putAll(placeholders.orEmpty())
        traversal({ fileTreeNode: FileTreeNode, _: Int ->
            if (fileTreeNode.placeholders != null) {
                result.putAll(fileTreeNode.placeholders!!)
            }
        })
        return result
    }

    /**
     * get the tree graph of node inherit
     *
     * @return The tree graph of node
     */
    fun getTreeGraph(): String {
        return getNodeGraph().toString()
    }

    /**
     * clone current node, the following fields will be copied:
     * name, isDir, fileTemplates, placeHolderMap, children
     *
     * the parent will not be cloned
     */
    fun clone(): FileTreeNode {
        val clone = FileTreeNode(null, name, isDir)
        clone.fileTemplates = fileTemplates?.toMutableMap()
        clone.placeholders = placeholders?.toMutableMap()
        children.forEach {
            clone.addChild(it.clone())
        }
        return clone
    }

    private fun removeChild(label: String): FileTreeNode? {
        if (hasChild(label)) {
            children.remove(labeledChildren[label])
            return labeledChildren.remove(label)
        }
        return null
    }

    private fun getNodeGraph(head: Stack<String> = Stack(), str: StringBuilder = StringBuilder()): StringBuilder {

        head.forEach {
            str.append(it)
        }
        str.append(when (this) {
            parent?.children?.last() -> "└─"
            parent?.children?.first() -> "├─"
            else -> {
                when {
                    parent != null -> "├─"
                    else -> ""
                }
            }
        })
        str.append(getRealName()).append("\n")

        if (!children.isNullOrEmpty()) {
            head.push(when {
                parent == null -> ""
                parent?.children?.last() != this -> "│\t"
                else -> "\t"
            })
            children.forEach {
                str.append(it.getNodeGraph(head))
            }
            head.pop()
        }
        return str
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

    private fun getLabel(): String {
        return "${name}_$isDir"
    }

    override fun toString(): String {
        return "FileTreeNode(path='${getPath()}' isDir=$isDir, fileTemplate=${getTemplateFile()}, children=${children.size})"
    }

    protected fun String.getPlaceholder(): List<String> {
        val result = mutableListOf<String>()
        val nameMatcher = sPlaceholderPattern.matcher(this)
        while (nameMatcher.find()) {
            result.add(nameMatcher.group(1))
        }
        return result
    }

    private fun String.replacePlaceholder(placeholders: Map<String, String>?, capitalize: Boolean = false): String {
        var after = this
        if (placeholders.isNullOrEmpty()) {
            return this
        }
        placeholders.forEach { (k, v) ->
            val replaced = if (capitalize) {
                v.toLowerCase().capitalize()
            } else {
                v
            }
            after = after.replace("\${$k}", replaced)
        }
        return after
    }
}