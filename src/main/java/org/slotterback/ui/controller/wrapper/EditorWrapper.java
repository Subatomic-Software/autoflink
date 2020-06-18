package org.slotterback.ui.controller.wrapper;

import org.springframework.stereotype.Service;

@Service
public class EditorWrapper {

    private String storedEditor;
    private String log;

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
