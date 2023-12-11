package com.dengzii.plugin.template.model

class FileTreeDsl() : FileTreeNode() {

    constructor(block: FileTreeDsl.() -> Unit) : this() {
        invoke(block)
    }

    operator fun invoke(block: FileTreeDsl.() -> Unit): FileTreeDsl {
        this.block()
        return this
    }

    operator fun FileTreeNode.invoke(block: FileTreeNode.() -> Unit) {
        block(this)
    }

    /**
     * create directory nodes from the path
     * if contains '/' or '.' in path
     *
     * @param path The dir path
     * @param block The child node domain
     */
    fun FileTreeNode.dir(path: String, block: FileTreeNode.() -> Unit = {}) {
        if (!isDir) {
            this(block)
            return
        }
        val newNode = FileTreeNode(this, path, true)
        if (addChild(newNode)) {
            newNode.invoke(block)
        }
    }

    fun FileTreeNode.file(name: String): FileTreeNode {
        if (!isDir) throw IllegalStateException("Can not create file in a file.")

        name.getPlaceholder().forEach {
            if (getPlaceholderInherit()?.containsKey(it) == false) {
                placeholder(it, "")
            }
        }
        val r = FileTreeNode(this, name, false)
        addChild(r)
        return r
    }

    fun FileTreeNode.placeholder(name: String, value: String) {
        if (this.placeholders == null) {
            this.placeholders = mutableMapOf()
        }
        placeholders!![name] = value
    }

    /**
     * merge all children of another node to this.
     * all placeholders and file templates of target node
     * will be copied to it's each children
     */
    fun FileTreeNode.include(other: FileTreeNode, override: Boolean = false) {
        if (!isDir) return
        other.children.forEach {
            val includeChild = it.clone()
            if (other.placeholders != null) {
                if (includeChild.placeholders == null) {
                    includeChild.placeholders = mutableMapOf()
                }
                other.placeholders?.forEach { k, v ->
                    if (getFileTemplateInherit()?.containsKey(k) != true) {
                        includeChild.placeholders?.put(k, v)
                    }
                }
            }
            if (other.fileTemplates != null) {
                if (includeChild.fileTemplates == null) {
                    includeChild.fileTemplates = mutableMapOf()
                }
                other.fileTemplates?.forEach { k, v ->
                    if (fileTemplates?.containsKey(k) != true) {
                        includeChild.fileTemplates?.put(k, v)
                    }
                }
            }
            addChild(includeChild, override)
        }
    }

    fun FileTreeNode.fileTemplate(fileName: String, template: String) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
    }
}