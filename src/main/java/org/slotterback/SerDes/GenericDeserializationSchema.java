package org.slotterback.SerDes;

import org.apache.flink.api.common.serialization.DeserializationSchema;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericDeserializationSchema implements DeserializationSchema<Map<String, Object>> {

    private interface DynamicDeserializationSchema {
        GenericDeserializationSchema getDeserializationSchema(String schemaFile);
    }

    private static DynamicDeserializationSchema none = (String schemaFile) -> null;
    private static  DynamicDeserializationSchema avro = (String schemaFile) -> new AvroDeserializationSchema(schemaFile);
    private static  DynamicDeserializationSchema json = (String schemaFile) -> new JsonDeserializationSchema();

    static Map<String, DynamicDeserializationSchema> typeMap = new HashMap(){
        {
            put(null, none);
            put("avro", avro);
            put("json", json);
        }
    };

    public static GenericDeserializationSchema getDeserializationSchema(String format, String schemaFile){
        return typeMap.get(format).getDeserializationSchema(schemaFile);
    }

}
