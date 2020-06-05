package org.slotterback.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.Map;

public class KafkaJsonSerializationSchema extends KafkaGenericSerializationSchema{

    ObjectMapper mapper;
    String topic;

    public KafkaJsonSerializationSchema(String topic){
        this.mapper = new ObjectMapper();
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> map, @Nullable Long aLong) {

        byte[] out = new byte[0];
        try {
            out = mapper.writeValueAsBytes(map);
        } catch (JsonProcessingException e) { }

        //todo key?
        return new ProducerRecord(topic,null, out);
    }
}
