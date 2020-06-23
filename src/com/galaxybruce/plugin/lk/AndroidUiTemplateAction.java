package com.galaxybruce.plugin.lk;

import com.galaxybruce.util.FileUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

/**
 * dialog模板
 */
public abstract class AndroidUiTemplateAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidUiTemplateAction");


    protected Project project;
    protected String psiPath;

    protected JDialog jFrame;
    protected JTextField nameTextField;
    protected ButtonGroup templateGroup;

    protected JCheckBox layoutBox;

    protected String javaParentPath;
    protected String layoutFileName;
    protected String modulePackage;


    protected KeyListener keyListener = new KeyListener() {
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

    protected ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                save();
            }
        }
    };

    private void dispose() {
        jFrame.dispose();
    }
    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please enter the name", "Info");
            return;
        }
        dispose();
        clickCreateFile();
        project.getProjectFile().refresh(false, true);
        Messages.showInfoMessage(project, "Enjoy Coding~", "Success!");
    }

    protected abstract void clickCreateFile();

    protected void generateFile(String srcFile, String psiPath, String featureDir, String fileName) {
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
    protected void generateLayoutFile(String srcFile, String psiPath) {
        String currentDirPath = javaParentPath + "res" + File.separator + "layout";
        String fileName = layoutFileName + ".xml";
        String content = FileUtil.readFile(this.getClass(), srcFile);
        FileUtil.writeToFile(content, currentDirPath, fileName);
    }

    /**
     * 生成布局文件名称，不带.xml后缀，并且是经过驼峰转下划线处理
     * @return
     */
    protected String makeLayoutFileName() {
        if (layoutBox.isSelected()) {
            String fileName = nameTextField.getText().trim() + "Layout";
            return FileUtil.camelToUnderline(fileName);
        }
        return "";
    }

    protected String dealFile(String psiPath, String currentDirPath, String content, String fileName) {
        content = FileUtil.makePackageString(currentDirPath) + content;
        content = content.replaceAll("\\$name\\$", nameTextField.getText());
        content = content.replaceAll("\\$package\\$", FileUtil.pathToPackage(psiPath));

        if(modulePackage != null && !"".equals(modulePackage)) {
            content = content.replaceAll("\\$importR\\$", "import " + modulePackage + ".R;");
        } else {
            content = content.replaceAll("\\$importR\\$", "");
        }

        // 布局文件名称需要驼峰转下划线
        if (layoutBox.isSelected()) {
            content = content.replaceAll("\\$layoutName\\$", layoutFileName);
        }
        return content;
    }

}
