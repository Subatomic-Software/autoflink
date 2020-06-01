package org.slotterback.Function;

import com.google.gson.JsonObject;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Set;

public abstract class GenericFunction {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericFunction functionBuilder(String entry, JsonObject obj, Set<String> keys, StreamExecutionEnvironment env, SingleOutputStreamOperator stream){

        //TODO make string bound to function
        if(entry.equals("filter")){
            return new FilterFunction(env, keys, stream, obj);
        }else if(entry.equals("map")){
            return new MapFunction(env, keys, stream, obj);
        }
        return null;

    }
}