package com.dengzii.plugin.template.model

class FileTreeDsl() : FileTreeNode() {

    constructor(block: FileTreeDsl.() -> Unit) : this() {
        invoke(block)
    }

    operator fun invoke(block: FileTreeDsl.() -> Unit): FileTreeDsl {
        this.block()
        return this
    }

    operator fun FileTreeNode.invoke(block: FileTreeNode.() -> Unit){
        block(this)
    }

    /**
     * create directory nodes from the path
     *
     * @param path The dir path
     * @param block The child node domain
     */
    fun FileTreeNode.dir(path: String, block: FileTreeNode.() -> Unit = {}) {
        if (!isDir) {
            this(block)
            return
        }
        var dirs = when {
            path.contains(".") -> path.split(".")
            path.contains("/") -> path.split("/")
            else -> {
                val newNode = FileTreeNode(this, path, true)
                if (addChild(newNode)) {
                    newNode(block)
                }
                return
            }
        }
        dirs = dirs.filter {
                    it.isNotBlank()
                }.toMutableList()
        val domain = createDirs(dirs, this)
        domain.invoke(block)
    }

    fun FileTreeNode.file(name: String) {
        if (!isDir) return
        addChild(FileTreeNode(this, name, false))
    }

    fun FileTreeNode.fileTemplate(fileName: String, template: String) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
    }
}