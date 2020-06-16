package org.slotterback.join;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.operator.FilterOperator;
import org.slotterback.operator.GenericOperator;
import org.slotterback.operator.MapOperator;

import java.util.Map;

public abstract class GenericJoin {
    public abstract SingleOutputStreamOperator<Map> getSourceStream();

    public final static GenericJoin functionBuilder(StreamExecutionEnvironment env,
                                                    Map schemas,
                                                    SingleOutputStreamOperator stream1,
                                                    SingleOutputStreamOperator stream2,
                                                    String name,
                                                    String type,
                                                    Map config){

        //TODO make string bound to function
        if(type.equals("union")){
            return new UnionJoin(stream1, stream2, name);
        }
        return null;

    }


}
