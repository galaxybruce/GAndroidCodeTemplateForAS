package com.galaxybruce.android.codegenerator.plugin.actions;

import com.galaxybruce.android.codegenerator.plugin.util.FileUtils;
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
 *
 * 参考文章：
 * [Intellij IDEA 插件开发秘籍](https://www.cnblogs.com/kancy/p/10654569.html)
 */
public abstract class AndroidUiTemplateAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidUiTemplateAction");

    static String MVP_DIR = "mvp";
    static String MVVM_DIR = "mvvm";

    static String ACTIVITY_DIR = "activity";
    static String FRAGMENT_DIR = "fragment";
    static String DIALOG_DIR = "dialog";

    protected Project project;
    protected String psiPath;

    protected JDialog jFrame;
    protected JTextField nameTextField;
    protected ButtonGroup templateGroup;

    protected JCheckBox layoutBox;
    protected JCheckBox kotlinBox;
    protected JCheckBox mvvmBox;

    protected String javaParentPath;
    protected String layoutFileName;
    protected String itemLayoutFileName;
    protected String modulePackage;

    protected String contextFileName;

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

    protected void generateFile(String srcFile, String psiPath, String featureDir, String fileName, boolean isContextFile) {
        String currentDirPath = psiPath;
        if(featureDir != null) {
            currentDirPath = currentDirPath  + File.separator + featureDir;
        }

        String content = "";

        if(kotlinBox != null && kotlinBox.isSelected()) {
            String tempSrcFile = srcFile.replaceAll("\\.java\\.txt", ".kt.txt");
            String tempFileName = fileName.replaceAll("\\.java", ".kt");
            content = FileUtils.readFile(this.getClass(), tempSrcFile);

            if(content != null && !"".equals(content.trim())) {
                srcFile = tempSrcFile;
                fileName = tempFileName;
            }
        }

        if(content == null || "".equals(content.trim())) {
            content = FileUtils.readFile(this.getClass(), srcFile);
        }

        fileName = nameTextField.getText().trim() + fileName;
        if(isContextFile) {
            contextFileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        content = dealFile(psiPath, currentDirPath, content, false);
        FileUtils.writeToFile(content, currentDirPath, fileName);
    }

    protected void generateLayoutFile(String srcFile, String psiPath) {
        generateLayoutFile(srcFile, psiPath, false);
    }

    /**
     * 生成布局文件
     * @param srcFile
     * @param psiPath
     */
    protected void generateLayoutFile(String srcFile, String psiPath, boolean isItem) {
        String currentDirPath = javaParentPath + "res" + File.separator + "layout";
        String fileName = (isItem ? itemLayoutFileName : layoutFileName) + ".xml";
        String content = FileUtils.readFile(this.getClass(), srcFile);
//        LOG.info("android generateLayoutFile content: \n" + content);
        content = dealFile(psiPath, currentDirPath, content, true);
        FileUtils.writeToFile(content, currentDirPath, fileName);
    }

    /**
     * 生成布局文件名称，不带.xml后缀，并且是经过驼峰转下划线处理
     * @return
     */
    protected String makeLayoutFileName() {
        if (layoutBox.isSelected()) {
            String fileName = nameTextField.getText().trim() + "Layout";
            return FileUtils.camelToUnderline(fileName);
        }
        return "";
    }

    protected String makeListItemLayoutFileName() {
        if (layoutBox.isSelected()) {
            String fileName = nameTextField.getText().trim() + "ItemLayout";
            return FileUtils.camelToUnderline(fileName);
        }
        return "";
    }

    protected String dealFile(String psiPath, String currentDirPath, String content, boolean isLayout) {
        if(!isLayout) {
            content = FileUtils.makePackageString(currentDirPath, kotlinBox != null && kotlinBox.isSelected()) + content;
        }
        content = content.replaceAll("\\$\\{name\\}", nameTextField.getText() + getNameSuffixStr());
        content = content.replaceAll("\\$\\{package\\}", FileUtils.pathToPackage(psiPath));
        content = content.replaceAll("\\$\\{modulePackage\\}", modulePackage);

        if(modulePackage != null && !"".equals(modulePackage)) {
            content = content.replaceAll("\\$\\{importR\\}", "import " + modulePackage + ".R");
            content = content.replaceAll("\\$\\{importBR\\}", "import " + modulePackage + ".BR");
        } else {
            content = content.replaceAll("\\$\\{importR\\}", "");
            content = content.replaceAll("\\$\\{importBR\\}", "");
        }

        if(isLayout && contextFileName != null) {
            String contextNameReplace = "";
            if(contextFileName.contains("Activity")) {
                contextNameReplace = ACTIVITY_DIR;
            } else if(contextFileName.contains("Fragment")) {
                contextNameReplace = FRAGMENT_DIR;
            } else if(contextFileName.contains("Dialog")) {
                contextNameReplace = DIALOG_DIR;
            }
            content = content.replaceAll("\\$\\{contextName\\}",
                    contextNameReplace + "." + contextFileName);
        }

        // 布局文件名称需要驼峰转下划线
        if (layoutBox.isSelected()) {
            content = content.replaceAll("\\$\\{layoutName\\}", layoutFileName);
            content = content.replaceAll("\\$\\{itemLayoutName\\}", itemLayoutFileName);
        }
        return content;
    }

    protected String getNameSuffixStr() {
        return "";
    }

}
