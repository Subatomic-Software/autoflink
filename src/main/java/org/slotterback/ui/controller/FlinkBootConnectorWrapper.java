package org.slotterback.ui.controller;

import org.slotterback.FlinkBootConnector;
import org.springframework.stereotype.Service;

@Service
public class FlinkBootConnectorWrapper {

    private Boolean isRunning = false;
    private FlinkBootConnector flinkBootConnector;

    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public FlinkBootConnector getFlinkBootConnector() {
        return flinkBootConnector;
    }

    public void setFlinkBootConnector(FlinkBootConnector flinkBootConnector) {
        this.flinkBootConnector = flinkBootConnector;
    }
}
