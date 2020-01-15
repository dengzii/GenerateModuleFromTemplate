package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.Config;
import com.dengzii.plugin.template.model.Module;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
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

    private JTextField tfName;
    private JBList listTemplate;
    private JPanel panelStructure;
    private JPanel panelPlaceholder;
    private JPanel panelFileTemp;
    private EditToolbar actionbar;

    private List<Module> configs;
    private DefaultListModel<String> templateListModel;

    private Module currentConfig;

    private ConfigurePanel panelConfig;
    private PreviewPanel panelPreview;

    private EditableTable tablePlaceholder;
    private EditableTable tableFileTemp;

    private ConfigurePanel() {

        initComponent();
        loadConfig();
        initData();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
        add(contentPane);

        panelPreview = new PreviewPanel();
        tablePlaceholder = new EditableTable(new String[]{"Placeholder", "Default Value"});
        tableFileTemp = new EditableTable(new String[]{"FileName", "Template"});
        panelStructure.add(panelPreview);
        panelPlaceholder.add(tablePlaceholder, BorderLayout.CENTER);
        panelFileTemp.add(tableFileTemp, BorderLayout.CENTER);
    }


    private void initData() {

        actionbar.onAdd(() -> {
            onAddConfig();
            return null;
        });
        actionbar.onRemove(() -> {
            onRemoveConfig();
            return null;
        });
        actionbar.onCopy(() -> {
            onCopyConfig();
            return null;
        });

        listTemplate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTemplate.addListSelectionListener(e -> {
            if (noSelectedConfig()) return;
            onConfigSelect(getSelectedConfigIndex());

        });
        //noinspection unchecked
        listTemplate.setModel(templateListModel);
        tfName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                if (currentConfig != null)
                    currentConfig.setTemplateName(tfName.getText());
            }
        });
        if (templateListModel.size() > 1) {
            listTemplate.setSelectedIndex(0);
        }
    }

    private void onAddConfig() {
        Module newConfig = Config.INSTANCE.getTEMPLATE_ANDROID_APPLICATION().clone();
        configs.add(newConfig);
        templateListModel.addElement(newConfig.getTemplateName());
        listTemplate.doLayout();
        listTemplate.setSelectedIndex(configs.indexOf(newConfig));
    }

    private void onRemoveConfig() {
        if (noSelectedConfig()) {
            return;
        }
        int selectedIndex = getSelectedConfigIndex();
        configs.remove(selectedIndex);
        templateListModel.remove(selectedIndex);
        listTemplate.doLayout();
    }

    private void onCopyConfig() {
        if (noSelectedConfig()) {
            return;
        }
        Module newConfig = currentConfig.clone();
        configs.add(newConfig);
        templateListModel.addElement(newConfig.getTemplateName());
        listTemplate.doLayout();
        listTemplate.setSelectedIndex(configs.indexOf(newConfig));
    }

    private void loadConfig() {
        configs = Config.INSTANCE.loadModuleTemplates();
        templateListModel = new DefaultListModel<>();
        configs.forEach(module -> templateListModel.addElement(module.getTemplateName()));
    }

    private void onConfigSelect(int index) {
        if (currentConfig == configs.get(index)) {
            return;
        }
        if (currentConfig != null) {
            cacheConfig();
        }
        currentConfig = configs.get(index);
        tfName.setText(currentConfig.getTemplateName());

        // update tree, file template and placeholder table
        panelPreview.setModuleConfig(currentConfig);
        tableFileTemp.setPairData(currentConfig.getTemplate().getFileTemplates());
        tablePlaceholder.setPairData(currentConfig.getTemplate().getPlaceHolderMap());
    }

    private void cacheConfig() {
        if (currentConfig == null) return;
        currentConfig.getTemplate().setFileTemplates(tableFileTemp.getPairResult());
        currentConfig.getTemplate().setPlaceHolderMap(tablePlaceholder.getPairResult());
    }

    private int getSelectedConfigIndex() {
        return listTemplate.getSelectedIndex();
    }

    private boolean noSelectedConfig() {
        return getSelectedConfigIndex() == -1;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // Implement SearchableConfigurable

    @Override
    public void apply() {
        if (panelConfig != null) {
            panelConfig.apply();
            return;
        }
        cacheConfig();
        Config.INSTANCE.saveModuleTemplates(configs);
    }

    @NotNull
    @Override
    public String getId() {
        return "preferences.ModuleTemplateConfig";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        panelConfig = new ConfigurePanel();
        return panelConfig;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Directory Template";
    }
}
