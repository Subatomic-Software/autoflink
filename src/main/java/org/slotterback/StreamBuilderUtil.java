package org.slotterback;

import java.util.Map;

public class StreamBuilderUtil {

    public static class Generic {
        public static String function = "function";
        public static String type = "type";

        public static String getFunction(Map map){
            return map.remove(function).toString();
        }
        public static String getType(Map map){
            return map.remove(type).toString();
        }

        public static class Source{

            public static class KafkaSource{
                public static String broker = "broker";
                public static String topic = "topic";
                public static String groupId = "groupId";
                public static String format = "format";

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
                    public static String type = "type";
                    public static String schema = "schema";

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
                public static String directory = "directory";
                public static String format = "format";

                public static String getDirectory(Map map){
                    return map.remove(directory).toString();
                }
                public static Map getFormat(Map map){
                    return (Map) map.remove(format);
                }

                public static class Format{
                    public static String type = "type";
                    public static String schema = "schema";

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

        public static class Operation{

            public static class FilterOperator{
                public static String target = "target";
                public static String function = "function";
                public static String value = "value";

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
                public static String operation = "operation";
                public static String target = "target";
                public static String eval = "eval";

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

            public static class KafkaSink{
                public static String broker = "broker";
                public static String topic = "topic";
                public static String format = "format";

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
                    public static String type = "type";
                    public static String schema = "schema";

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

            public static class PrintSink{}
        }
    }

}
