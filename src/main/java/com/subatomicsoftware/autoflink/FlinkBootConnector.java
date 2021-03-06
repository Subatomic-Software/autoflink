package com.subatomicsoftware.autoflink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlinkBootConnector {

    private StreamExecutionEnvironment env;
    private String killDirectory;
    private String jobName;

    public FlinkBootConnector(ParameterTool parameterTool) throws IOException {

        jobName = parameterTool.get("streambuilder.jobname", "AutoFlinkJob");
        String jsonFile = parameterTool.get("streambuilder.json.file", null);
        String jsonRaw = parameterTool.get("streambuilder.json.raw", null);
        String schemasRaw = parameterTool.get("streambuilder.schemas.raw", null);
        String schemasString = parameterTool.get("streambuilder.avro", "");
        killDirectory = null;

        boolean localCluster = parameterTool.getBoolean("streambuilder.localcluster", false);
        int parallelism = parameterTool.getInt("streambuilder.localcluster.parallelism", 1);
        if(localCluster){
            env = StreamExecutionEnvironment.createLocalEnvironment(parallelism);
            killDirectory = parameterTool.get("streambuilder.killdirectory", null);
        }else{
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map streamBuilder;
        Map schemas;
        if(jsonRaw != null){
            streamBuilder = objectMapper.readValue(jsonRaw, HashMap.class);
            schemas = buildSchemasRaw(schemasRaw, objectMapper);
        }else {
            streamBuilder = objectMapper.readValue(new FileReader(jsonFile), HashMap.class);
            schemas = buildSchemas(schemasString, objectMapper);
        }

        StreamBuilder builder = new StreamBuilder();
        builder.buildStream(streamBuilder, env, schemas, killDirectory);
    }

    private Map buildSchemas(String schemasString, ObjectMapper mapper) throws IOException {
        Map schemas = new HashMap<String, String>();
        schemas.put("", "");
        schemas.put(null, "");
        for (String schema: schemasString.split(",")){
            Map schemaMap = mapper.readValue(new FileReader(schema), HashMap.class);
            schemas.put(schemaMap.get("name"), schema);
        }
        return schemas;
    }

    private Map buildSchemasRaw(String schemasRaw, ObjectMapper mapper) throws IOException {
        HashMap rawSchemas = mapper.readValue(schemasRaw, HashMap.class);
        Set<String> keySet = rawSchemas.keySet();
        Map schemas = new HashMap();
        schemas.put("", "");
        schemas.put(null, "");
        for(String key : keySet){
            String json = new ObjectMapper().writeValueAsString(rawSchemas.get(key));
            schemas.put(key, json);
        }
        return schemas;
    }

    public JobClient startFlinkJob() throws Exception {
        if(killDirectory != null){
            try {
                FileUtils.cleanDirectory(new File(killDirectory));
            }catch (FileNotFoundException e){
                new File(killDirectory).mkdirs();
            }
        }
        JobClient client = env.executeAsync(jobName);
        return client;
    }

    public void stopFlinkJob() throws IOException {
        BufferedWriter bw = Files.newBufferedWriter(new File(killDirectory+"death").toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        bw.write("killing " + jobName + " at " + LocalDateTime.now().toString());
        bw.flush();
        bw.close();
    }
}
