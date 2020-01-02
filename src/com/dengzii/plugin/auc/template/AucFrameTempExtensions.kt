@file:Suppress("unused", "SpellCheckingInspection")

package com.dengzii.plugin.auc.template

import com.dengzii.plugin.auc.FileTreeNode

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2020/1/2
 * desc   :
 * </pre>
 */
class FileNode(parent: FileTreeNode?, name: String) : FileTreeNode(parent, name, false)

typealias AucFrame = FileTreeNode

typealias ChildNodeBlock = (parent: Node) -> Unit

typealias Node = FileTreeNode

val Node.test: Node get() = dirNode("test")
val Node.main: Node get() = dirNode("main")
val Node.java: Node get() = dirNode("java")
val Node.src: Node get() = dirNode("src")
val Node.res: Node get() = dirNode("res")
val Node.app: Node get() = dirNode("app")
val Node.pkg_name: Node get() = dirNode(Placeholder.PACKAGE_NAME.getPlaceholder())
val Node.layout: Node get() = dirNode("layout")
val Node.values: Node get() = dirNode("values")
val Node.drawable: Node get() = dirNode("drawable")
val Node.mipmap: Node get() = dirNode("mipmap")

val Node.AndroidManifest: FileNode get() = fileNode("MainActivity")
val Node.proguard_rules: FileNode get() = fileNode("proguard-rules")
val Node.gitignore: FileNode get() = fileNode(".gitignore")
val Node.build: FileNode get() = fileNode("build")
val Node.App: FileNode get() = fileNode("App")

val FileNode.gradle: Unit get() = nodeSuffix(".gradle")
val FileNode.java: Unit get() = nodeSuffix(".java")
val FileNode.pro: Unit get() = nodeSuffix(".pro")
val FileNode.xml: Unit get() = nodeSuffix(".xml")
val FileNode.kt: Unit get() = nodeSuffix(".kt")

private fun Node.dirNode(name: String): Node {
    val dirNode = Node(this, name, true)
    this.children.add(dirNode)
    return dirNode
}

private fun Node.fileNode(name: String): FileNode {
    val node = FileNode(this, name)
    children.add(node)
    return node
}

private fun FileNode.nodeSuffix(suffix: String) {
    this.name = this.name + suffix
}