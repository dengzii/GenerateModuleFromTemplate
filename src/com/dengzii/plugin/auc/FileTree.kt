package com.dengzii.plugin.auc

import java.io.File

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/1/1
 * desc   :
</pre> */
class FileTree private constructor() {

    var name = ""
    var isDir = true
    private var parent: FileTree? = null
    private val children by lazy { mutableListOf<FileTree>() }

    companion object {
        private val TAG = FileTree::class.java.simpleName

        fun root(path: String): FileTree {
            val root = File(path)
            if (!root.isDirectory) {
                throw RuntimeException("The root must be a directory.")
            }
            return with(FileTree(null, path, true)) {
                this.parent = this
                this
            }
        }
    }

    constructor(block: FileTree.() -> Unit) : this() {
        invoke(block)
    }

    constructor(parent: FileTree?, name: String, isDir: Boolean) : this() {
        this.name = name
        this.parent = parent
        this.isDir = isDir
    }

    operator fun invoke(block: FileTree.() -> Unit): FileTree {
        this.block()
        return this
    }

    fun setRoot(path: String): FileTree {
        if (!File(path).isDirectory) {
            throw RuntimeException("The root must be a directory.")
        }
        parent = this
        name = path
        return this
    }

    fun create() {
        if (!isRoot()) {
            throw RuntimeException("Must create structure from root node.")
        }
        createChild()
    }

    fun forEach(block: (it: FileTree) -> Unit) {
        children.forEach(block)
    }

    fun dir(name: String, block: FileTree.() -> Unit = {}) {
        val dir = FileTree(this, name, true)
        children.add(dir)
        dir(block)
    }

    fun file(name: String) {
        children.add(FileTree(this, name, false))
    }

    fun getPath(): String {
        if (isRoot() || parent == null) {
            return name
        }
        return parent!!.getPath() + File.separator + name
    }

    private fun isRoot(): Boolean {
        return this == parent
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
}