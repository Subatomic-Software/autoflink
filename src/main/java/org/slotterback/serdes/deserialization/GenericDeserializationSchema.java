package org.slotterback.serdes.deserialization;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericDeserializationSchema implements DeserializationSchema<Map<String, Object>> {

    private interface DynamicDeserializationSchema {
        GenericDeserializationSchema getDeserializationSchema(String schemaFile);
    }

    private static DynamicDeserializationSchema none = (String) -> null;
    private static  DynamicDeserializationSchema avro = (String schemaFile) -> new AvroDeserializationSchema(schemaFile);
    private static  DynamicDeserializationSchema json = (String) -> new JsonDeserializationSchema();
    private static DynamicDeserializationSchema csv = (String schema) -> new CsvDeserializationSchema(schema);

    static Map<String, DynamicDeserializationSchema> typeMap = new HashMap(){
        {
            put(null, none);
            put("avro", avro);
            put("json", json);
            put("csv", csv);
        }
    };

    public static GenericDeserializationSchema getDeserializationSchema(String format, String schema){
        return typeMap.get(format).getDeserializationSchema(schema);
    }

    @Override
    public boolean isEndOfStream(Map<String, Object> nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Map<String, Object>> getProducedType() {
        return null;
    }

    public void setSchema(String schema) { }

}
