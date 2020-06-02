package org.slotterback.Function;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.Set;

public abstract class GenericFunction {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericFunction functionBuilder(String entry, Map streamBuilder, Set<String> keys, StreamExecutionEnvironment env, SingleOutputStreamOperator stream){

        //TODO make string bound to function
        if(entry.equals("filter")){
            return new FilterFunction(env, keys, stream, streamBuilder);
        }else if(entry.equals("map")){
            return new MapFunction(env, keys, stream, streamBuilder);
        }
        return null;

    }
}