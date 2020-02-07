package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.model.FileTreeNode;
import com.dengzii.plugin.template.utils.PluginKit;
import com.intellij.ide.fileTemplates.FileTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreateFileDialog extends JDialog {
    private static final String NONE = "None";

    private JPanel contentPane;
    private JComboBox cbTemplate;
    private JTextField tfName;
    private JLabel lbTemplate;
    private JLabel lbName;
    private JButton btConfirm;

    private CreateFileCallback createFileCallback;
    private boolean isDir;
    private FileTreeNode parent;
    private FileTreeNode current;

    public static void showForRefactor(FileTreeNode node, CreateFileCallback createFileCallback) {
        CreateFileDialog createFileDialog = new CreateFileDialog();
        createFileDialog.createFileCallback = createFileCallback;
        createFileDialog.isDir = node.isDir();
        createFileDialog.parent = node.getParent();
        createFileDialog.current = node;
        createFileDialog.initDialog();
    }

    public static void showForCreate(FileTreeNode parent, boolean isDir, CreateFileCallback createFileCallback) {
        CreateFileDialog createFileDialog = new CreateFileDialog();
        createFileDialog.createFileCallback = createFileCallback;
        createFileDialog.isDir = isDir;
        createFileDialog.parent = parent;
        createFileDialog.initDialog();
    }

    private CreateFileDialog() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initFileTemplateList() {
        FileTemplate[] fileTemplates = PluginKit.Companion.getAllFileTemplate();
        String[] items = new String[fileTemplates.length + 1];
        items[0] = NONE;
        for (int i = 0; i < fileTemplates.length; i++) {
            items[i + 1] = fileTemplates[i].getName();
        }
        cbTemplate.setModel(new DefaultComboBoxModel<>(items));
        if (isRefactor() && current.getTemplateFile() != null
                && PluginKit.Companion.getFileTemplate(current.getTemplateFile()) != null) {
            cbTemplate.setSelectedItem(current.getTemplateFile());
        }
    }

    private void onConfirm() {
        if (tfName.getText().trim().isEmpty()) {
            return;
        }
        if (isRefactor()) {
            current.setName(tfName.getText());
            // setup template
            if (!isDir && getSelectedTemplate() != null && !getSelectedTemplate().equals(NONE)) {
                current.setTemplate(getSelectedTemplate());
            }
        } else {
            current = new FileTreeNode(parent, tfName.getText(), isDir);
        }

        if (!isRefactor() && parent != null && parent.hasChild(tfName.getText(), isDir)) {
            lbName.setText(lbName.getText() + "  (already exist.)");
            return;
        }

        createFileCallback.callback(current);
        dispose();
    }

    private void initDialog() {

        if (isDir) {
            lbTemplate.setVisible(false);
            cbTemplate.setVisible(false);
        } else {
            initFileTemplateList();
        }
        pack();

        Dimension screen = getToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2 - 100;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle((isRefactor() ? "Refactor " : "New ") + (isDir ? "Directory" : "File"));
        if (isRefactor()) {
            tfName.setText(current.getRealName());
        }
        tfName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onConfirm();
                }
            }
        });
        btConfirm.addActionListener(e -> onConfirm());
        setVisible(true);
        tfName.requestFocus();
    }

    private String getSelectedTemplate() {
        if (isDir) {
            return null;
        }
        return cbTemplate.getSelectedItem().toString();
    }

    private boolean isRefactor() {
        return current != null;
    }

    public interface CreateFileCallback {
        void callback(FileTreeNode fileTreeNode);
    }

    public static void main(String[] args) {
        CreateFileDialog.showForCreate(null, true, fileTreeNode -> {

        });
    }
}
