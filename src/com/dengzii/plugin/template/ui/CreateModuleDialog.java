package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.Config;
import com.dengzii.plugin.template.model.ModuleConfig;
import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CreateModuleDialog extends JDialog {

    private JPanel rootPanel;
    private JLabel labelTitle;
    private JComboBox cbModuleType;
    private JTextField fieldModuleName;
    private JTextField fieldPkgName;
    private JComboBox cbLanguage;
    private JPanel mainPanel;
    private JPanel contentPanel;

    private JButton btConfigure;
    private JButton btCancel;
    private JButton btPrevious;
    private JButton btFinish;

    private OnFinishListener onFinishListener;

    private java.util.List<ModuleConfig> moduleTemplates = Collections.emptyList();
    private ModuleConfig selectedModuleConfig;

    private HashMap<String, JPanel> panels = new HashMap<>();
    private List<String> titles = new ArrayList<>();

    private PreviewPanel previewPanel;

    private int currentPanelIndex;

    private CreateModuleDialog(OnFinishListener onFinishListener) {
        setContentPane(rootPanel);
        setModal(true);
        getRootPane().setDefaultButton(btFinish);

        this.onFinishListener = onFinishListener;
    }

    public static void createAndShow(OnFinishListener onFinishListener) {

        CreateModuleDialog dialog = new CreateModuleDialog(onFinishListener);
        dialog.initDialog();
        dialog.initData();
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShow(config -> {

        });
    }

    private void onNextClick(ActionEvent e) {
        if (currentPanelIndex == panels.size() - 1) {
            onFinishListener.onFinish(moduleTemplates.get(cbModuleType.getSelectedIndex()));
            dispose();
            return;
        }
        currentPanelIndex++;
        setPanel();
        setButton();
    }

    private void onPreviousClick(ActionEvent e) {
        if (currentPanelIndex == 0) {
            return;
        }
        currentPanelIndex--;
        setPanel();
        setButton();
    }

    private void onConfClick(ActionEvent e) {
        setPanel();
        setButton();
    }

    private void setButton() {
        btPrevious.setEnabled(true);
        btFinish.setText("Next");
        if (currentPanelIndex == panels.size() - 1) {
            btFinish.setText("Finish");
        } else if (currentPanelIndex == 0) {
            btPrevious.setEnabled(false);
        }
    }

    private void setPanel() {
        String title = titles.get(currentPanelIndex);
        labelTitle.setText(title);
        contentPanel.removeAll();
        contentPanel.add(panels.get(title));
        contentPanel.invalidate();
        contentPanel.doLayout();
        contentPanel.updateUI();
    }

    private void onModuleConfigChange(ModuleConfig moduleConfig) {
        selectedModuleConfig = moduleConfig;
        fieldModuleName.setText(selectedModuleConfig.getName());
        fieldPkgName.setText(selectedModuleConfig.getPackageName() + "." + selectedModuleConfig.getName());
        previewPanel.setModuleConfig(selectedModuleConfig);
    }

    private void initDialog() {

        Dimension screen = getToolkit().getScreenSize();
        int w = screen.width / 3 + 160;
        int h = 600;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2;
        setLocation(x, y);
        rootPanel.setPreferredSize(new Dimension(w, h));

        setTitle("Create Module");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rootPanel.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        cbLanguage.setModel(new DefaultComboBoxModel<>(ModuleConfig.Companion.getLangList()));

        btConfigure.setIcon(AllIcons.General.GearPlain);
        previewPanel = new PreviewPanel();
        titles.add("Create Module From Template");
        titles.add("Preview Module");

        panels.put(titles.get(0), mainPanel);
        panels.put(titles.get(1), previewPanel);

        btFinish.addActionListener(this::onNextClick);
        btPrevious.addActionListener(this::onPreviousClick);
        btCancel.addActionListener(e -> dispose());
        btConfigure.addActionListener(this::onConfClick);

        cbLanguage.addItemListener(e -> {
            if (cbLanguage.getSelectedItem() != null) {
                selectedModuleConfig.setLanguage(cbLanguage.getSelectedItem().toString().toUpperCase());
            }
        });
        cbModuleType.addItemListener(e -> {
            onModuleConfigChange(moduleTemplates.get(cbModuleType.getSelectedIndex()));
        });
    }

    private void initData() {

        moduleTemplates = Config.INSTANCE.getDEFAULT_TEMPLATE();
        List<String> temp = new ArrayList<>();
        moduleTemplates.forEach(i -> temp.add(i.getTemplateName()));
        cbModuleType.setModel(new DefaultComboBoxModel<>(temp.toArray()));
        cbModuleType.setSelectedIndex(1);
    }

    public interface OnFinishListener {
        void onFinish(ModuleConfig config);
    }
}
