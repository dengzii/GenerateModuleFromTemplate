package com.dengzii.plugin.auc.ui;

import com.dengzii.plugin.auc.model.FileTreeNode;
import com.dengzii.plugin.auc.model.ModuleConfig;
import com.dengzii.plugin.auc.template.AucTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CreateModuleDialog extends JDialog {
    private JPanel contentPane;
    private JButton btCancel;
    private JButton btFinish;
    private JComboBox cbModuleType;
    private JTextField filedModuleName;
    private JTextField fieldPkgName;
    private JComboBox cbLanguage;
    private JButton btConfigure;

    private OnFinishListener onFinishListener;

    private CreateModuleDialog(OnFinishListener onFinishListener) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btFinish);
        this.onFinishListener = onFinishListener;
    }

    public static void createAndShow(OnFinishListener onFinishListener) {

        CreateModuleDialog dialog = new CreateModuleDialog(onFinishListener);
        dialog.initDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShow(config -> {

        });
    }

    private void finishClick(ActionEvent e) {
        FileTreeNode temp = AucTemplate.INSTANCE.getAPP();
        switch (cbModuleType.getSelectedIndex()) {
            case 0:
                temp = AucTemplate.INSTANCE.getMODULE();
                break;
            case 1:
                temp = AucTemplate.INSTANCE.getAPP();
                break;
            case 2:
                temp = AucTemplate.INSTANCE.getPKG();
                break;
            case 3:
                temp = AucTemplate.INSTANCE.getEXPORT();
                break;
        }
        onFinishListener.onFinish(new ModuleConfig(
                temp,
                filedModuleName.getText(),
                fieldPkgName.getText(),
                cbLanguage.getSelectedItem().toString()));
        dispose();
    }

    private void onConfClick(ActionEvent e) {

    }

    private void initDialog() {

        Dimension screen = getToolkit().getScreenSize();
        int w = screen.width / 3;
        int h = 600;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2;
        setLocation(x, y);
        contentPane.setPreferredSize(new Dimension(w, h));

        setTitle("Create Module");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        btFinish.addActionListener(this::finishClick);
        btCancel.addActionListener(e -> dispose());
        btConfigure.addActionListener(this::onConfClick);
    }

    public interface OnFinishListener {
        void onFinish(ModuleConfig config);
    }
}
