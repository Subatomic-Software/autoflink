package com.subatomicsoftware.autoflink.serdes.serialization.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import com.subatomicsoftware.autoflink.serdes.serialization.CsvSerializationSchema;

import javax.annotation.Nullable;
import java.util.Map;

public class KafkaCsvSerializationSchema extends KafkaGenericSerializationSchema{

    private CsvSerializationSchema serializationSchema;
    private String topic;

    public KafkaCsvSerializationSchema(String topic) {
        serializationSchema = new CsvSerializationSchema();
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> element, @Nullable Long timestamp) {
        byte[] bytes = serializationSchema.serialize(element);

        //todo key?
        return new ProducerRecord(topic,null, bytes);
    }
}
