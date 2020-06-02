package org.slotterback.Function;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class FilterFunction extends GenericFunction{

    private interface DynamicCompare extends Serializable {
        Boolean compare(Object obj1, Object obj2);
    }

    private SingleOutputStreamOperator stream;

    public FilterFunction(StreamExecutionEnvironment env, Set<String> keys, SingleOutputStreamOperator stream, Map streamBuilder) {

        //TODO compare doubles to self values?
        DynamicCompare eqCompare = (obj1, obj2) -> { return obj1 == obj2; };
        DynamicCompare neqCompare = (obj1, obj2) -> { return obj1 != obj2; };
        DynamicCompare ltCompare = (obj1, obj2) -> { return Double.valueOf(obj1.toString()) < (Double)obj2; };
        DynamicCompare gtCompare = (obj1, obj2) -> { return Double.valueOf(obj1.toString()) > (Double)obj2; };

        Map conf = (Map) streamBuilder.get("func");
        final String field = conf.get("field").toString();
        String comp = conf.get("comp").toString();
        Object val = conf.get("val").toString();
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

        keys.remove("func");

        stream = stream.filter(new RichFilterFunction<Map>() {
            @Override
            public boolean filter(Map map) throws Exception {
                System.out.println(compare.compare(map.get(field), finalVal));
                return compare.compare(map.get(field), finalVal);
            }
        });

        this.stream = stream;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }
}
