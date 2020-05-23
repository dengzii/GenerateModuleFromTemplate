package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.model.FileTreeNode;
import com.dengzii.plugin.template.model.Module;
import com.dengzii.plugin.template.utils.Logger;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private static final String TAG = PreviewPanel.class.getSimpleName();

    private JPanel contentPanel;
    private Tree fileTree;

    private Map<String, Icon> fileIconMap = new HashMap<>();
    private boolean replacePlaceholder = true;

    // render tree node icon, title
    private TreeCellRenderer treeCellRenderer = new ColoredTreeCellRenderer() {
        @Override
        public void customizeCellRenderer(@NotNull JTree jTree, Object value, boolean b, boolean b1, boolean b2, int i, boolean b3) {
            if (value instanceof DefaultMutableTreeNode) {
                Object object = ((DefaultMutableTreeNode) value).getUserObject();
                if (object instanceof FileTreeNode) {
                    FileTreeNode node = (FileTreeNode) object;
                    setIcon(IconUtil.getAddClassIcon());
                    this.append(replacePlaceholder ? node.getRealName() : node.getName());
                    if (node.isDir()) {
                        setIcon(AllIcons.Nodes.Package);
                    } else {
                        String suffix = "";
                        if (node.getName().contains(".")) {
                            suffix = node.getName().substring(node.getName().lastIndexOf("."));
                        }
                        setIcon(fileIconMap.getOrDefault(suffix, AllIcons.FileTypes.Text));
                    }
                } else {
                    setIcon(AllIcons.FileTypes.Unknown);
                }
            }
        }
    };

    PreviewPanel() {
        setLayout(new BorderLayout());
        add(contentPanel);
        initPanel();
    }

    public void setReplacePlaceholder(boolean replace) {
        if (replace != replacePlaceholder) {
            replacePlaceholder = replace;
            fileTree.updateUI();
        }
    }

    public void setModuleConfigPreview(Module module) {
        Module clone = module.clone();
        clone.getTemplate().build();
        setModuleConfig(clone);
    }

    public void setModuleConfig(Module module) {
        Logger.INSTANCE.i("PreviewPanel", "setModuleConfig");

        FileTreeNode node = module.getTemplate();
        fileTree.setModel(getTreeModel(node));
        fileTree.doLayout();
        fileTree.updateUI();
        expandAll(fileTree, new TreePath(fileTree.getModel().getRoot()), true);
    }

    /**
     * init Tree icon, node title, mouse listener
     */
    private void initPanel() {
        fileIconMap.put(".java", AllIcons.Nodes.Class);
        fileIconMap.put(".kt", AllIcons.Nodes.Class);
        fileIconMap.put(".xml", AllIcons.FileTypes.Xml);
        fileIconMap.put(".gradle", AllIcons.FileTypes.Config);
        fileIconMap.put(".gitignore", AllIcons.FileTypes.Config);
        fileIconMap.put(".pro", AllIcons.FileTypes.Config);

        fileTree.addMouseListener(getTreeMouseListener());

        fileTree.setEditable(true);
        fileTree.setCellRenderer(treeCellRenderer);
    }

    /**
     * the JTree mouse listener use for edit tree
     *
     * @return the mouse adapter
     */
    private MouseAdapter getTreeMouseListener() {

        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != MouseEvent.BUTTON3) {
                    return;
                }
                int row = fileTree.getRowForLocation(e.getX(), e.getY());
                if (row != -1) {
                    DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent());
                    Object[] nodes = treeNode.getUserObjectPath();
                    // has selected node and the node is FileTreeNode
                    if (nodes.length == 0 || !(nodes[0] instanceof FileTreeNode)) {
                        return;
                    }
                    FileTreeNode selectedNode = ((FileTreeNode) nodes[nodes.length - 1]);
                    showEditMenu(e.getComponent(), selectedNode, e.getX(), e.getY());
                    Logger.INSTANCE.d(TAG, selectedNode.toString());
                }
            }
        };
    }

    private void showEditMenu(Component anchor, FileTreeNode current, int x, int y) {

        JBPopupMenu jbPopupMenu = new JBPopupMenu();
        if (current.isDir()) {
            jbPopupMenu.add(getMenuItem("New Directory", e -> {
                CreateFileDialog.showForCreate(current, true, fileTreeNode -> addTreeNode(current, fileTreeNode));
            }));
            jbPopupMenu.add(getMenuItem("New File", e -> {
                CreateFileDialog.showForCreate(current, false, fileTreeNode -> addTreeNode(current, fileTreeNode));
            }));
        }
        if (!current.isRoot()) {
            jbPopupMenu.add(getMenuItem("Rename", e -> {
                CreateFileDialog.showForRefactor(current, fileTreeNode -> {
                    ((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()).setUserObject(fileTreeNode);
                    fileTree.updateUI();
                });
            }));
            jbPopupMenu.add(getMenuItem("Delete", e -> {
                if (current.removeFromParent()) {
                    ((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()).removeFromParent();
                    fileTree.updateUI();
                }
            }));
        }

        jbPopupMenu.show(anchor, x, y);
    }

    private void addTreeNode(FileTreeNode parent, FileTreeNode node) {

        parent.getChildren().add(node);
        ((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()).add(new DefaultMutableTreeNode(node));
        fileTree.updateUI();
    }

    @NotNull
    private TreeModel getTreeModel(FileTreeNode fileTreeNode) {
        return new TreeModel(getTree(fileTreeNode));
    }

    // convert FileTreeNode to JTree TreeNode
    @NotNull
    private DefaultMutableTreeNode getTree(FileTreeNode treeNode) {
        DefaultMutableTreeNode result = new DefaultMutableTreeNode(treeNode, true);

        if (treeNode.isDir()) {
            treeNode.getChildren().forEach(i -> result.add(getTree(i)));
        }
        return result;
    }

    // expand all nodes
    private void expandAll(JTree tree, @NotNull TreePath parent, boolean expand) {
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

    private JBMenuItem getMenuItem(String title, ActionListener listener) {
        JBMenuItem jbMenuItem = new JBMenuItem(title);
        jbMenuItem.addActionListener(listener);
        return jbMenuItem;
    }
}
