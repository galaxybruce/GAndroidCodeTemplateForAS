package com.galaxybruce.android.codegenerator.plugin.actions.snippet;

import com.galaxybruce.android.codegenerator.plugin.util.ClipboardHelper;
import com.galaxybruce.android.codegenerator.plugin.util.FileUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * 代码片段生成
 */
public class AndroidCodeSnippetAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidCodeSnippetAction");

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    private static final int MENU_HEIGHT = 100;
    private static final int TEMPLATE_LIST_WIDTH = 230;



    protected JDialog jFrame;
    private JTextArea codeArea;

    private ArrayList<TemplateInfo> templateInfoList;

    private void initTemplateInfo() {
        templateInfoList = new ArrayList<>();

        templateInfoList.add(new TemplateInfo("snippet/RecyclerViewGridLayout.txt"));
        templateInfoList.add(new TemplateInfo("snippet/LocalSingleton_Java.txt"));
        templateInfoList.add(new TemplateInfo("snippet/LocalSingleton_kt.txt"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOG.info("android snippet creates start... ");

        initTemplateInfo();
        initView();

        LOG.info("android snippet creates end... ");
    }

    private void initView() {
        // 创建弹框窗口
        jFrame = new JDialog();
        jFrame.setModal(true);


        // dialog中的面板设置布局样式
        Container container = jFrame.getContentPane();
        container.setLayout(new BorderLayout());

        // 主要区域
        JPanel mainPanel = createMainPanel(container);
        // 左边类型区域
        JPanel templateTypePanel = createTemplateTypePanel(mainPanel);
        initTemplateTypes(templateTypePanel);

        // 代码显示区域
        codeArea = createTemplateCodePanel(mainPanel);

        // 添加一个Panel，确定 取消按钮
        createMenuPanel(container);

        // 显示窗口
        jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private void createMenuPanel(Container container) {
        VerticalBox verticalBox = new VerticalBox();
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.setPreferredSize(new Dimension(FRAME_WIDTH, MENU_HEIGHT));
        container.add(verticalBox, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        HorizontalBox horizontalBox = new HorizontalBox();
        horizontalBox.add( Box.createHorizontalGlue() );
        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);
        JButton ok = new JButton("Copy Code To Clipboard");
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
    }

    private JPanel createMainPanel(Container container) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        container.add(panel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTemplateTypePanel(JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setPreferredSize(new Dimension(TEMPLATE_LIST_WIDTH, FRAME_HEIGHT - MENU_HEIGHT));
        mainPanel.add(panel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Template Types");
        titleLabel.setBorder(JBUI.Borders.emptyLeft(20));
        titleLabel.setFont(new Font(null, Font.BOLD, 15));
        titleLabel.setPreferredSize(new Dimension(TEMPLATE_LIST_WIDTH, 50));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel templateTypePanel = new JPanel();
        templateTypePanel.setLayout(new BoxLayout(templateTypePanel, BoxLayout.Y_AXIS));
        JBScrollPane scrollPane = new JBScrollPane(templateTypePanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return templateTypePanel;
    }

    private JTextArea createTemplateCodePanel(JPanel mainPanel) {
        // template对应的代码Panel
        JTextArea codeArea = new JTextArea();
        codeArea.setBorder(new LineBorder(JBColor.GRAY));

        JBScrollPane panel = new JBScrollPane(codeArea);
        mainPanel.add(panel, BorderLayout.CENTER);
        return codeArea;
    }

    private void initTemplateTypes(JPanel templateTypePanel) {
        ButtonGroup templateGroup = new ButtonGroup();
        for (TemplateInfo info : templateInfoList) {
            String displayName = info.templateName;
//            displayName = displayName.replaceAll("\\s+", "");
            if(displayName.indexOf("/") > 0) {
                displayName = displayName.substring(displayName.indexOf("/") + 1);
            }
            if(displayName.indexOf(".") > 0) {
                displayName = displayName.substring(0, displayName.indexOf("."));
            }

            JRadioButton radioButton = new JRadioButton(displayName, false);
            radioButton.setActionCommand(info.templateName);
            radioButton.addActionListener(radioActionListener);
            templateTypePanel.add(radioButton);
            templateGroup.add(radioButton);
        }
    }

    private ActionListener radioActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            for (TemplateInfo info : templateInfoList) {
                if(info.templateName.equals(cmd)) {
                    String content = FileUtils.readFile(this.getClass(), info.templateName);
                    codeArea.setText(content);
                    break;
                }
            }
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
        ClipboardHelper.copy(codeArea.getText());
        dispose();
    }

    private static class TemplateInfo {
        String templateName;

        public TemplateInfo(String templateName) {
            this.templateName = templateName;
        }
    }
}
