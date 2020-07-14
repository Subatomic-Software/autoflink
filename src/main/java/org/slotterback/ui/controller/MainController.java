package org.slotterback.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.util.ArrayUtils;
import org.slotterback.FlinkBootConnector;
import org.slotterback.StreamBuilderUtil;
import org.slotterback.ui.controller.wrapper.EditorWrapper;
import org.slotterback.ui.controller.wrapper.FlinkBootConnectorWrapper;
import org.slotterback.ui.controller.wrapper.LogCatcherWrapper;
import org.slotterback.ui.controller.wrapper.ParameterToolWrapper;
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
    private EditorWrapper editorWrapper;

    @PostConstruct
    public void initialize() {
        editorWrapper.setLog("");
        editorWrapper.setSchemas("");
        logCatcher.setOutputStream(new ByteArrayOutputStream());
        logCatcher.setPrintStream(new PrintStream(logCatcher.getOutputStream()));
        System.setOut(logCatcher.getPrintStream());
    }

    @GetMapping("/load")
    public String getLoadEditor() throws JsonProcessingException {
        Map response = new HashMap();

        boolean isRunning;
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client == null || client.getJobStatus().isDone()){
            isRunning = false;
            response.put("jsonEditor", editorWrapper.getStoredEditor());
            response.put("log", editorWrapper.getLog());
        }else{
            isRunning = !(client.getJobStatus().isDone());
            response.put("jsonDriver", flinkBootConnectorWrapper.getDriverJson());
            response.put("log", editorWrapper.getLog()+getOutputLogs());
        }

        response.put("schemas", editorWrapper.getSchemas());
        response.put("isRunning", isRunning);
        response.put("jsonOperators", StreamBuilderUtil.getOperatorJson());

        return new ObjectMapper().writeValueAsString(response);
    }

    @PutMapping("/saveEditor")
    public void storeEditor(@RequestBody Map<String, Object> storeEditor){
        System.err.println(storeEditor);
        System.err.println(storeEditor.get("jsonEditor").toString());
        editorWrapper.setStoredEditor(storeEditor.get("jsonEditor").toString());
        editorWrapper.setLog(storeEditor.get("log").toString());
        editorWrapper.setSchemas(storeEditor.get("schemas").toString());
    }

    /*
    @PutMapping("/startWithoutJson")
    public String startJob() throws Exception {
        Map response = new HashMap();
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client == null || client.getJobStatus().isDone()) {
            getConfig();
            FlinkBootConnector connector = new FlinkBootConnector(parameterToolWrapper.getParameterTool());
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.setClient(flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob());
            return "triggered start";
        }
        return "unable to start";
    }
    */

    //@PutMapping("/startWithJson")
    @PutMapping("/start")
    public String startJob(@RequestBody Map<String, Object> startStream) throws Exception {
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client == null || client.getJobStatus().isDone()) {
            String driver = (String) startStream.get("driver");
            String schemas = (String) startStream.get("schemas");
            ParameterTool temp = getParametersWithRaw(driver, schemas);
            FlinkBootConnector connector = new FlinkBootConnector(temp);
            flinkBootConnectorWrapper.setDriverJson(driver);
            flinkBootConnectorWrapper.setFlinkBootConnector(connector);
            flinkBootConnectorWrapper.setClient(flinkBootConnectorWrapper.getFlinkBootConnector().startFlinkJob());
            return "triggered start";
        }
        return "unable to start";
    }

    @GetMapping("/stop")
    public String stopJob() throws Exception {
        JobClient client = flinkBootConnectorWrapper.getClient();
        if(client != null && !client.getJobStatus().isDone()) {
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
        response.put("logAppend", getOutputLogs());

        return new ObjectMapper().writeValueAsString(response);
    }

    private ParameterTool getParametersWithRaw(String jsonDriver, String schemas) {
        getConfig();
        String[] newArgs = ArrayUtils.concat(
                args.getSourceArgs(),
                new String[]
                        {
                                "--streambuilder.json.raw",
                                jsonDriver,
                                "--streambuilder.schemas.raw",
                                schemas
                        });
        ParameterTool parameterTool = ParameterTool.fromArgs(newArgs);
        return parameterTool;
    }

    private String getOutputLogs(){
        System.out.flush();
        String log = logCatcher.getOutputStream().toString();
        logCatcher.getOutputStream().reset();
        return log;
    }

}
