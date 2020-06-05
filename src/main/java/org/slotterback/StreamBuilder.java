package org.slotterback;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.operator.GenericOperator;
import org.slotterback.sink.GenericSink;
import org.slotterback.source.GenericSource;

import java.util.*;

public class StreamBuilder {

    private StreamExecutionEnvironment env;
    private Map schemas;

    public void buildStream(Map streamBuilder, StreamExecutionEnvironment env, Map schemas){
        this.env = env;
        this.schemas = schemas;
        Set<String> keys = streamBuilder.keySet();

        for(String entry : keys) {
            buildStream(entry, (Map) streamBuilder.get(entry), null);
        }
    }

    private void buildStream(String entry, Map streamBuilder, SingleOutputStreamOperator stream){

        String function = StreamBuilderUtil.Generic.getFunction(streamBuilder);
        String type = StreamBuilderUtil.Generic.getType(streamBuilder);
        Map config = (Map) streamBuilder.remove(type);

        if(function.equals("source")){
            stream = GenericSource.sourceBuilder(env, schemas, stream, entry, type, config).getSourceStream();
        }else if(function.equals("operator")){
            stream = GenericOperator.functionBuilder(env, schemas, stream, entry, type, config).getSourceStream();
        }else if(function.equals("key")){

        }else if(function.equals("sink")){
            GenericSink.sinkBuilder(env, schemas, stream, entry, type, config);
        }else{

        }

        Set<String> keys = streamBuilder.keySet();
        for(String key : keys){
            buildStream(key, (Map) streamBuilder.get(key), stream);
        }

    }
}
