package org.slotterback.ui.controller.wrapper;

import org.apache.flink.api.java.utils.ParameterTool;
import org.springframework.stereotype.Service;

@Service
public class ParameterToolWrapper {

    private ParameterTool parameterTool;

    public ParameterTool getParameterTool() {
        return parameterTool;
    }

    public void setParameterTool(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }
}
