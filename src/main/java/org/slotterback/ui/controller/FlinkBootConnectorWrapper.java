package org.slotterback.ui.controller;

import org.apache.flink.core.execution.JobClient;
import org.slotterback.FlinkBootConnector;
import org.springframework.stereotype.Service;

@Service
public class FlinkBootConnectorWrapper {

    private Boolean isRunning = false;
    private FlinkBootConnector flinkBootConnector;
    private JobClient client;

    public JobClient getClient() {
        return client;
    }

    public void setClient(JobClient client) {
        this.client = client;
    }

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
