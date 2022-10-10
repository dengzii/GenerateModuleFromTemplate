package com.dengzii.plugin.template.ui;

import com.dengzii.plugin.template.Config;
import com.dengzii.plugin.template.TemplateConfigurable;
import com.dengzii.plugin.template.model.Module;
import com.dengzii.plugin.template.utils.Logger;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CreateModuleDialog extends JDialog {

    private static final String TAG = CreateModuleDialog.class.getSimpleName();
    private final OnFinishListener onFinishListener;
    private final HashMap<String, JPanel> panels = new HashMap<>();
    private final List<String> titles = new ArrayList<>();
    private final Project project;
    private JPanel rootPanel;
    private JLabel labelTitle;
    private JComboBox cbModuleTemplate;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JButton btConfigure;
    private JButton btCancel;
    private JButton btPrevious;
    private JButton btFinish;
    private JScrollPane scrollPanePlaceHolder;
    private JScrollPane scrollPaneFileTemplate;
    private JPanel panelConfig;
    private EditableTable tablePlaceholder;
    private EditableTable tableFileTemplate;
    private java.util.List<Module> moduleTemplates = Collections.emptyList();
    private Module selectedModule;
    private PreviewPanel previewPanel;
    private int currentPanelIndex;

    private CreateModuleDialog(Project project, OnFinishListener onFinishListener) {
        setContentPane(rootPanel);
        setModal(true);
        getRootPane().setDefaultButton(btFinish);
        this.project = project;
        this.onFinishListener = onFinishListener;
    }

    public static void createAndShow(Project project, OnFinishListener onFinishListener) {

        CreateModuleDialog dialog = new CreateModuleDialog(project, onFinishListener);
        dialog.initDialog();
        dialog.initData();
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShow(null, config -> {

        });
    }

    private void onNextClick(ActionEvent e) {
        tablePlaceholder.stopEdit();
        tableFileTemplate.stopEdit();
        selectedModule.getTemplate().removeAllTemplateInTree();
        selectedModule.getTemplate().removeAllPlaceHolderInTree();
        selectedModule.getTemplate().setPlaceholders(tablePlaceholder.getPairResult());
        selectedModule.getTemplate().setFileTemplates(tableFileTemplate.getPairResult());

        previewPanel.setReplacePlaceholder(true);
        previewPanel.setModuleConfig(selectedModule);

        if (currentPanelIndex == panels.size() - 1) {
            onFinishListener.onFinish(selectedModule);
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
        ShowSettingsUtil.getInstance().editConfigurable(project, new TemplateConfigurable(this::initData, null));
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
        contentPanel.doLayout();
        contentPanel.updateUI();
    }

    private void onModuleConfigChange() {
        Logger.INSTANCE.i(TAG, "onModuleConfigChange");
        selectedModule = moduleTemplates.get(cbModuleTemplate.getSelectedIndex()).clone();
        selectedModule.getTemplate().expandPath();

        panelConfig.removeAll();
        panelConfig.add(new JBCheckBox("Capitalize file", selectedModule.getCapitalizeFile()));
        panelConfig.add(new JBCheckBox("Lowercase dir", selectedModule.getLowercaseDir()));
        panelConfig.add(new JBCheckBox("Expand package name", selectedModule.getPackageNameToDir()));
        panelConfig.doLayout();
        panelConfig.updateUI();
        for (Component c : panelConfig.getComponents()) {
            c.setEnabled(false);
        }

        previewPanel.setReplacePlaceholder(true);
        previewPanel.setModuleConfig(selectedModule);
        tablePlaceholder.setPairData(selectedModule.getTemplate().getAllPlaceholdersMap());
        tableFileTemplate.setPairData(selectedModule.getTemplate().getAllTemplateMap());
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

        tablePlaceholder = new EditableTable(new String[]{"Key", "Value"}, new Boolean[]{false, true});
        tablePlaceholder.setToolBarVisible(false);
        tableFileTemplate = new EditableTable(new String[]{"File Name", "Template"}, new Boolean[]{false, true});
        tableFileTemplate.setToolBarVisible(false);

        scrollPanePlaceHolder.setViewportView(tablePlaceholder);
        scrollPaneFileTemplate.setViewportView(tableFileTemplate);

        btConfigure.setIcon(AllIcons.General.GearPlain);
        previewPanel = new PreviewPanel(true);
        titles.add("Create Module From Template");
        titles.add("Preview Module");

        panels.put(titles.get(0), mainPanel);
        panels.put(titles.get(1), previewPanel);

        btFinish.addActionListener(this::onNextClick);
        btPrevious.addActionListener(this::onPreviousClick);
        btCancel.addActionListener(e -> dispose());
        btConfigure.addActionListener(this::onConfClick);

        cbModuleTemplate.addItemListener(e -> {
            onModuleConfigChange();
        });
    }

    private void initData() {
        Logger.INSTANCE.i(TAG, "initData");
        moduleTemplates = Config.INSTANCE.loadModuleTemplates();
//        moduleTemplates = new ArrayList<>();
//        moduleTemplates.add(Module.Companion.getAucModule());
        List<String> temp = new ArrayList<>();
        moduleTemplates.forEach(i -> temp.add(i.getTemplateName()));
        cbModuleTemplate.setModel(new DefaultComboBoxModel<>(temp.toArray()));
        if (cbModuleTemplate.getModel().getSize() > 0) {
            cbModuleTemplate.setSelectedIndex(0);
            onModuleConfigChange();
        }
    }

    public interface OnFinishListener {
        void onFinish(Module config);
    }
}
