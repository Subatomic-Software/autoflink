package com.subatomicsoftware.autoflink.serdes.serialization.kafka;

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import com.subatomicsoftware.autoflink.serdes.serialization.GenericSerializationSchema;

import java.util.HashMap;
import java.util.Map;

public abstract class KafkaGenericSerializationSchema extends GenericSerializationSchema implements KafkaSerializationSchema<Map<String, Object>>{

    private interface DynamicSerializationSchema {
        KafkaSerializationSchema getSerializationSchema(String schemaFile, String topic);
    }

    private static DynamicSerializationSchema none = (String schemaFile, String topic) -> null;
    private static DynamicSerializationSchema avro = (String schemaFile, String topic) -> new KafkaAvroSerializationSchema(schemaFile, topic);
    private static DynamicSerializationSchema json = (String schemaFile, String topic) -> new KafkaJsonSerializationSchema(topic);
    private static DynamicSerializationSchema csv = (String schemaFile, String topic) -> new KafkaCsvSerializationSchema(topic);

    static Map<String, DynamicSerializationSchema> typeMap = new HashMap(){
        {
            put(null, none);
            put("avro", avro);
            put("json", json);
            put("csv", csv);
        }
    };

    public static KafkaSerializationSchema getSerializationSchema(String format, String schemaFile, String topic) {
        return typeMap.get(format).getSerializationSchema(schemaFile, topic);
    }

    @Override
    public byte[] serialize(Map<String, Object> element) {
        return null;
    }

}
