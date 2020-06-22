package com.galaxybruce.plugin.lk;

import com.galaxybruce.util.FileUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class LKAndroidCodeTemplateAction extends AnAction {

    static String MVP_DIR = "mvp";
    static String ACTIVITY_DIR = "activity";
    static String FRAGMENT_DIR = "fragment";


    private Project project;
    private String psiPath;

    private JDialog jFrame;
    private JTextField nameTextField;
    private ButtonGroup templateGroup;

    private JCheckBox layoutBox;


    private String layoutFileName;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        project = event.getData(PlatformDataKeys.PROJECT);
        psiPath = event.getData(PlatformDataKeys.PSI_ELEMENT).toString();
        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        initView();
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


        // 是否生成布局文件选项
        JPanel file = new JPanel();
        file.setLayout(new GridLayout(2, 3));
        file.setBorder(BorderFactory.createTitledBorder("Add Layout"));
        layoutBox = new JCheckBox("Layout", true);
        file.add(layoutBox);
        container.add(file);

        // 添加一个Panel，用于输入模板文件的名称前缀
        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("Naming"));
        JLabel nameLabel = new JLabel("Module Name：");
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
        jFrame.setSize(550, 300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                save();
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };


    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                save();
            }
        }
    };

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

    private void dispose() {
        jFrame.dispose();
    }
    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please enter the module name", "Info");
            return;
        }
        dispose();
        clickCreateFile();
        project.getProjectFile().refresh(false, true);
        Messages.showInfoMessage(project, "Enjoy Coding~", "Success!");
    }

    private void clickCreateFile() {
        layoutFileName = makeLayoutFileName();

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
        generateCommonFiles();
        generateFile("page/Activity.java.txt", psiPath, ACTIVITY_DIR, "Activity.java");
    }

    private void generateFragment() {
        generateCommonFiles();
        generateFile("page/Fragment.java.txt", psiPath, FRAGMENT_DIR, "Fragment.java");
    }

    private void generateFragmentActivity() {
        generateFragment();
        generateFile("page/FragmentActivity.java.txt", psiPath, ACTIVITY_DIR, "FragmentActivity.java");
    }

    private void generateRefreshActivity() {
        generateCommonFiles();
        generateFile("page/RefreshActivity.java.txt", psiPath, ACTIVITY_DIR, "Activity.java");
    }

    private void generateRefreshFragment() {
        generateCommonFiles();
        generateFile("page/RefreshFragment.java.txt", psiPath, FRAGMENT_DIR, "Fragment.java");
    }

    private void generateRefreshFragmentActivity() {
        generateRefreshFragment();
        generateFile("page/FragmentActivity.java.txt", psiPath, ACTIVITY_DIR, "FragmentActivity.java");
    }

    private void generateCommonFiles() {
        generateFile("page/Contract.java.txt", psiPath, MVP_DIR, "Contract.java");
        generateFile("page/Presenter.java.txt", psiPath, MVP_DIR, "Presenter.java");

        if (layoutBox.isSelected()) {
            generateLayoutFile("page/RefreshLayout.xml.txt", psiPath);
        }
    }

    private void generateFile(String srcFile, String psiPath, String featureDir, String fileName) {
        String currentDirPath = psiPath;
        if(featureDir != null) {
            currentDirPath = currentDirPath  + File.separator + featureDir;
        }

        fileName = nameTextField.getText().trim() + fileName;
        String content = FileUtil.readFile(this.getClass(), srcFile);
        content = dealFile(psiPath, currentDirPath, content, fileName);
        FileUtil.writeToFile(content, currentDirPath, fileName);
    }

    /**
     * 生成布局文件
     * @param srcFile
     * @param psiPath
     */
    private void generateLayoutFile(String srcFile, String psiPath) {
        String currentDirPath = psiPath;
        final String javaDir = "java" + File.separator;
        int javaIndex = currentDirPath.indexOf(javaDir);
        currentDirPath = currentDirPath.substring(0, javaIndex) + "res" + File.separator + "layout";
        String fileName = layoutFileName + ".xml";
        String content = FileUtil.readFile(this.getClass(), srcFile);
        FileUtil.writeToFile(content, currentDirPath, fileName);
    }

    /**
     * 生成布局文件名称，不带.xml后缀，并且是经过驼峰转下划线处理
     * @return
     */
    private String makeLayoutFileName() {
        if (layoutBox.isSelected()) {
            String fileName = nameTextField.getText().trim() + "Layout";
            return FileUtil.camelToUnderline(fileName);
        }
        return "";
    }

    private String dealFile(String psiPath, String currentDirPath, String content, String fileName) {
        content = FileUtil.makePackageString(currentDirPath) + content;
        content = content.replaceAll("\\$name\\$", nameTextField.getText());
        content = content.replaceAll("\\$package\\$", FileUtil.pathToPackage(psiPath));

        // 布局文件名称需要驼峰转下划线
        if (layoutBox.isSelected()) {
            content = content.replaceAll("\\$layoutName\\$", layoutFileName);
        }
        return content;
    }

}
