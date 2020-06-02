package org.slotterback.Sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Set;

public abstract class GenericSink {

    public final static GenericSink sinkBuilder(String entry, Map streamBuilder, Set<String> keys, Map schemas, StreamExecutionEnvironment env, SingleOutputStreamOperator stream){

        //todo runnables
        if(entry.equals("kafkaSink")){
            return new KafkaSink(env, keys, streamBuilder, schemas, stream);
        }else if(entry.equals("print")){
            return new PrintSink(stream);
        }
        return null;
    }
}
