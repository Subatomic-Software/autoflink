package org.slotterback.serdes.serialization.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slotterback.serdes.serialization.JsonSerializationSchema;

import javax.annotation.Nullable;
import java.util.Map;

public class KafkaJsonSerializationSchema extends KafkaGenericSerializationSchema{

    private JsonSerializationSchema serializationSchema;
    private String topic;

    public KafkaJsonSerializationSchema(String topic){
        this.serializationSchema = new JsonSerializationSchema();
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> map, @Nullable Long aLong) {

        byte[] bytes = serializationSchema.serialize(map);

        //todo key?
        return new ProducerRecord(topic,null, bytes);
    }
}
