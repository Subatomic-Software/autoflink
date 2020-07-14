package org.slotterback.ui.controller.wrapper;

import org.apache.flink.core.execution.JobClient;
import org.slotterback.FlinkBootConnector;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlinkBootConnectorWrapper {

    private Boolean isRunning = false;
    private FlinkBootConnector flinkBootConnector;
    private JobClient client;
    private String driverJson;

    public String getDriverJson() {
        return driverJson;
    }

    public void setDriverJson(String driverJson) {
        this.driverJson = driverJson;
    }

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
