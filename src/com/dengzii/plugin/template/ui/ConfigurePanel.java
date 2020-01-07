package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.Config;
import com.dengzii.plugin.template.model.ModuleConfig;
import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConfigurePanel extends JPanel {

    private JPanel contentPane;

    private JButton btAdd;
    private JButton btRemove;
    private JButton btCopy;

    private JTextArea taTemplate;
    private JTextArea taPlaceholder;
    private JTextField tfName;
    private JBTable JB;
    private JBList listTemplate;

    private ApplyListener applyListener;
    private List<ModuleConfig> configs;
    private DefaultListModel<String> model;
    private ModuleConfig currentConfig;

    public ConfigurePanel() {
        setLayout(new BorderLayout());
        add(contentPane);
        initPanel();
    }

    private void onApply() {
        Config.INSTANCE.saveModuleTemplates(configs);
        applyListener.apply(configs);
    }

    private void onAdd() {

    }

    private void onRemove() {
        configs.remove(listTemplate.getSelectedIndex());
        model.remove(listTemplate.getSelectedIndex());
        listTemplate.doLayout();
    }

    private void onCopy() {

    }

    private void loadConfig() {
        configs = Config.INSTANCE.loadModuleTemplates();
        model = new DefaultListModel<>();
        configs.forEach(moduleConfig -> {
            model.addElement(moduleConfig.getTemplateName());
        });
        listTemplate.setModel(model);
    }

    private void initPanel() {

        setIconButton(btAdd, AllIcons.General.Add);
        setIconButton(btRemove, AllIcons.General.Remove);
        setIconButton(btCopy, AllIcons.General.CopyHovered);
        btCopy.addActionListener(e -> onCopy());
        btAdd.addActionListener(e -> onAdd());
        btRemove.addActionListener(e -> onRemove());

        listTemplate.addListSelectionListener(e -> {
            currentConfig = configs.get(listTemplate.getSelectedIndex());
            tfName.setText(currentConfig.getTemplateName());
        });
        loadConfig();
    }

    private void setIconButton(JButton button, Icon icon) {
        button.setToolTipText(button.getText());
        button.setIcon(icon);
        button.setText("");
        button.setPreferredSize(new Dimension(25, 25));
    }

    public interface ApplyListener {
        void apply(List<ModuleConfig> moduleConfig);
    }
}
