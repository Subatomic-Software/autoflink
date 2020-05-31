package org.slotterback.Function;

import com.google.gson.JsonObject;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterFunction extends GenericFunction{

    private interface DynamicCompare extends Serializable {
        Boolean compare(Object obj1, Object obj2);
    }


    private SingleOutputStreamOperator stream;

    public FilterFunction(StreamExecutionEnvironment env, Set<String> keys, SingleOutputStreamOperator stream, JsonObject obj) {
        super();


        DynamicCompare eqCompare = (obj1, obj2) -> { return obj1 == obj2; };
        DynamicCompare neqCompare = (obj1, obj2) -> { return obj1 != obj2; };
        DynamicCompare ltCompare = (obj1, obj2) -> { return Double.valueOf(obj1.toString()) < (Double)obj2; };
        DynamicCompare gtCompare = (obj1, obj2) -> { return Double.valueOf(obj1.toString()) > (Double)obj2; };

        JsonObject conf = obj.getAsJsonObject("comp");

        final String field = conf.get("field").getAsString();
        String comp = conf.get("comp").getAsString();
        Object val = conf.get("val").getAsString();
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

        keys.remove("comp");

        stream = stream.filter(new RichFilterFunction() {
            @Override
            public boolean filter(Object o) throws Exception {
                HashMap obj = (HashMap) o;
                System.out.println(compare.compare(obj.get(field), finalVal));
                return compare.compare(obj.get(field), finalVal);
            }
        });

        this.stream = stream;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }
}
