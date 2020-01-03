package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.model.FileTreeNode;
import com.dengzii.plugin.template.model.ModuleConfig;
import com.intellij.icons.AllIcons;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/3
 * desc   :
 * </pre>
 */
public class PreviewPanel extends JPanel {

    private JPanel panel1;
    private JScrollPane scrollPane;
    private Tree fileTree;

    private Map<String, Icon> fileIconMap = new HashMap<>();

    PreviewPanel() {
        setLayout(new BorderLayout());
        add(panel1);
        initPanel();
    }

    private void initPanel() {
        fileIconMap.put(".java", AllIcons.Nodes.Class);
        fileIconMap.put(".kt", AllIcons.Nodes.Class);
        fileIconMap.put(".xml", AllIcons.FileTypes.Xml);
        fileIconMap.put(".gradle", AllIcons.FileTypes.Config);
        fileIconMap.put(".gitignore", AllIcons.FileTypes.Config);
        fileIconMap.put(".pro", AllIcons.FileTypes.Config);

        fileTree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object value, boolean b, boolean b1, boolean b2, int i, boolean b3) {
                if (value instanceof DefaultMutableTreeNode) {
                    Object object = ((DefaultMutableTreeNode) value).getUserObject();
                    if (object instanceof FileTreeNode) {
                        FileTreeNode node = (FileTreeNode) object;
                        setIcon(IconUtil.getAddClassIcon());
                        this.append(node.getName());
                        if (node.isDir()) {
                            setIcon(AllIcons.Nodes.Package);
                        } else {
                            String suffix = node.getName().substring(node.getName().lastIndexOf("."));
                            setIcon(fileIconMap.getOrDefault(suffix, AllIcons.FileTypes.Text));
                        }
                    }
                }
            }
        });
    }

    public void setModuleConfig(ModuleConfig moduleConfig) {
        TreeModel treeModel = new TreeModel(getTree(moduleConfig.getTemplate()));
        fileTree.setModel(treeModel);
        fileTree.doLayout();
        expandAll(fileTree, new TreePath(treeModel.getRoot()), true);
    }

    private DefaultMutableTreeNode getTree(FileTreeNode treeNode) {
        DefaultMutableTreeNode result = new DefaultMutableTreeNode(treeNode);

        if (treeNode.isDir()) {
            treeNode.getChildren().forEach(i -> result.add(getTree(i)));
        }
        return result;
    }

    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

}
