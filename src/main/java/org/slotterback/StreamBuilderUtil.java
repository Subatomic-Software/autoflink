package org.slotterback;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamBuilderUtil {

    private static ArrayList formats = new ArrayList(){{
       add("json");
       add("avro");
       add("csv");
    }};

    public static class Base {
        public static String function = "function";
        public static String type = "type";

        public static String getFunction(Map map){
            return map.remove(function).toString();
        }
        public static String getType(Map map){
            return map.remove(type).toString();
        }

        public static class Source{
            public static String name = "source";

            public static class KafkaSource{
                public static String name = "kafka";

                public static String broker = "broker";
                public static String topic = "topic";
                public static String groupId = "groupId";
                public static String format = "format";

                public static List req = new ArrayList(){{
                    add(broker);
                    add(topic);
                    add(groupId);
                    add(format);
                }};

                public static String getBroker(Map map){
                    return map.remove(broker).toString();
                }
                public static String getTopic(Map map){
                    return map.remove(topic).toString();
                }
                public static String getGroupId(Map map){
                    return map.remove(groupId).toString();
                }

                public static class Format{
                    public static String name = "format";

                    public static String type = "type";
                    public static String schema = "schema";

                    public static Map allowed = new HashMap(){{ put("type", formats); }};
                    public static List req = new ArrayList(){{ add("type"); }};

                    public static String getType(Map map) {
                        return map.remove(type).toString();
                    }
                    public static String getSchema(Map map){
                        try{
                            return map.remove(schema).toString();
                        } catch (Exception e){
                            return null;
                        }
                    }
                }
                public static Map getFormat(Map map){
                    return (Map) map.remove(format);
                }
            }
            public static class FileSource{
                public static String name = "file";

                public static String directory = "directory";
                public static String format = "format";

                public static List req = new ArrayList(){{
                    add(directory);
                    add(format);
                }};

                public static String getDirectory(Map map){
                    return map.remove(directory).toString();
                }
                public static Map getFormat(Map map){
                    return (Map) map.remove(format);
                }

                public static class Format{
                    public static String name = "format";

                    public static String type = "type";
                    public static String schema = "schema";

                    public static Map allowed = new HashMap(){{ put("type", formats); }};
                    public static List req = new ArrayList(){{ add("type"); }};

                    public static String getType(Map map) {
                        return map.remove(type).toString();
                    }
                    public static String getSchema(Map map){
                        try{
                            return map.remove(schema).toString();
                        } catch (Exception e){
                            return null;
                        }
                    }
                }
            }
        }

        public static class Operator{
            public static String name = "operation";

            public static class FilterOperator{
                public static String name = "filter";

                public static String target = "target";
                public static String function = "function";
                public static String value = "value";

                public static List req = new ArrayList(){{
                    add(target);
                    add(function);
                    add(value);
                }};

                public static Map allowed = new HashMap(){{
                   put(function, new String[]{"==", "!=", "<", ">"});
                }};

                public static String getFunction(Map map){
                    return map.remove(function).toString();
                }
                public static String getValue(Map map){
                    return map.remove(value).toString();
                }
                public static Object getTarget(Map map){
                    Object obj = map.remove(target);
                    try {
                        return Double.valueOf(obj.toString());
                    }catch (NumberFormatException e){
                        return obj.toString();
                    }
                }
            }
            public static class MapOperator{
                public static String name = "map";

                public static String operation = "operation";
                public static String target = "target";
                public static String eval = "eval";

                public static List req = new ArrayList(){{
                    add(operation);
                    add(target);
                }};

                public static Map allowed = new HashMap(){{
                    put(operation, new String[]{"calc", "remove", "replace"});
                }};

                public static String getOperation(Map map){
                    return map.remove(operation).toString();
                }
                public static String getTarget(Map map){
                    return map.remove(target).toString();
                }
                public static String getEval(Map map){
                    try{
                        return map.remove(eval).toString();
                    }catch (Exception e){
                        return null;
                    }
                }
            }
        }

        public static class Sink{
            public static String name = "sink";

            public static class KafkaSink{
                public static String name = "kafka";

                public static String broker = "broker";
                public static String topic = "topic";
                public static String format = "format";

                public static List req = new ArrayList(){{
                    add(broker);
                    add(topic);
                    add(format);
                }};

                public static String getBroker(Map map){
                    return map.remove(broker).toString();
                }
                public static String getTopic(Map map){
                    return map.remove(topic).toString();
                }
                public static Map getFormat(Map map){
                    return (Map) map.remove(format);
                }

                public static class Format{
                    public static String name = "format";

                    public static String type = "type";
                    public static String schema = "schema";

                    public static Map allowed = new HashMap(){{ put("type", formats); }};
                    public static List req = new ArrayList(){{ add("type"); }};

                    public static String getType(Map map) {
                        return map.remove(type).toString();
                    }
                    public static String getSchema(Map map){
                        try{
                            return map.remove(schema).toString();
                        } catch (Exception e){
                            return null;
                        }
                    }
                }
            }

            public static class PrintSink{
                public static String name = "print";
            }
        }
    }

    public static String getOperatorJson(){
        String json = "";
        try {
            StreamBuilderUtil streamBuilderUtil = new StreamBuilderUtil();
            Class base = streamBuilderUtil.getClass().getClasses()[0];
            Class[] baseClasses = base.getClasses();

            Map baseFunctionMap = new HashMap();
            for (Class clazz : baseClasses) {
                Field[] tmp = clazz.getFields();
                Class[] tmp2 = clazz.getClasses();
                baseFunctionMap.put(tmp[0], tmp2);
            }

            Map operators = new HashMap();
            for (Object clazzes : baseFunctionMap.keySet()) {
                Map combined = new HashMap();
                Map mapfields = null;
                for (Class clazz : (Class[]) baseFunctionMap.get(clazzes)) {
                    mapfields = new HashMap();
                    Class[] subclazzes = clazz.getClasses();
                    Field[] fields = clazz.getFields();
                    for (Field field : fields) {
                        mapfields.put(field.getName(), field.get("name"));
                    }

                    Map suboperators = new HashMap();
                    for (Class subclazz : subclazzes) {
                        Map mapsubfields = new HashMap();
                        Field[] subfields = subclazz.getFields();
                        for (Field subfield : subfields) {
                            mapsubfields.put(subfield.getName(), subfield.get("name"));
                        }
                        suboperators.put(subclazz.getField("name").get("name"), mapsubfields);
                    }
                    for (Object suboperator : suboperators.keySet()) {
                        Map suboperatormap = (Map) suboperators.get(suboperator);

                        mapfields.put(suboperator.toString(), suboperatormap);
                        int k = 0;
                    }
                    combined.put(clazz.getField("name").get("name"), mapfields);
                    int i = 0;
                }
                operators.put(((Field) clazzes).get("name"), combined);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(operators);
        }catch (Exception e){
            return "";
        }
        return json;
    }

}
