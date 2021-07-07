package com.galaxybruce.android.codegenerator.plugin.actions.snippet;

import com.galaxybruce.android.codegenerator.plugin.util.ClipboardHelper;
import com.galaxybruce.android.codegenerator.plugin.util.FileUtils;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * 代码片段生成
 */
public class AndroidCodeSnippetAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidCodeSnippetAction");

    private static final String SNIPPET_PATH_KEY = "code_snippet_path";

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    private static final int TOP_HEIGHT = 60;
    private static final int MENU_HEIGHT = 100;
    private static final int TEMPLATE_LIST_WIDTH = 230;


    private Project project;

    private JDialog jFrame;
    private JTextArea codeArea;
    private JPanel templateListPanel;
    private JTextField pathField;

    private String snippetPath;
    private ArrayList<TemplateInfo> templateInfoList = new ArrayList<>();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOG.info("android snippet creates start... ");
        project = event.getProject();
        initTemplateInfo(PropertiesComponent.getInstance().getValue(SNIPPET_PATH_KEY));
        initView();

        LOG.info("android snippet creates end... ");
    }

    private void initTemplateInfo(String path) {
        if(path == null || "".equals(path)) {
            return;
        }
        File dir = new File(path);
        if(!dir.exists()) {
            return;
        }
        File[] list = dir.listFiles();
        if(list == null || list.length == 0) {
            return;
        }

        templateInfoList.clear();
        for (File file : list) {
            templateInfoList.add(new TemplateInfo(file.getName(), file.getAbsolutePath()));
        }
        this.snippetPath = path;
    }

    private void initView() {
        // 创建弹框窗口
        jFrame = new JDialog();
        jFrame.setModal(true);

        // dialog中的面板设置布局样式
        Container container = jFrame.getContentPane();
        container.setLayout(new BorderLayout());
        // 主要区域
        JPanel mainPanel = createMainPanel();
        container.add(mainPanel, BorderLayout.CENTER);

        // 顶部选择模块文件存放路径
        createFileChoosePanel(mainPanel);
        this.pathField.setText(snippetPath);

        // 左边模板列表区域
        mainPanel.add(createTemplateListPanel(), BorderLayout.WEST);
        updateTemplateList();

        // 代码显示区域
        mainPanel.add(createTemplateCodePanel(), BorderLayout.CENTER);

        // 确定 取消按钮
        container.add(createMenuPanel(), BorderLayout.SOUTH);

        // 显示窗口
        jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
//        panel.setBackground(Color.LIGHT_GRAY);
        return panel;
    }

    private void createFileChoosePanel(JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setPreferredSize(new Dimension(FRAME_WIDTH, TOP_HEIGHT));
        panel.setBorder(BorderFactory.createTitledBorder("Template path"));
        mainPanel.add(panel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Template file path: ");
        titleLabel.setBorder(JBUI.Borders.emptyLeft(10));
        titleLabel.setFont(new Font(null, Font.PLAIN, 13));
//        titleLabel.setPreferredSize(new Dimension(TEMPLATE_LIST_WIDTH, 50));
        panel.add(titleLabel);

        // 用JPanel套起来，可以自适应布局变大变小
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout());
        pathPanel.setBorder(JBUI.Borders.empty(0, 10));
        JTextField pathField = new JTextField();
        pathField.setFont(new Font(null, Font.PLAIN, 13));
        pathField.setPreferredSize(new Dimension(0, 35));
        pathPanel.add(pathField);
        panel.add(pathPanel);

        JButton openChoose = new JButton("...");
        openChoose.addActionListener(actionListener);
        openChoose.setPreferredSize(new Dimension(35, 35));
        panel.add(openChoose);

        this.pathField = pathField;
    }

    private JPanel createTemplateListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Template list"));
        panel.setPreferredSize(new Dimension(TEMPLATE_LIST_WIDTH, FRAME_HEIGHT - MENU_HEIGHT));

        JPanel templateListPanel = new JPanel();
        templateListPanel.setLayout(new BoxLayout(templateListPanel, BoxLayout.Y_AXIS));
        JBScrollPane scrollPane = new JBScrollPane(templateListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        this.templateListPanel = templateListPanel;
        return panel;
    }

    private Component createTemplateCodePanel() {
        // template对应的代码Panel
        JTextArea codeArea = new JTextArea();
//        codeArea.setBorder(new LineBorder(JBColor.GRAY));

        JBScrollPane panel = new JBScrollPane(codeArea);
        panel.setBorder(BorderFactory.createTitledBorder("Code"));
        this.codeArea = codeArea;
        return panel;
    }

    private Component createMenuPanel() {
        VerticalBox verticalBox = new VerticalBox();
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.setPreferredSize(new Dimension(FRAME_WIDTH, MENU_HEIGHT));

        JPanel panel = new JPanel();
        HorizontalBox horizontalBox = new HorizontalBox();
        horizontalBox.add(Box.createHorizontalGlue());
        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);
        JButton ok = new JButton("Copy");
        ok.setForeground(JBColor.GREEN);
        ok.addActionListener(actionListener);
        horizontalBox.add(cancel);
        horizontalBox.add( Box.createHorizontalStrut(30) );
        horizontalBox.add(ok);
        horizontalBox.add( Box.createHorizontalStrut(30) );
        horizontalBox.setPreferredSize(new Dimension(FRAME_WIDTH, 40));
        // 必须用panel嵌套一下，horizontalBox直接嵌套在verticalBox中无效
        panel.add(horizontalBox);

        verticalBox.add(panel);
        return verticalBox;
    }

    private void updateTemplateList() {
        templateListPanel.removeAll();
        ButtonGroup templateGroup = new ButtonGroup();
        for (TemplateInfo info : templateInfoList) {
            String displayName = info.name;
//            if(displayName.indexOf("/") > 0) {
//                displayName = displayName.substring(displayName.indexOf("/") + 1);
//            }
//            if(displayName.indexOf(".") > 0) {
//                displayName = displayName.substring(0, displayName.indexOf("."));
//            }

            JRadioButton radioButton = new JRadioButton(displayName, false);
            radioButton.setActionCommand(info.name);
            radioButton.addActionListener(radioActionListener);
            templateGroup.add(radioButton);

            templateListPanel.add(radioButton);
        }
        templateListPanel.updateUI();
    }

    private void chooseTemplatePath() {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true,
                false , false, false, false);
        VirtualFile[] virtualFiles = FileChooser.chooseFiles(descriptor, project, null);
        if(virtualFiles.length == 0) {
            return;
        }
        String path = virtualFiles[0].getPath();
        this.pathField.setText(path);
        PropertiesComponent.getInstance().setValue(SNIPPET_PATH_KEY, path);
        initTemplateInfo(path);
        updateTemplateList();
    }

    private void dispose() {
        jFrame.dispose();
    }

    private void save() {
        ClipboardHelper.copy(codeArea.getText());
        dispose();
    }

    private ActionListener radioActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            for (TemplateInfo info : templateInfoList) {
                if(info.name.equals(cmd)) {
//                    String content = FileUtils.readFile(this.getClass(), info.templateName);
                    String content = FileUtils.readToString(info.path);
                    codeArea.setText(content);
                    break;
                }
            }
        }
    };

    protected ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("Cancel")) {
                dispose();
            } else if (cmd.equals("Copy")) {
                save();
            } else if (cmd.equals("...")) {
                chooseTemplatePath();
            }
        }
    };

    private static class TemplateInfo {
        String name;
        String path;

        public TemplateInfo(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
}
