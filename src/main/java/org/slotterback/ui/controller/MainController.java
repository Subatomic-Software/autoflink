package org.slotterback.ui.controller;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slotterback.FlinkBootConnector;
import org.slotterback.StreamBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class MainController {

    @Autowired
    private FlinkBootConnectorWrapper flinkBootConnectorWrapper;

    @Autowired
    private ParameterToolWrapper parameterToolWrapper;

    @Autowired
    private ApplicationArguments args;

    @GetMapping("/")
    public String getOperatorJson() {
        return StreamBuilderUtil.getOperatorJson();
    }

    @GetMapping("/start")
    public String startJob() throws Exception {
        getConfig();
        FlinkBootConnector connector = new FlinkBootConnector(parameterToolWrapper.getParameterTool());
        flinkBootConnectorWrapper.setFlinkBootConnector(connector);
        flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob();
        return null;
    }

    @GetMapping("/stop")
    public String stopJob() throws Exception {
        if(flinkBootConnectorWrapper != null){
            FlinkBootConnector connector = flinkBootConnectorWrapper.getFlinkBootConnector();
            connector.stopFlinkJob();
        }
        return null;
    }

    @GetMapping("/config")
    public String getConfig(){
        ParameterTool parameterTool = ParameterTool.fromArgs(args.getSourceArgs());
        parameterToolWrapper.setParameterTool(parameterTool);
        return parameterTool.toString();
    }

}
