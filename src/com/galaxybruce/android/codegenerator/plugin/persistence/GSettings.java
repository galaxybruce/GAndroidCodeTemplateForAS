package com.galaxybruce.android.codegenerator.plugin.persistence;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CodeGeneratorSettings",
        storages = {
                @Storage(StoragePathMacros.WORKSPACE_FILE),
                @Storage("android_code_generator_settings.xml")
        }
)
public class GSettings implements PersistentStateComponent<GSettings> {

    private String sourcePath;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public static GSettings getInstance(Project project) {
        return ServiceManager.getService(project, GSettings.class);
    }

    @Nullable
    @Override
    public GSettings getState() {
        return this;
    }

    @Override
    public void loadState(GSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }
}
