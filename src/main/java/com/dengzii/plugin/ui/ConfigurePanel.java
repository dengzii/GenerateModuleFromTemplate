package com.dengzii.plugin.ui;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/8
 * desc   :
 * </pre>
 */
public class ConfigurePanel extends JPanel {

    public JPanel contentPane;
    public JTextField tfName;
    public JBList listTemplate;
    public JPanel panelStructure;
    public JPanel panelPlaceholder;
    public JPanel panelFileTemp;
    public JCheckBox cbPlaceholder;
    public JBTabbedPane tabbedPane;
    public JPanel panelActionBar;
    public JCheckBox cbLowercaseDir;
    public JCheckBox cbCapitalizeFile;
    public JCheckBox cbExpandPkgName;
    public JCheckBox cbEnableVelocity;
}
