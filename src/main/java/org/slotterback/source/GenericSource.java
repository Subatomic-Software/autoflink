package org.slotterback.source;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public abstract class GenericSource {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericSource sourceBuilder(StreamExecutionEnvironment env,
                                                    Map schemas,
                                                    SingleOutputStreamOperator stream,
                                                    String name,
                                                    String type,
                                                    Map config){

        if(type.equals("kafka")){
            return new KafkaSource(env, schemas, config);
        }if(type.equals("file")){
            return new FileSource(env, schemas, config);
        }
        return null;
    }

}
