package com.dengzii.plugin.template.model

class FileTreeDsl() : FileTreeNode() {

    constructor(block: FileTreeDsl.() -> Unit) : this() {
        invoke(block)
    }

    operator fun invoke(block: FileTreeDsl.() -> Unit): FileTreeDsl {
        this.block()
        return this
    }

    constructor(parent: FileTreeDsl?, name: String, isDir: Boolean) : this() {
        this.name = name
        this.parent = parent
        this.isDir = isDir
    }

    /**
     * create directory nodes from the path
     *
     * @param path The dir path
     * @param block The child node domain
     */
    fun dir(path: String, block: FileTreeDsl.() -> Unit = {}) {
        if (!isDir) {
            this(block)
            return
        }
        var dirs = when {
            path.contains(".") -> path.split(".")
            path.contains("/") -> path.split("/")
            else -> {
                val newNode = FileTreeDsl(this, path, true)
                if (addChild(newNode)) {
                    newNode(block)
                }
                return
            }
        }
        dirs = dirs
                .filter {
                    it.isNotBlank()
                }.toMutableList()
        createDirs(dirs, this)(block)
    }

    fun file(name: String) {
        if (!isDir) return
        addChild(FileTreeNode(this, name, false))
    }

    fun fileTemplate(fileName: String, template: String) {
        if (this.fileTemplates == null) {
            this.fileTemplates = mutableMapOf()
        }
        fileTemplates!![fileName] = template
    }

    private fun createDirs(dirs: MutableList<String>, parent: FileTreeDsl): FileTreeNode {
        if (dirs.isEmpty()) {
            return parent
        }
        // the first dir
        val first = dirs[0]
        dirs.removeAt(0)
        val firstDir = FileTreeDsl(parent, first, true)
        val findChild = getChild(first, true)
        if (findChild != null) {
            return createDirs(dirs, findChild)
        }
        addChild(firstDir)
        // create child dir
        return createDirs(dirs, firstDir)
    }
}