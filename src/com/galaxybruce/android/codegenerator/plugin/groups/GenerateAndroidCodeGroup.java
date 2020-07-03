package com.galaxybruce.android.codegenerator.plugin.groups;

import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class GenerateAndroidCodeGroup extends DefaultActionGroup {

    @Override
    public boolean hideIfNoVisibleChildren() {
        return true;
    }
}
