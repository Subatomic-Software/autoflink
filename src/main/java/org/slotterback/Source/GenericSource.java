package org.slotterback.Source;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Set;

public abstract class GenericSource {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericSource sourceBuilder(String entry, Map streamBuilder, Set<String> keys, Map schemas, StreamExecutionEnvironment env){

        if(entry.equals("kafkaSource")){
            return new KafkaSource(env, keys, streamBuilder, schemas);
        }
        return null;
    }

}
