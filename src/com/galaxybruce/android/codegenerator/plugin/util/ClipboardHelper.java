package com.galaxybruce.android.codegenerator.plugin.util;

import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.StringSelection;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class ClipboardHelper {

    public static void copy(String generatedCode) {
        CopyPasteManager.getInstance().setContents(new StringSelection(generatedCode));
    }
}
