package org.slotterback.Function;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

public class MapFunction extends GenericFunction{

    private SingleOutputStreamOperator stream;
    private List<String> operators;

    private interface Arithmetic extends Serializable {
        Object apply(Object o1, Object o2);
    }

    public MapFunction(StreamExecutionEnvironment env, Set<String> keys, SingleOutputStreamOperator stream, JsonObject obj) {

        Map<String, Arithmetic> operators = new HashMap<>();
        operators.put("+", (Object o1, Object o2) -> {
            if(StringUtils.isNumeric(o1.toString()) && StringUtils.isNumeric(o2.toString())){
                return Double.valueOf(o1.toString())+Double.valueOf(o2.toString());
            }else{
                return o1.toString()+o2.toString();
            }
        });
        operators.put("-", (Object o1, Object o2) -> Double.valueOf(o1.toString())-Double.valueOf(o2.toString()));
        operators.put("*", (Object o1, Object o2) -> Double.valueOf(o1.toString())*Double.valueOf(o2.toString()));
        operators.put("/", (Object o1, Object o2) -> Double.valueOf(o1.toString())/Double.valueOf(o2.toString()));


        JsonObject conf = obj.getAsJsonObject("func");
        final String operation = conf.get("op").getAsString();
        final String target = conf.get("target").getAsString();

        String[] evals = null;
        String operator = "";

        if(!operation.equals("remove")) {
            final String eval = conf.get("eval").getAsString();
            operator = operators.keySet().stream().filter(str -> eval.contains(str)).findFirst().get();
            evals = eval.split("\\"+operator);
        }

        final String[] finalEvals = evals;
        final String finalOperator = operator;



        stream = stream.map(new RichMapFunction<Map<Object, Object>, Map<Object, Object>>() {
            @Override
            public Map map(Map map) throws Exception {
                if(!operation.equals("remove")) {
                    //TODO chain operations
                    Object var1 = null;
                    Object var2 = null;
                    for (Object field : map.keySet()) {
                        if (field.toString().equals(finalEvals[0])) {
                            var1 = map.get(field);
                        }
                        if (field.toString().equals(finalEvals[1])) {
                            var2 = map.get(field);
                        }
                    }
                    map.put(target, operators.get(finalOperator).apply(var1, var2));
                }else{
                    map.remove(target);
                }
                return map;
            }
        });

        this.stream = stream;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }

}
