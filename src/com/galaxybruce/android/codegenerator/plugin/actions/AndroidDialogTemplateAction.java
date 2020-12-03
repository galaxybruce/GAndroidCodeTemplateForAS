package com.galaxybruce.android.codegenerator.plugin.actions;

import com.galaxybruce.android.codegenerator.plugin.util.FileUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * dialog模板
 */
public class AndroidDialogTemplateAction extends AndroidUiTemplateAction {

    private static final Logger LOG = Logger.getInstance("AndroidDialogTemplateAction");


    static String DIALOG_DIR = "dialog";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOG.info("android page template code creates start... ");

        project = event.getData(PlatformDataKeys.PROJECT);
        PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            Messages.showMessageDialog(project, "Please switch Project View Mode! ", "Generate Failed！", null);
            return;
        }
        psiPath = psiElement.toString();
        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        LOG.info("target path is: " + psiPath);

        // 判断点击的目录是否在包含java目录
        final String javaDir = "java" + File.separator;
        int javaIndex = psiPath.indexOf(javaDir);
        if(javaIndex < 0) {
            Messages.showMessageDialog(project, "Please select a subdirectory in the Java directory! ", "Generate Failed！", null);
            return;
        } else {
            javaParentPath = psiPath.substring(0, javaIndex);
        }

        modulePackage = FileUtils.readPackageName(javaParentPath);

        initView();

        LOG.info("android page template code creates end... ");
    }

    private void initView() {
        // 创建弹框窗口
        jFrame = new JDialog();
        jFrame.setModal(true);


        // dialog中的面板设置布局样式
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        // 添加一个Panel，用于提供可用的模板类型，如Activity，Fragment
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(1, 2));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));

        JRadioButton bottomDialog = new JRadioButton("Bottom Dialog", true);
        bottomDialog.setActionCommand("Bottom-Dialog");
        bottomDialog.addActionListener(radioActionListener);
        JRadioButton centerDialog = new JRadioButton("Center Dialog");
        centerDialog.setActionCommand("Center-Dialog");
        centerDialog.addActionListener(radioActionListener);

        JRadioButton centerCustomConfirmDialog = new JRadioButton("Center CustomConfirm Dialog");
        centerCustomConfirmDialog.setActionCommand("Center-CustomConfirm-Dialog");
        centerCustomConfirmDialog.addActionListener(radioActionListener);

        template.add(bottomDialog);
        template.add(centerDialog);
        template.add(centerCustomConfirmDialog);
        templateGroup = new ButtonGroup();
        templateGroup.add(bottomDialog);
        templateGroup.add(centerDialog);
        templateGroup.add(centerCustomConfirmDialog);
        container.add(template);


        // 是否生成布局文件选项
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(1, 3));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Add Options"));


        // 是否生成布局文件选项
        layoutBox = new JCheckBox("Layout", true);
        layoutBox.setEnabled(false);
        kotlinBox = new JCheckBox("Kotlin", false);
        kotlinBox.setEnabled(false);
        mvvmBox = new JCheckBox("MVVM", false);
        optionsPanel.add(layoutBox);
        optionsPanel.add(kotlinBox);
        optionsPanel.add(mvvmBox);
        container.add(optionsPanel);

        // 添加一个Panel，用于输入模板文件的名称前缀
        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("Naming"));
        JLabel nameLabel = new JLabel("Module Name Prefix：");
        nameTextField = new JTextField(30);
        nameTextField.addKeyListener(keyListener);
        nameField.add(nameLabel);
        nameField.add(nameTextField);
        container.add(nameField);


        // 添加一个Panel，确定 取消按钮
        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);

        JButton ok = new JButton("OK");
        ok.setForeground(JBColor.GREEN);
        ok.addActionListener(actionListener);
        menu.add(cancel);
        menu.add(ok);
        container.add(menu);

        // 显示窗口
        jFrame.setSize(800, 400);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }



    private ActionListener radioActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Bottom-Dialog":

                    break;
                case "Center-Dialog":

                    break;
            }
        }
    };

    @Override
    protected void clickCreateFile() {
        layoutFileName = makeLayoutFileName();

        switch (templateGroup.getSelection().getActionCommand()) {
            case "Bottom-Dialog":
                generateBottomDialog();
                break;
            case "Center-Dialog":
                generateCenterDialog();
                break;
            case "Center-CustomConfirm-Dialog":
                generateCenterCustomConfirmDialog();
                break;
        }
    }

    @Override
    protected String makeLayoutFileName() {
        if (layoutBox.isSelected()) {
            String fileName = nameTextField.getText().trim() + "Dialog";
            return FileUtils.camelToUnderline(fileName);
        }
        return "";
    }

    private void generateBottomDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/BottomDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles();
        if (layoutBox.isSelected()) {
            generateLayoutFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/BottomDialogLayout.xml.txt", psiPath);
        }
    }

    private void generateCenterDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles();
        if (layoutBox.isSelected()) {
            generateLayoutFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterDialogLayout.xml.txt", psiPath);
        }
    }

    private void generateCenterCustomConfirmDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterCustomConfirmDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles();
        if (layoutBox.isSelected()) {
            generateLayoutFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterCustomConfirmDialogLayout.xml.txt", psiPath);
        }
    }

    private void generateCommonFiles() {
        if (mvvmBox.isSelected()) {
            generateFile("page/mvvm/Request.java.txt", psiPath, MVVM_DIR + File.separator + "request", "DialogRequest.java", false);
            generateFile("page/mvvm/ViewModel.java.txt", psiPath, MVVM_DIR + File.separator + "viewmodel", "DialogViewModel.java", false);
        }
    }

    @Override
    protected String getNameSuffixStr() {
        return "Dialog";
    }
}
