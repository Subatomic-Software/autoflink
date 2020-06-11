package org.slotterback.operator;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.GenericUtil;
import org.slotterback.StreamBuilderUtil;

import java.io.Serializable;
import java.util.Map;

public class FilterOperator extends GenericOperator {

    private interface DynamicFunction extends Serializable {
        Boolean compare(Object obj1, Object obj2);
    }

    private DynamicFunction eqCompare = (obj1, obj2) -> { return obj1.equals(obj2); };
    private DynamicFunction neqCompare = (obj1, obj2) -> { return !obj1.equals(obj2); };
    private DynamicFunction ltCompare = (obj1, obj2) -> { return (Double) obj1 < (Double) obj2; };
    private DynamicFunction gtCompare = (obj1, obj2) -> { return (Double) obj1 > (Double) obj2; };

    private SingleOutputStreamOperator stream;

    public FilterOperator(StreamExecutionEnvironment env, SingleOutputStreamOperator stream, Map config) {

        String function = StreamBuilderUtil.Base.Operator.FilterOperator.getFunction(config);
        String value = StreamBuilderUtil.Base.Operator.FilterOperator.getValue(config);
        Object target = StreamBuilderUtil.Base.Operator.FilterOperator.getTarget(config);

        final DynamicFunction dynamicFunction;
        if(function.equals("==")){
            dynamicFunction = eqCompare;
        }else if(function.equals("!=")){
            dynamicFunction = neqCompare;
        }else if(function.equals("<")){
            dynamicFunction = ltCompare;
        }else if(function.equals(">")){
            dynamicFunction = gtCompare;
        }else{
            dynamicFunction = null;
        }

        stream = stream.filter(new RichFilterFunction<Map>() {
            @Override
            public boolean filter(Map map) throws Exception {
                Object compVal = GenericUtil.getEmbeddedValue(map, value);
                try {
                    compVal = Double.valueOf(compVal.toString());
                } catch (Exception e) {}
                //System.out.println(dynamicFunction.compare(compVal, target));
                return dynamicFunction.compare(compVal, target);
            }
        });

        this.stream = stream;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }
}
