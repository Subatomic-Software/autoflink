package org.slotterback.ui.controller;

import org.slotterback.FlinkBootConnector;
import org.springframework.stereotype.Service;

@Service
public class FlinkBootConnectorWrapper {

    private FlinkBootConnector flinkBootConnector;

    public FlinkBootConnector getFlinkBootConnector() {
        return flinkBootConnector;
    }

    public void setFlinkBootConnector(FlinkBootConnector flinkBootConnector) {
        this.flinkBootConnector = flinkBootConnector;
    }
}
