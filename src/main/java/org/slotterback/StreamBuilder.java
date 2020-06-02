package org.slotterback;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.Function.GenericFunction;
import org.slotterback.Sink.GenericSink;
import org.slotterback.Source.GenericSource;

import java.util.*;

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
        sinkList.add("print");
        sinkList.add("kafkaSink");
    }

    public void buildStream(Map streamBuilder, StreamExecutionEnvironment env, Map schemas){
        this.env = env;
        this.schemas = schemas;
        Set<String> keys = streamBuilder.keySet();


        for(String entry : keys)
            buildStream(entry, (HashMap)streamBuilder.get(entry), null);
    }

    private void buildStream(String entry, Map streamBuilder, SingleOutputStreamOperator stream){
        Set<String> keys = streamBuilder.keySet();


        if(sourceList.contains(entry)){
            stream = GenericSource.sourceBuilder(entry, streamBuilder, keys, schemas, env).getSourceStream();
        }else if(functionList.contains(entry)){
            stream = GenericFunction.functionBuilder(entry, streamBuilder, keys, env, stream).getSourceStream();
        }else if(keyList.contains(entry)){

        }else if(sinkList.contains(entry)){
            //todo all these useless params..
            GenericSink.sinkBuilder(entry, streamBuilder, keys, schemas, env, stream);
        }else{

        }

        for(String key : keys){
            if(sourceList.contains(key)){
                buildStream(key, (Map) streamBuilder.get(key), null);
            }else if(functionList.contains(key)){
                buildStream(key, (Map) streamBuilder.get(key), stream);
            }else if(keyList.contains(key)){
                System.out.println("");
            }else if(sinkList.contains(key)){
                buildStream(key, (Map) streamBuilder.get(key), stream);
            }
        }
    }


}
