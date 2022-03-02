package test

import com.dengzii.plugin.template.model.FileTreeDsl
import org.junit.Test

class FileTreeDslTest {

    @Test
    fun createSimpleFileTreeTest() {
        val tree = FileTreeDsl {
            file("file")
            dir("dir1") {
                file("file2")
                dir("dir2") {
                    dir("dir3") {
                        dir("dir4")
                    }
                    dir("dir5") {
                        dir("dir6")
                        dir("dir9")
                    }
                }
                file("file3")
            }
            dir("dir7") {
                dir("dir8")
            }
        }
        println(tree.getTreeGraph())
    }

    @Test
    fun createPackageDirTest() {

        val tree = FileTreeDsl {
            dir("src") {
                dir("com/example/app") {
                    file("Main.java")
                }
                dir("com/example/app1")
            }
            file("README.md")
        }
        println(tree.getTreeGraph())
    }


    @Test
    fun fileNamePlaceholderTest() {
        val tree = FileTreeDsl {
            placeholder("FILE_1", "first_file")
            placeholder("FILE_2", "second_file")
            file("\${FILE_1}.java")
            dir("com/example") {
                println(name)
                file("\${FILE_2}.java")
            }
        }
        println(tree.getTreeGraph())
    }

    @Test
    fun expandDirectoriesTest() {
        val tree = FileTreeDsl {
            dir("src") {
                dir("com.dengzii.plugin") {
                    dir("model")
                    dir("template")
                }
                dir("test")
            }
        }
        tree.expandPath()
        println(tree.getTreeGraph())

        val tree2 = FileTreeDsl {
            dir("src") {
                dir("com.dengzii.plugin") {
                    dir("dir1/dir2/") {
                        dir("model")
                        dir("template")
                    }
                    dir("model")
                    dir("template")
                }
                dir("test")
            }
        }
        tree2.expandPath()
        println(tree2.getTreeGraph())
    }

    @Test
    fun expandDirectoriesInPlaceholderTest() {

        val tree = FileTreeDsl {
            placeholder("PACKAGE_NAME", "com.dengzii.plugin")
            dir("src") {
                dir("\${PACKAGE_NAME}") {
                    dir("model")
                    dir("template")
                }
                dir("test")
            }
        }
        tree.expandPath()
        println(tree.getTreeGraph())
    }

    @Test
    fun getAllPlaceholderInTreeNodeNameTest() {
        val tree = FileTreeDsl {
            placeholder("PACKAGE_NAME", "com.dengzii.plugin")
            dir("src") {
                dir("\${PACKAGE_NAME}") {
                    dir("model")
                    dir("template"){
                        file("\${TEST}")
                        file("\${TEST2}")
                    }
                }
                dir("test")
            }
        }
//        println(tree.getAllPlaceholderInTree())
    }
}