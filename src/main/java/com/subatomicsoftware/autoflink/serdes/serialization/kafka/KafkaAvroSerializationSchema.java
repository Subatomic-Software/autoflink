package com.subatomicsoftware.autoflink.serdes.serialization.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import com.subatomicsoftware.autoflink.serdes.serialization.AvroSerializationSchema;

import javax.annotation.Nullable;
import java.util.Map;

public class KafkaAvroSerializationSchema extends KafkaGenericSerializationSchema{

    private AvroSerializationSchema serializationSchema;
    private String topic;

    public KafkaAvroSerializationSchema(String schemaFile, String topic) {
        this.serializationSchema = new AvroSerializationSchema(schemaFile);
        this.topic = topic;
    }


    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> map, @Nullable Long aLong) {
        byte[] bytes = serializationSchema.serialize(map);

        //todo key?
        return new ProducerRecord(topic,null, bytes);
    }
}
