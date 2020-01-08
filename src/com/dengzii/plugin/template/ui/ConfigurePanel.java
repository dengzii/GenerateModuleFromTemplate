package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.ModuleTemplateConfigProvider;
import com.dengzii.plugin.template.model.Module;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/8
 * desc   :
 * </pre>
 */
public class ConfigurePanel extends JPanel implements SearchableConfigurable {

    private JPanel contentPane;

    private JButton btAdd;
    private JButton btRemove;
    private JButton btCopy;

    private JTextArea taTemplate;
    private JTextArea taPlaceholder;
    private JTextField tfName;
    private JBTable JB;
    private JBList listTemplate;

    private List<Module> configs;
    private DefaultListModel<String> model;
    private Module currentConfig;

    private ConfigurePanel() {
        setLayout(new BorderLayout());
        add(contentPane);
        initPanel();
    }

    @Override
    public void apply() {
//        Config.INSTANCE.saveModuleTemplates(configs);
    }

    @NotNull
    @Override
    public String getId() {
        return "preferences.ModuleTemplateConfig";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return new ConfigurePanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Module Template Generator";
    }

    private void onAdd() {

    }

    private void onRemove() {
        if (listTemplate.getSelectedIndex() == -1) {
            return;
        }
        configs.remove(listTemplate.getSelectedIndex());
        model.remove(listTemplate.getSelectedIndex());
        listTemplate.doLayout();
    }

    private void onCopy() {

    }

    private void loadConfig() {
        configs = ModuleTemplateConfigProvider.Companion.getService().getModuleConfig();
        model = new DefaultListModel<>();
        configs.forEach(module -> {
            model.addElement(module.getTemplateName());
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
}
