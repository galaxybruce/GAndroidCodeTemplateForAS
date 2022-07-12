package com.galaxybruce.android.codegenerator.plugin.actions;

import com.galaxybruce.android.codegenerator.plugin.util.FileUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

/**
 * 模块模板
 */
public class AndroidModuleTemplateAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidModuleTemplateAction");

    protected Project project;

    protected JDialog jFrame;
    protected JTextField nameTextField;

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

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOG.info("android module template code creates start... ");

        project = event.getData(PlatformDataKeys.PROJECT);
        PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            Messages.showMessageDialog(project, "Please switch Project View Mode! ", "Generate Failed！", null);
            return;
        }

        initView();

        LOG.info("android module template code creates end... ");
    }

    private void initView() {
        // 创建弹框窗口
        jFrame = new JDialog();
        jFrame.setModal(true);

        // dialog中的面板设置布局样式
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

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
        jFrame.setSize(600, 300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private void dispose() {
        jFrame.dispose();
    }

    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please enter the name", "Info");
            return;
        }
        dispose();
        clickCreateFile(nameTextField.getText().trim());
        project.getProjectFile().refresh(false, true);
        Messages.showInfoMessage(project, "Enjoy Coding~", "Success!");
    }

    private void clickCreateFile(String moduleName) {
        String projectPath = project.getBasePath();
        File moduleTemplate = new File(projectPath, "module_packagedemo");
        File destModule = new File(projectPath, "module_" + moduleName);

        try {
            FileUtils.copyFolder(moduleTemplate, destModule);

            String packageDemo = "/src/androidTest/java/com/galaxybruce/".replace("/", File.separator) + "packagedemo";
            File exampleInstrumentedTest = new File(destModule + packageDemo + File.separator + "ExampleInstrumentedTest.java");
            FileUtils.replaceFileText(exampleInstrumentedTest.getAbsolutePath(), ".packagedemo", "." + moduleName);
            String packageModule = "/src/androidTest/java/com/galaxybruce/".replace("/", File.separator) + moduleName;
            new File(destModule + packageDemo).renameTo(new File(destModule + packageModule));

            packageDemo = "/src/test/java/com/galaxybruce/".replace("/", File.separator) + "packagedemo";
            File exampleUnitTest = new File(destModule + packageDemo + File.separator + "ExampleUnitTest.java");
            FileUtils.replaceFileText(exampleUnitTest.getAbsolutePath(), ".packagedemo", "." + moduleName);
            packageModule = "/src/test/java/com/galaxybruce/".replace("/", File.separator) + moduleName;
            new File(destModule + packageDemo).renameTo(new File(destModule + packageModule));

            packageDemo = "/src/main/java/com/galaxybruce/".replace("/", File.separator) + "packagedemo";
            packageModule = "/src/main/java/com/galaxybruce/".replace("/", File.separator) + moduleName;
            new File(destModule + packageDemo).renameTo(new File(destModule + packageModule));

            File manifestFile = new File(destModule + "/src/main/AndroidManifest.xml".replace("/", File.separator));
            FileUtils.replaceFileText(manifestFile.getAbsolutePath(), "packagedemo", moduleName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
