package org.slotterback.Sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public abstract class GenericSink {

    public final static GenericSink sinkBuilder(StreamExecutionEnvironment env,
                                                Map schemas,
                                                SingleOutputStreamOperator stream,
                                                String name,
                                                String type,
                                                Map config){

        //todo runnables
        if(type.equals("kafka")){
            return new KafkaSink(schemas, stream, config);
        }else if(type.equals("print")){
            return new PrintSink(stream, name);
        }
        return null;
    }
}
