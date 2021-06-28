package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.model.FileTreeNode;
import com.dengzii.plugin.template.utils.PluginKit;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.ProjectManager;
import org.apache.velocity.runtime.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Properties;

public class FileDialog extends JDialog {
    private static final String NONE = "None";

    private JPanel contentPane;
    private JComboBox cbTemplate;
    private JTextField tfName;
    private JLabel lbTemplate;
    private JLabel lbName;
    private JButton btConfirm;
    private JPanel panelTemplate;
    private JCheckBox cbProperties;

    private CreateFileCallback createFileCallback;
    private boolean isDir;
    private FileTreeNode parent;
    private FileTreeNode fileNode;
    private FileTemplate[] fileTemplates;

    public static void showForRefactor(FileTreeNode node, CreateFileCallback createFileCallback) {
        FileDialog fileDialog = new FileDialog();
        fileDialog.createFileCallback = createFileCallback;
        fileDialog.isDir = node.isDir();
        fileDialog.parent = node.getParent();
        fileDialog.fileNode = node;
        fileDialog.initDialog();
    }

    public static void showForCreate(FileTreeNode parent, boolean isDir, CreateFileCallback createFileCallback) {
        FileDialog fileDialog = new FileDialog();
        fileDialog.createFileCallback = createFileCallback;
        fileDialog.isDir = isDir;
        fileDialog.parent = parent;
        fileDialog.initDialog();
    }

    private FileDialog() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initFileTemplateList() {
        fileTemplates = PluginKit.Companion.getAllFileTemplate();
        String[] items = new String[fileTemplates.length + 1];
        items[0] = NONE;
        for (int i = 0; i < fileTemplates.length; i++) {
            items[i + 1] = fileTemplates[i].getName();
        }
        cbTemplate.setModel(new DefaultComboBoxModel<>(items));
        if (isRefactor() && fileNode.getTemplateFile() != null
                && PluginKit.Companion.getFileTemplate(fileNode.getTemplateFile()) != null) {
            cbTemplate.setSelectedItem(fileNode.getTemplateFile());
        }
    }

    private void onConfirm() {
        if (tfName.getText().trim().isEmpty()) {
            return;
        }
        String template = getSelectedTemplate();
        template = NONE.equals(template) ? "" : template;

        if (isRefactor()) {
            fileNode.setName(tfName.getText());
            // setup template
        } else {
            fileNode = new FileTreeNode(parent, tfName.getText(), isDir);
        }
        if (!isDir && template != null) {
            if (!template.isEmpty() && cbProperties.isSelected()) {
                FileTemplate ft = fileTemplates[cbTemplate.getSelectedIndex() - 1];
                String[] p = new String[0];
                try {
                    p = FileTemplateUtil.calculateAttributes(ft.getText(), new Properties(), true, ProjectManager.getInstance().getDefaultProject());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fileNode.addPlaceholders(p);
            }
            if (!template.isEmpty()){
                fileNode.addFileTemplate(fileNode.getName(), template);
            }
        }

        if (!isRefactor() && parent != null && parent.hasChild(tfName.getText(), isDir)) {
            lbName.setText(lbName.getText() + "  (already exist.)");
            return;
        }
        createFileCallback.callback(fileNode);
        dispose();
    }

    private void initDialog() {

        if (isDir) {
            lbTemplate.setVisible(false);
            cbTemplate.setVisible(false);
            panelTemplate.setVisible(false);
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
            tfName.setText(fileNode.getName());
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
        return fileNode != null;
    }

    public interface CreateFileCallback {
        void callback(FileTreeNode fileTreeNode);
    }

    public static void main(String[] args) {
        FileDialog.showForCreate(null, true, fileTreeNode -> {

        });
    }
}
