package org.slotterback.Source;

import com.google.gson.JsonObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public abstract class GenericSource {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericSource sourceBuilder(JsonObject obj, StreamExecutionEnvironment env, String schema){

        if(obj.get("type").toString().replaceAll("\"","").equals("kafka")){
            return new KafkaSource(env, schema, obj);
        }
        return null;
    }

}
