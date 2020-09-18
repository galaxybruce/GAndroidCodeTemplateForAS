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
 * 页面模板
 */
public class AndroidPageTemplateAction extends AndroidUiTemplateAction {

    private static final Logger LOG = Logger.getInstance("AndroidPageTemplateAction");


    static String MVP_DIR = "mvp";
    static String MVVM_DIR = "mvvm";
    static String ACTIVITY_DIR = "activity";
    static String FRAGMENT_DIR = "fragment";


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
        template.setLayout(new GridLayout(2, 3));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));

        JRadioButton activity = new JRadioButton("Activity", true);
        activity.setActionCommand("Activity");
        activity.addActionListener(radioActionListener);
        JRadioButton fragment = new JRadioButton("Fragment");
        fragment.setActionCommand("Fragment");
        fragment.addActionListener(radioActionListener);
        JRadioButton fragmentActivity = new JRadioButton("FragmentActivity");
        fragmentActivity.setActionCommand("FragmentActivity");
        fragmentActivity.addActionListener(radioActionListener);

        JRadioButton refreshActivity = new JRadioButton("RefreshActivity");
        refreshActivity.setActionCommand("RefreshActivity");
        refreshActivity.addActionListener(radioActionListener);
        JRadioButton refreshFragment = new JRadioButton("RefreshFragment");
        refreshFragment.setActionCommand("RefreshFragment");
        refreshFragment.addActionListener(radioActionListener);
        JRadioButton refreshFragmentActivity = new JRadioButton("RefreshFragmentActivity");
        refreshFragmentActivity.setActionCommand("RefreshFragmentActivity");
        refreshFragmentActivity.addActionListener(radioActionListener);

        template.add(activity);
        template.add(fragment);
        template.add(fragmentActivity);
        template.add(refreshActivity);
        template.add(refreshFragment);
        template.add(refreshFragmentActivity);
//        template.add(new Label()); // GridLayout占用一列
        templateGroup = new ButtonGroup();
        templateGroup.add(activity);
        templateGroup.add(fragment);
        templateGroup.add(fragmentActivity);
        templateGroup.add(refreshActivity);
        templateGroup.add(refreshFragment);
        templateGroup.add(refreshFragmentActivity);
        container.add(template);



        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(1, 3));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Add Options"));

        // 是否生成布局文件选项
        layoutBox = new JCheckBox("Layout", true);
        kotlinBox = new JCheckBox("Kotlin", false);
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
                case "Activity":

                    break;
                case "Fragment":

                    break;
                case "FragmentActivity":

                    break;
            }
        }
    };

    @Override
    protected void clickCreateFile() {
        layoutFileName = makeLayoutFileName();
        itemLayoutFileName = makeListItemLayoutFileName();

        switch (templateGroup.getSelection().getActionCommand()) {
            case "Activity":
                generateActivity();
                break;
            case "Fragment":
                generateFragment();
                break;
            case "FragmentActivity":
                generateFragmentActivity();
                break;
            case "RefreshActivity":
                generateRefreshActivity();
                break;
            case "RefreshFragment":
                generateRefreshFragment();
                break;
            case "RefreshFragmentActivity":
                generateRefreshFragmentActivity();
                break;
        }
    }

    private void generateActivity() {
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/Activity.java.txt";
        generateFile(srcFile, psiPath, ACTIVITY_DIR, "Activity.java", true);
        generateCommonFiles(false);
        generateLayoutFile("Layout.xml.txt", false);
    }

    private void generateFragment() {
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/Fragment.java.txt";
        generateFile(srcFile, psiPath, FRAGMENT_DIR, "Fragment.java", true);
        generateCommonFiles(false);
        generateLayoutFile("Layout.xml.txt", false);
    }

    private void generateFragmentActivity() {
        generateFragment();
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + "" : "") + "/FragmentActivity.java.txt";
        generateFile(srcFile, psiPath, ACTIVITY_DIR, "FragmentActivity.java", false);
    }

    private void generateRefreshActivity() {
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshActivity.java.txt";
        generateFile(srcFile, psiPath, ACTIVITY_DIR, "Activity.java", true);
        generateCommonFiles(true);
        generateLayoutFile("RefreshLayout.xml.txt", true);
    }

    private void generateRefreshFragment() {
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshFragment.java.txt";
        generateFile(srcFile, psiPath, FRAGMENT_DIR, "Fragment.java", true);
        generateCommonFiles(true);
        generateLayoutFile("RefreshLayout.xml.txt", true);
    }

    private void generateRefreshFragmentActivity() {
        generateRefreshFragment();
        String srcFile = "page" + (mvvmBox.isSelected() ? "/" + "" : "") + "/FragmentActivity.java.txt";
        generateFile(srcFile, psiPath, ACTIVITY_DIR, "FragmentActivity.java", false);
    }

    private void generateCommonFiles(boolean isRefreshTemplate) {
        if (mvvmBox.isSelected()) {
            if(isRefreshTemplate) {
                generateFile("page/mvvm/RefreshRequest.java.txt", psiPath, MVVM_DIR + File.separator + "request", "Request.java", false);
                generateFile("page/mvvm/RefreshViewModel.java.txt", psiPath, MVVM_DIR + File.separator + "viewmodel", "ViewModel.java", false);
            } else {
                generateFile("page/mvvm/Request.java.txt", psiPath, MVVM_DIR + File.separator + "request", "Request.java", false);
                generateFile("page/mvvm/ViewModel.java.txt", psiPath, MVVM_DIR + File.separator + "viewmodel", "ViewModel.java", false);
            }
        } else {
            generateFile("page/Contract.java.txt", psiPath, MVP_DIR, "Contract.java", false);
            generateFile("page/Presenter.java.txt", psiPath, MVP_DIR, "Presenter.java", false);
        }
    }

    private void generateLayoutFile(String layoutSrcFile, boolean isRefreshTemplate) {
        if (layoutBox.isSelected()) {
            String srcFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/" + layoutSrcFile;
            generateLayoutFile(srcFile, psiPath);

            if(isRefreshTemplate) {
                String itemSFile = "page" + (mvvmBox.isSelected() ? "/" + MVVM_DIR : "") + "/RefreshItemLayout.xml.txt";
                generateLayoutFile(itemSFile, psiPath, true);
            }
        }
    }

}
