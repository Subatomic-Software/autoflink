package org.slotterback.Source;

import com.google.gson.JsonObject;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Set;

public abstract class GenericSource {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericSource sourceBuilder(String entry, JsonObject obj, Set<String> keys, StreamExecutionEnvironment env, String schema){

        keys.remove("schema");
        if(entry.equals("kafkaSource")){
            return new KafkaSource(env, keys, schema, obj);
        }
        return null;
    }

}
