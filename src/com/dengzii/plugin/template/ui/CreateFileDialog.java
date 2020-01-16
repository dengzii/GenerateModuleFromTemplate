package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.model.FileTreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreateFileDialog extends JDialog {
    private JPanel contentPane;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JLabel label1;
    private JLabel label2;

    private CreateFileCallback createFileCallback;
    private boolean isDir;
    private FileTreeNode parent;
    private FileTreeNode current;

    public static void showForRename(FileTreeNode node, CreateFileCallback createFileCallback) {
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

        contentPane.registerKeyboardAction(e -> {
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initDialog() {

        if (isDir) {
            label1.setVisible(false);
            comboBox1.setVisible(false);
        }
        pack();

        Dimension screen = getToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2 - 100;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle((isRename() ? "New " : "Rename ") + (isDir ? "Directory" : "File"));
        if (isRename()) {
            textField1.setText(current.getRealName());
        }
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (parent != null && parent.hasChild(textField1.getText(), isDir)) {
                        label2.setText(label2.getText() + "  (already exist.)");
                        return;
                    }
                    if (isRename()) {
                        current.setName(textField1.getText());
                        createFileCallback.callback(current);
                    } else {
                        createFileCallback.callback(new FileTreeNode(parent, textField1.getText(), isDir));
                    }

                    dispose();
                }
            }
        });
        setVisible(true);
        textField1.requestFocus();
    }

    private boolean isRename() {
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
