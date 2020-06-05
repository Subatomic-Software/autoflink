package org.slotterback.operator;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public abstract class GenericOperator {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericOperator functionBuilder(StreamExecutionEnvironment env, Map schemas, SingleOutputStreamOperator stream, String name, String type, Map config){

        //TODO make string bound to function
        if(type.equals("filter")){
            return new FilterOperator(env, stream, config);
        }else if(type.equals("map")){
            return new MapOperator(env, schemas, stream, name, type, config);
        }
        return null;

    }
}