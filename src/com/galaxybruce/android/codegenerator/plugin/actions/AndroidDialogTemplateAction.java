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
        LOG.info("android dialog template code creates start... ");

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

        LOG.info("android dialog template code creates end... ");
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
        template.setLayout(new GridLayout(2, 3));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));

        JRadioButton bottomDialog = new JRadioButton("BottomDialog", true);
        bottomDialog.setActionCommand("BottomDialog");
        bottomDialog.addActionListener(radioActionListener);
        JRadioButton centerDialog = new JRadioButton("CenterDialog");
        centerDialog.setActionCommand("CenterDialog");
        centerDialog.addActionListener(radioActionListener);
        JRadioButton centerCustomConfirmDialog = new JRadioButton("CenterCustomConfirmDialog");
        centerCustomConfirmDialog.setActionCommand("CenterCustomConfirmDialog");
        centerCustomConfirmDialog.addActionListener(radioActionListener);

        JRadioButton refreshBottomDialog = new JRadioButton("RefreshBottomDialog", true);
        refreshBottomDialog.setActionCommand("RefreshBottomDialog");
        refreshBottomDialog.addActionListener(radioActionListener);
        JRadioButton refreshCenterDialog = new JRadioButton("RefreshCenterDialog");
        refreshCenterDialog.setActionCommand("RefreshCenterDialog");
        refreshCenterDialog.addActionListener(radioActionListener);
        JRadioButton refreshCenterCustomConfirmDialog = new JRadioButton("RefreshCenterCustomConfirmDialog");
        refreshCenterCustomConfirmDialog.setActionCommand("RefreshCenterCustomConfirmDialog");
        refreshCenterCustomConfirmDialog.addActionListener(radioActionListener);

        template.add(bottomDialog);
        template.add(centerDialog);
        template.add(centerCustomConfirmDialog);
        template.add(refreshBottomDialog);
        template.add(refreshCenterDialog);
        template.add(refreshCenterCustomConfirmDialog);
        templateGroup = new ButtonGroup();
        templateGroup.add(bottomDialog);
        templateGroup.add(centerDialog);
        templateGroup.add(centerCustomConfirmDialog);
        templateGroup.add(refreshBottomDialog);
        templateGroup.add(refreshCenterDialog);
        templateGroup.add(refreshCenterCustomConfirmDialog);
        container.add(template);


        // 是否生成布局文件选项
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(1, 3));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Add Options"));


        // 是否生成布局文件选项
        layoutBox = new JCheckBox("Layout", true);
        layoutBox.setEnabled(false);
        kotlinBox = new JCheckBox("Kotlin", true);
        mvvmBox = new JCheckBox("MVVM", true);
        mvvmBox.setEnabled(false);
        optionsPanel.add(layoutBox);
        optionsPanel.add(kotlinBox);
        optionsPanel.add(mvvmBox);
        container.add(optionsPanel);

        // 添加一个Panel，用于输入模板文件的名称前缀
        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("Naming"));
        JLabel nameLabel = new JLabel("Dialog Name Prefix：");
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
                case "RefreshBottomDialog":
                case "RefreshCenterDialog":
                case "RefreshCenterCustomConfirmDialog":
                    mvvmBox.setSelected(true);
                    mvvmBox.setEnabled(false);
                    break;
                default:
                    mvvmBox.setSelected(true);
                    mvvmBox.setEnabled(false);
                    break;
            }
        }
    };

    @Override
    protected void clickCreateFile() {
        layoutFileName = makeLayoutFileName();
        itemLayoutFileName = makeListItemLayoutFileName(true);

        switch (templateGroup.getSelection().getActionCommand()) {
            case "BottomDialog":
                generateBottomDialog();
                break;
            case "RefreshBottomDialog":
                generateRefreshBottomDialog();
                break;
            case "CenterDialog":
                generateCenterDialog();
                break;
            case "RefreshCenterDialog":
                generateRefreshCenterDialog();
                break;
            case "CenterCustomConfirmDialog":
                generateCenterCustomConfirmDialog();
                break;
            case "RefreshCenterCustomConfirmDialog":
                generateRefreshCenterCustomConfirmDialog();
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
        generateCommonFiles(false);
        if (layoutBox.isSelected()) {
            generateLayoutFile("BottomDialogLayout.xml.txt", false);
        }
    }

    private void generateRefreshBottomDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshBottomDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles(true);
        if (layoutBox.isSelected()) {
            generateLayoutFile("RefreshBottomDialogLayout.xml.txt", true);
        }
    }

    private void generateCenterDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles(false);
        if (layoutBox.isSelected()) {
            generateLayoutFile("CenterDialogLayout.xml.txt", false);
        }
    }

    private void generateRefreshCenterDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshCenterDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles(true);
        if (layoutBox.isSelected()) {
            generateLayoutFile("RefreshCenterDialogLayout.xml.txt", true);
        }
    }

    private void generateCenterCustomConfirmDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/CenterCustomConfirmDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles(false);
        if (layoutBox.isSelected()) {
            generateLayoutFile("CenterCustomConfirmDialogLayout.xml.txt", false);
        }
    }

    private void generateRefreshCenterCustomConfirmDialog() {
        generateFile("page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshCenterCustomConfirmDialog.java.txt", psiPath, DIALOG_DIR, "Dialog.java", true);
        generateCommonFiles(true);
        if (layoutBox.isSelected()) {
            generateLayoutFile("RefreshCenterCustomConfirmDialogLayout.xml.txt", true);
        }
    }

    private void generateCommonFiles(boolean isRefreshTemplate) {
        if (mvvmBox.isSelected()) {
            if(isRefreshTemplate) {
                generateFile("page/mvvm/RefreshRequest.java.txt", psiPath, MVVM_DIR + File.separator + "request", "DialogRequest.java", false);
                generateFile("page/mvvm/RefreshViewModel.java.txt", psiPath, MVVM_DIR + File.separator + "viewmodel", "DialogViewModel.java", false);
            } else {
                generateFile("page/mvvm/Request.java.txt", psiPath, MVVM_DIR + File.separator + "request", "DialogRequest.java", false);
                generateFile("page/mvvm/ViewModel.java.txt", psiPath, MVVM_DIR + File.separator + "viewmodel", "DialogViewModel.java", false);
            }
        }
    }

    private void generateLayoutFile(String layoutSrcFile, boolean isRefreshTemplate) {
        if (layoutBox.isSelected()) {
            String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/" + layoutSrcFile;
            LOG.info("android generateLayoutFile: " + srcFile);
            generateLayoutFile(srcFile, psiPath);

            if(isRefreshTemplate) {
                String itemSFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshItemLayout.xml.txt";
                generateLayoutFile(itemSFile, psiPath, true);
            }
        }
    }

    @Override
    protected String getNameSuffixStr() {
        return "Dialog";
    }
}
