package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.Config;
import com.dengzii.plugin.template.ConfigProvider;
import com.dengzii.plugin.template.ModuleTemplateConfigProvider;
import com.dengzii.plugin.template.model.Module;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
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
        ConfigProvider.Companion.getService().setModuleTemplate(Config.INSTANCE.loadModuleTemplates());
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
        return true;//currentConfig != null && !tfName.getText().equals(currentConfig.getTemplateName());
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
        configs = ConfigProvider.Companion.getService().getModuleTemplate();
        ConfigProvider.Companion.getService().getModuleTemplate();
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
        tfName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                if (currentConfig != null)
                    currentConfig.setTemplateName(tfName.getText());
            }
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
