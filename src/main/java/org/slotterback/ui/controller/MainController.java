package org.slotterback.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.util.ArrayUtils;
import org.slotterback.FlinkBootConnector;
import org.slotterback.StreamBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class MainController {

    @Autowired
    private FlinkBootConnectorWrapper flinkBootConnectorWrapper;

    @Autowired
    private ParameterToolWrapper parameterToolWrapper;

    @Autowired
    private ApplicationArguments args;

    @Autowired
    private LogCatcherWrapper logCatcher;

    @Autowired


    @PostConstruct
    public void initialize() {
        logCatcher.setOutputStream(new ByteArrayOutputStream());
        logCatcher.setPrintStream(new PrintStream(logCatcher.getOutputStream()));
        System.setOut(logCatcher.getPrintStream());
    }

    @GetMapping("/load")
    public String getOperatorJson() {
        return StreamBuilderUtil.getOperatorJson();
    }

    @PutMapping("/startWithoutJson")
    public String startJob() throws Exception {
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client == null || client.getJobStatus().isDone()) {
            getConfig();
            FlinkBootConnector connector = new FlinkBootConnector(parameterToolWrapper.getParameterTool());
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.setClient(flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob());
            //flinkBootConnectorWrapper.setRunning(true);
            return "triggered start";
        }
        return "unable to start";
    }

    @PutMapping("/startWithJson")
    public String startJob(@RequestBody String jsonRaw) throws Exception {
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client == null || client.getJobStatus().isDone()) {
            ParameterTool temp = getParametersWithRaw(jsonRaw);
            FlinkBootConnector connector = new FlinkBootConnector(temp);
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.setClient(flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob());
            //flinkBootConnectorWrapper.setRunning(true);
            return "triggered start";
        }
        return "unable to start";
    }

    @GetMapping("/stop")
    public String stopJob() throws Exception {
        if(!flinkBootConnectorWrapper.getClient().getJobStatus().isDone()) {
            if (flinkBootConnectorWrapper != null) {
                FlinkBootConnector connector = flinkBootConnectorWrapper.getFlinkBootConnector();
                connector.stopFlinkJob();
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

    @GetMapping("/status")
    public String getStatus() throws IOException {
        Map response = new HashMap();
        boolean isRunning;
        JobClient client = flinkBootConnectorWrapper.getClient();

        if(client == null){
            isRunning = false;
        }else{
            isRunning = !(client.getJobStatus().isDone());
        }

        response.put("isRunning", isRunning);
        response.put("logAppend", logCatcher.getOutputStream().toString());
        System.out.flush();
        logCatcher.getOutputStream().reset();

        return new ObjectMapper().writeValueAsString(response);
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
