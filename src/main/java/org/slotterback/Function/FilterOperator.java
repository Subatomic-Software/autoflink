package org.slotterback.Function;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.GenericUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class FilterOperator extends GenericOperator {

    private interface DynamicCompare extends Serializable {
        Boolean compare(Object obj1, Object obj2);
    }

    private SingleOutputStreamOperator stream;

    public FilterOperator(StreamExecutionEnvironment env, SingleOutputStreamOperator stream, Map config) {

        //TODO compare doubles to self values?
        DynamicCompare eqCompare = (obj1, obj2) -> { return obj1.equals(obj2); };
        DynamicCompare neqCompare = (obj1, obj2) -> { return !obj1.equals(obj2); };
        DynamicCompare ltCompare = (obj1, obj2) -> { return (Double) obj1 < (Double) obj2; };
        DynamicCompare gtCompare = (obj1, obj2) -> { return (Double) obj1 > (Double) obj2; };

        final String field = config.get("field").toString();
        String comp = config.get("comp").toString();
        Object val = config.get("val").toString();
        try {
            val = Double.valueOf((String) val);
        }catch (NumberFormatException e){}
        final Object finalVal = val;

        final DynamicCompare compare;
        if(comp.equals("eq")){
            compare = eqCompare;
        }else if(comp.equals("neq")){
            compare = neqCompare;
        }else if(comp.equals("lt")){
            compare = ltCompare;
        }else if(comp.equals("gt")){
            compare = gtCompare;
        }else{
            compare = null;
        }

        stream = stream.filter(new RichFilterFunction<Map>() {
            @Override
            public boolean filter(Map map) throws Exception {
                Object compVal = GenericUtil.getEmbeddedValue(map, field);
                try {
                    compVal = Double.valueOf(compVal.toString());
                } catch (Exception e) {}

                System.out.println(compare.compare(compVal, finalVal));
                return compare.compare(compVal, finalVal);
            }
        });

        this.stream = stream;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }
}
