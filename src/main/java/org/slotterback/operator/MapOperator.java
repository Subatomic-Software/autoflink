package org.slotterback.operator;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slotterback.GenericUtil;
import org.slotterback.StreamBuilderUtil;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class MapOperator extends GenericOperator {

    private SingleOutputStreamOperator stream;

    private interface MapOperations extends Serializable {
        Map run(Map map, Expression expression, List<String> vars, String target);
    }

    public MapOperator(StreamExecutionEnvironment env, Map schemas, SingleOutputStreamOperator stream, String name, String type, Map config) {

        Map<String, MapOperations> mapOperations = new HashMap<>();
        mapOperations.put("calc", (Map map, Expression expression, List<String> vars, String target)
                -> mapCalc(map, expression, vars, target));
        mapOperations.put("remove", (Map map, Expression expression, List<String> vars, String target)
                -> remove(map, target));
        mapOperations.put("replace", (Map map, Expression expression, List<String> vars, String target)
                -> replace(map, vars, target));

        String operation = StreamBuilderUtil.Generic.Operation.MapOperator.getOperation(config);
        String target = StreamBuilderUtil.Generic.Operation.MapOperator.getTarget(config);
        String eval = StreamBuilderUtil.Generic.Operation.MapOperator.getEval(config);

        List<String> varList = new ArrayList<>();
        String evalFunction = "0";
        if(eval != null) {
            evalFunction = eval;
            String regex = "[\\!|\\%|\\^|\\&|\\*|\\(|\\)|\\+|\\-|\\/]";
            String[] vars = evalFunction.split(regex);

            varList = Arrays
                    .stream(vars)
                    .distinct()
                    .filter(str -> !str.equals(""))
                    .filter(str -> { try{ Double.valueOf(str); } catch (Exception e){ return true; } return false; })
                    .collect(Collectors.toList());
        }else if(operation.equals("replace")){
            varList.add(target);
        }

        final String finalEvalFunc = evalFunction;
        final List<String> finalVars = varList;

        stream = stream.map(new RichMapFunction<Map<Object, Object>, Map<Object, Object>>() {
            transient Expression expression;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                expression = new ExpressionBuilder(finalEvalFunc)
                        .variables(new HashSet(finalVars))
                        .build();
            }

            @Override
            public Map map(Map map) throws Exception {
                return mapOperations.get(operation).run(map, expression, finalVars, target);
            }
        });

        this.stream = stream;
    }

    private static Map mapCalc(Map map, Expression expression, List<String> vars, String target){
        Map<String, Double> variables = new HashMap<>();
        for(String var : vars){
            Double val = Double.valueOf(GenericUtil.getEmbeddedValue(map, var).toString());
            variables.put(var, val);
        }
        double result = expression.setVariables(variables).evaluate();
        GenericUtil.putEmbeddedValue(map, target, result);
        return map;
    }

    private static Map remove(Map map, String target){
        GenericUtil.removeEmbeddedValue(map, target);
        return map;
    }

    private static Map replace(Map map, List<String> vars, String target){
        GenericUtil.putEmbeddedValue(map, target, vars.get(0));
        return map;
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return stream;
    }

}
