package com.subatomicsoftware.autoflink.serdes.serialization;

import org.apache.flink.api.common.serialization.SerializationSchema;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericSerializationSchema implements SerializationSchema<Map<String, Object>> {

    private interface DynamicSerializationSchema {
        GenericSerializationSchema getSerializationSchema(String schemaFile);
    }

    private static DynamicSerializationSchema none = (String) -> null;
    private static DynamicSerializationSchema avro = (String schemaFile) -> new AvroSerializationSchema(schemaFile);
    private static DynamicSerializationSchema json = (String) -> new JsonSerializationSchema();
    private static DynamicSerializationSchema csv = (String) -> new CsvSerializationSchema();

    static Map<String, DynamicSerializationSchema> typeMap = new HashMap(){
        {
            put(null, none);
            put("avro", avro);
            put("json", json);
            put("csv", csv);
        }
    };

    public static GenericSerializationSchema getSerializationSchema(String format, String schema){
        return typeMap.get(format).getSerializationSchema(schema);
    }

}
