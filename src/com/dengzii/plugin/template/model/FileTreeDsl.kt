package com.dengzii.plugin.template.model

import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty

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

    fun FileTreeNode.file(name: String) {
        if (!isDir) return
        name.getPlaceholder().ifNotEmpty {
            allPlaceholder.addAll(this)
        }
        addChild(FileTreeNode(this, name, false))
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
            val clone = it.clone()
            if (other.placeholders != null) {
                if (clone.placeholders == null) {
                    clone.placeholders = mutableMapOf()
                }
                clone.placeholders?.putAll(other.placeholders!!)
            }
            if (other.fileTemplates != null) {
                if (clone.fileTemplates == null) {
                    clone.fileTemplates = mutableMapOf()
                }
                clone.fileTemplates?.putAll(other.fileTemplates!!)
            }
            addChild(clone, override)
        }
    }

    fun FileTreeNode.fileTemplate(fileName: String, template: String) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
    }
}