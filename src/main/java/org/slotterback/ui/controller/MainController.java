package org.slotterback.ui.controller;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.ArrayUtils;
import org.slotterback.FlinkBootConnector;
import org.slotterback.StreamBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
public class MainController {

    @Autowired
    private FlinkBootConnectorWrapper flinkBootConnectorWrapper;

    @Autowired
    private ParameterToolWrapper parameterToolWrapper;

    @Autowired
    private ApplicationArguments args;

    @GetMapping("/load")
    public String getOperatorJson() {
        return StreamBuilderUtil.getOperatorJson();
    }

    @PutMapping("/startWithoutJson")
    public String startJob() throws Exception {
        if(!flinkBootConnectorWrapper.getRunning()) {
            getConfig();
            FlinkBootConnector connector = new FlinkBootConnector(parameterToolWrapper.getParameterTool());
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob();
            flinkBootConnectorWrapper.setRunning(true);
            return "triggered start";
        }
        return "unable to start";
    }

    @PutMapping("/startWithJson")
    public String startJob(@RequestBody String jsonRaw) throws Exception {
        if(!flinkBootConnectorWrapper.getRunning()) {
            ParameterTool temp = getParametersWithRaw(jsonRaw);
            FlinkBootConnector connector = new FlinkBootConnector(temp);
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob();
            flinkBootConnectorWrapper.setRunning(true);
            return "triggered start";
        }
        return "unable to start";
    }

    @GetMapping("/stop")
    public String stopJob() throws Exception {
        if(flinkBootConnectorWrapper.getRunning()) {
            if (flinkBootConnectorWrapper != null) {
                FlinkBootConnector connector = flinkBootConnectorWrapper.getFlinkBootConnector();
                connector.stopFlinkJob();
                flinkBootConnectorWrapper.setRunning(false);
                return "triggered stop";
            }
        }
        return "unable to stop";
    }

    @GetMapping("/config")
    public String getConfig(){
        ParameterTool parameterTool = ParameterTool.fromArgs(args.getSourceArgs());
        parameterToolWrapper.setParameterTool(parameterTool);
        return parameterTool.toString();
    }

    private ParameterTool getParametersWithRaw(String jsonRaw) {
        getConfig();
        String[] newArgs = ArrayUtils.concat(
                args.getSourceArgs(),
                new String[]{"--streambuilder.json.raw", jsonRaw});
        ParameterTool parameterTool = ParameterTool.fromArgs(newArgs);
        return parameterTool;
    }

}
