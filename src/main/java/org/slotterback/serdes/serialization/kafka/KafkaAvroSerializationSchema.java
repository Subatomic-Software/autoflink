package org.slotterback.serdes.serialization.kafka;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slotterback.serdes.serialization.AvroSerializationSchema;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
