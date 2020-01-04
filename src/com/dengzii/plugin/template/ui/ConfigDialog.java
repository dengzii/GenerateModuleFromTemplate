package com.dengzii.plugin.template.ui;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton btApply;
    private JButton buttonCancel;
    private JList list1;
    private JPanel toolbarPanel;
    private JButton btAdd;
    private JLabel labelAdd;

    public ConfigDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btApply);
    }

    public static void createAndShow(){
        ConfigDialog configDialog = new ConfigDialog();
        configDialog.initDialog();
        configDialog.pack();
        configDialog.setVisible(true);
    }

    private void initDialog() {

        Dimension screen = getToolkit().getScreenSize();
        int w = screen.width / 3 + 160;
        int h = 600;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle("Configure Template");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        btAdd.setBorder(null);

        btAdd.setIcon(AllIcons.General.GearPlain);
        labelAdd.setIcon(AllIcons.General.GearPlain);

    }

    public static void main(String[] args) {
        createAndShow();
    }
}
