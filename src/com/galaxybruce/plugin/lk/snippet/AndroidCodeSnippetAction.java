package com.galaxybruce.plugin.lk.snippet;

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

/**
 * 代码片段生成
 */
public class AndroidCodeSnippetAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("AndroidCodeSnippetAction");

    protected Project project;
    protected String psiPath;

    protected JDialog jFrame;


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOG.info("android snippet creates start... ");

        project = event.getData(PlatformDataKeys.PROJECT);
//        PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
//        psiPath = psiElement.toString();
//        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        LOG.info("target path is: " + psiPath);

        initView();

        LOG.info("android snippet creates end... ");
    }

    private void initView() {
        // 创建弹框窗口
        jFrame = new JDialog();
        jFrame.setModal(true);


        // dialog中的面板设置布局样式
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        // 添加一个Panel，用于提供可用的模板类型
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(1, 2));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));

        JRadioButton recyclerView_gridLayout = new JRadioButton("RecyclerView GridLayout", true);
        recyclerView_gridLayout.setActionCommand("recyclerView_gridLayout");
        recyclerView_gridLayout.addActionListener(radioActionListener);

        template.add(recyclerView_gridLayout);
        template.setPreferredSize(new Dimension(600, 300));
        container.add(template);


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
        menu.setPreferredSize(new Dimension(600, 100));
        container.add(menu);


        // 显示窗口
        jFrame.setSize(600, 400);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private ActionListener radioActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "recyclerView_gridLayout":

                    break;
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

        Messages.showInfoMessage(project, "敬请期待", "Hint!");
        dispose();
    }
}
