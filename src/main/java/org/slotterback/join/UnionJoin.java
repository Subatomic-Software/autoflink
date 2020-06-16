package org.slotterback.join;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.Map;

public class UnionJoin extends GenericJoin{

    private SingleOutputStreamOperator stream;

    public UnionJoin(SingleOutputStreamOperator stream1, SingleOutputStreamOperator stream2, String name){
        this.stream = stream1.union(stream2).filter(new FilterFunction() {
            @Override
            public boolean filter(Object value) {
                return true;
            }
        }).name(name);
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return this.stream;
    }
}
