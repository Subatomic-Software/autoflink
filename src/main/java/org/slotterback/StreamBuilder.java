package org.slotterback;

import com.google.gson.JsonObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.Function.GenericFunction;
import org.slotterback.Source.GenericSource;

import java.util.*;
import java.util.stream.Stream;

public class StreamBuilder {

    private StreamExecutionEnvironment env;
    private Map schemas;

    private List<String> sourceList;
    private List<String>  functionList;
    private List<String>  keyList;
    private List<String>  sinkList;

    public StreamBuilder(){
        //TODO call each generic to get list
        sourceList = new ArrayList();
        sourceList.add("kafkaSource");

        functionList = new ArrayList();
        functionList.add("filter");
        functionList.add("map");

        keyList = new ArrayList();

        sinkList = new ArrayList();
        sinkList.add("sink");
    }

    public void buildStream(JsonObject jsonObject, StreamExecutionEnvironment env, Map schemas){
        this.env = env;
        this.schemas = schemas;
        Set<String> keys = jsonObject.keySet();

        for(String entry : keys)
            buildStream(entry, jsonObject.getAsJsonObject(entry), null);
    }

    private DataStream buildStream(String entry, JsonObject jsonObject, SingleOutputStreamOperator stream){
        Set<String> keys = jsonObject.keySet();


        if(sourceList.contains(entry)){
            String schemaKey = jsonObject.get("schema").toString().replaceAll("\"","");
            stream = GenericSource.sourceBuilder(entry, jsonObject, keys, env, schemas.get(schemaKey).toString()).getSourceStream();
        }else if(functionList.contains(entry)){
            stream = GenericFunction.functionBuilder(entry, jsonObject, keys, env, stream).getSourceStream();
        }else if(keyList.contains(entry)){

        }else if(sinkList.contains(entry)){
            stream.print();
        }else{

        }

        DataStream finalStream = stream;

        for(String key : keys){
            if(sourceList.contains(key)){
                buildStream(key, jsonObject.getAsJsonObject(key), null);
            }else if(functionList.contains(key)){
                buildStream(key, jsonObject.getAsJsonObject(key), stream);
            }else if(keyList.contains(key)){
                System.out.println("");
            }else if(sinkList.contains(key)){
                buildStream(key, jsonObject.getAsJsonObject(key), stream);
            }
        }



        return null;
    }


}
