package com.subatomicsoftware.autoflink.ui.controller.wrapper;

import org.springframework.stereotype.Service;

@Service
public class EditorWrapper {

    private String storedEditor;
    private String log;
    private String schemas;

    public String getSchemas() {
        return schemas;
    }

    public void setSchemas(String schemas) {
        this.schemas = schemas;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getStoredEditor() {
        return storedEditor;
    }

    public void setStoredEditor(String storedEditor) {
        this.storedEditor = storedEditor;
    }
}
