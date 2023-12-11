@file:Suppress("unused")

package com.dengzii.plugin.template.model

import com.dengzii.plugin.template.utils.Logger
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.File
import java.io.StringWriter
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

    @Transient
    private var module: Module? = null

    @Transient
    var context: VelocityContext? = null

    companion object {

        private val TAG = FileTreeNode::class.java.simpleName
        private val sPathSplitPattern = Pattern.compile("/")
        private val sPkgSplitPattern = Pattern.compile("\\.")
        private val sPlaceholderPattern = Pattern.compile("\\$\\{([A-Za-z\\d_\\-]+)}")

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
    }

    fun init(module: Module) {
        this.module = module
        children.forEach {
            it.parent = this
            if (it.isDir) {
                it.init(module)
            }
        }
    }

    private fun getTreeNodeCount(): Int {
        var count = realChildren.size
        realChildren.forEach {
            count += it.getTreeNodeCount()
        }
        return count
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

    private fun hasChild(label: String): Boolean {
        return labeledChildren.containsKey(label)
    }

    private fun getChild(name: String, isDir: Boolean): FileTreeNode? {
        return labeledChildren["${name}_$isDir"]
    }

    /**
     * get the real name replace with placeholder
     */
    fun getRealName(velocityContext: VelocityContext? = null): String {
        return getRealNameInternal(velocityContext, this.name)
    }

    private fun getRealNameInternal(
        velocityContext: VelocityContext? = getContextInherit(),
        fileName: String = this.name,
    ): String {
        return if (isDir) {
            val rn = replacePlaceholder(velocityContext, fileName, getPlaceholderInherit(), false)
            if (getModuleInherit()?.lowercaseDir == true) rn.lowercase() else rn
        } else {
            val capitalize = getModuleInherit()?.capitalizeFile ?: false
            replacePlaceholder(velocityContext, fileName, getPlaceholderInherit(), capitalize)
        }
    }

    fun getPlaceholderInNodeName() = name.getPlaceholder()

    fun removeAllTemplateInTree() {
        fileTemplates?.clear()
        template = null
        traversal({ it, _ ->
            it.removeAllTemplateInTree()
        })
    }

    fun removeAllPlaceHolderInTree() {
        placeholders?.clear()
        traversal({ it, _ ->
            it.removeAllPlaceHolderInTree()
        })
    }

    /**
     * Return all file template in tree node
     */
    fun getAllTemplateMap(): Map<String, String> {
        val templates = fileTemplates.orEmpty().toMutableMap()
        realChildren.forEach {
            templates.putAll(it.getAllTemplateMap())
        }
        return templates
    }

    private fun getNodeHasTemplateInTree(): List<FileTreeNode> {
        val nodes = mutableListOf<FileTreeNode>()
        if (fileTemplates != null) {
            nodes.add(this)
        }
        realChildren.forEach {
            nodes.addAll(it.getNodeHasTemplateInTree())
        }
        return nodes
    }

    private fun getContextInherit(): VelocityContext? {
        return if (context == null) {
            parent?.getContextInherit()
        } else {
            context
        }
    }

    fun getFileTemplateInherit(): MutableMap<String, String>? {
        return if (fileTemplates.isNullOrEmpty()) {
            return parent?.getFileTemplateInherit()
        } else {
            fileTemplates
        }
    }

    fun getPlaceholderInherit(): MutableMap<String, String>? {
        return if (placeholders.isNullOrEmpty()) {
            return parent?.getPlaceholderInherit()
        } else {
            placeholders
        }
    }

    /**
     *  Resolve all file template file name in tree node.
     */
    fun resolveFileTemplate(context: VelocityContext? = getContextInherit()) {
        val templates = getAllTemplateMap()
        val placeholders = getPlaceholderInherit() ?: return
        if (fileTemplates != null && templates.isNotEmpty()) {
            templates.forEach { (key, value) ->
                val realName = getRealNameInternal(context, key)
                val realValue = replacePlaceholder(context, value, placeholders, false)
                fileTemplates!![realName] = realValue
            }
        }
        traversal({ it, _ ->
            it.resolveFileTemplate(context)
        })
    }

    /**
     *  Return the most matching file template.
     *  If the node has template, return it, otherwise return the template in parent node.
     */
    fun getTemplateFile(): String? {
        if (template != null) {
            return template
        }
        val tpl = getFileTemplateInherit() ?: return null
        val path = getPath()
        for (entry in tpl) {
            if (entry.key != name && path.endsWith(entry.key)) {
                return entry.value
            }
        }
        return tpl[name]
    }

    fun setTemplate(name: String) {
        template = name
    }

    fun putPlaceholders(placeholders: Map<String, String>) {
        if (this.placeholders == null) {
            this.placeholders = mutableMapOf()
        }
        this.placeholders!!.putAll(placeholders)
    }

    fun putPlaceholders(placeholders: Array<String>) {
        putPlaceholders(placeholders.associateWith { (this.placeholders?.getOrDefault(it, "") ?: "") })
    }

    /**
     * Add placeholders with empty value.
     */
    fun addFileTemplate(fileName: String, template: String) {
        if (fileTemplates == null) {
            fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
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
    private fun traversal(block: (it: FileTreeNode, depth: Int) -> Unit, depth: Int = 0) {
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
    private fun createDirs(dirs: MutableList<String>, parent: FileTreeNode): FileTreeNode {
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
     * expand path name to dir structure
     */
    fun expandPath() {
        if (!isDir) {
            return
        }
        if (!isRoot()) {
            val dir = name.split("/")
                .filter {
                    it.isNotBlank()
                }
                .toMutableList()
            expandDirs(dir)
        }
        realChildren.forEach {
            it.expandPath()
        }
    }

    /**
     * expand the package name to dir structure, eg: `com.example.app` to `com/example/app`
     */
    fun expandPkgName(replacePlaceholder: Boolean = true) {
        if (!isDir) {
            return
        }
        val pkgName2Dir = getModuleInherit()?.packageNameToDir ?: false
        if (!pkgName2Dir) {
            return
        }
        if (replacePlaceholder) {
            name = getRealName()
        }
        if (!isRoot()) {
            val dir = name
                .split(sPkgSplitPattern)
                .filter {
                    it.isNotBlank()
                }
                .toMutableList()
            expandDirs(dir)
        }
        realChildren.forEach {
            it.expandPkgName(replacePlaceholder)
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

    /**
     * Return the placeholder key/replacement map of the node tree.
     */
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
    fun getTreeGraph(
        context: VelocityContext? = null,
        templateFile: Boolean = false,
        resolveName: Boolean = true,
    ): String {
        return getNodeGraph(context, templateFile = templateFile, resolveName = resolveName).toString()
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
        clone.module = module
        realChildren.forEach {
            clone.addChild(it.clone())
        }
        return clone
    }

    private fun removeChild(label: String): FileTreeNode? {
        if (hasChild(label)) {
            realChildren.remove(labeledChildren[label])
            return labeledChildren.remove(label)
        }
        return null
    }

    private fun getNodeGraph(
        context: VelocityContext?,
        head: Stack<String> = Stack(),
        str: StringBuilder = StringBuilder(),
        templateFile: Boolean = false,
        resolveName: Boolean = true,
    ): StringBuilder {

        head.forEach {
            str.append(it)
        }
        str.append(
            when (this) {
                parent?.realChildren?.last() -> "└─"
                parent?.realChildren?.first() -> "├─"
                else -> {
                    when {
                        parent != null -> "├─"
                        else -> ""
                    }
                }
            }
        )
        val n = if (resolveName) getRealName(context) else name

        if (isDir || !templateFile) {
            str.append(n)
        } else {
            str.append("$n  ${getTemplateFile()}")
        }
        str.append("\n")

        if (realChildren.isNotEmpty()) {
            head.push(
                when {
                    parent == null -> ""
                    parent?.realChildren?.last() != this -> "│\t"
                    else -> "\t"
                }
            )
            realChildren.forEach {
                str.append(it.getNodeGraph(context, head, StringBuilder(), templateFile))
            }
            head.pop()
        }
        return str
    }

    private fun createChild() {
        realChildren.forEach {
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

    private fun getModuleInherit(): Module? {
        return module ?: parent?.getModuleInherit()
    }

    private fun getLabel(): String {
        return "${name}_$isDir"
    }

    override fun toString(): String {
        return "FileTreeNode(path='${getPath()}' isDir=$isDir, fileTemplate=${getTemplateFile()}, children=${children.size})"
    }

    protected fun String.getPlaceholder(): List<String> {
        val result = mutableListOf<String>()
        try {
            val nameMatcher = sPlaceholderPattern.matcher(this)
            while (nameMatcher.find()) {
                result.add(nameMatcher.group(1))
            }
        } catch (e: Exception) {
            Logger.e("FileTreeNode", e)
        }
        return result
    }

    private fun replacePlaceholder(
        velocityContext: VelocityContext? = null,
        origin: String,
        placeholders: Map<String, String>?,
        capitalize: Boolean = false
    ): String {
        if (velocityContext != null) {
            val writer = StringWriter()
            Velocity.evaluate(velocityContext, writer, "FileTreeNode", origin)
            return writer.toString()
        }

        var after = origin
        if (placeholders.isNullOrEmpty()) {
            return origin
        }
        placeholders.forEach { (k, v) ->
            var replacement = v
            if (capitalize) {
                replacement = v.lowercase(Locale.getDefault())
                    .replaceFirstChar {
                        it.toString().uppercase()
                    }
            }
            after = after.replace("\${$k}", replacement)
        }
        return if (after == origin || !after.contains('$')) {
            after
        } else {
            replacePlaceholder(velocityContext, after, placeholders, capitalize)
        }
    }
}