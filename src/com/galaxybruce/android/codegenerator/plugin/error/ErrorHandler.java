package com.galaxybruce.android.codegenerator.plugin.error;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class ErrorHandler {

    public void handleError(Project project, Exception exception) {
        exception.printStackTrace();
        showErrorMessage(project, exception);
    }

    private void showErrorMessage(Project project, Exception e) {
        showErrorMessage(project, e.getMessage());
    }

    private void showErrorMessage(Project project, String message) {
        Messages.showErrorDialog(project, message, "Android Code Generate Plugin");
    }
}
