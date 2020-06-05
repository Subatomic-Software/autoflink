package org.slotterback.serdes;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class KafkaAvroSerializationSchema extends KafkaGenericSerializationSchema{

    private static Schema schema;
    private String topic;

    public KafkaAvroSerializationSchema(String schemaFile, String topic) {
        this.topic = topic;
        try {
            this.schema = new Schema.Parser().parse(new File(schemaFile));
        } catch (IOException e) { }

    }


    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> map, @Nullable Long aLong) {
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        for (String key : map.keySet()){
            builder.set(key, map.get(key));
        }
        GenericRecord record = builder.build();

        byte[] out = new byte[0];
        try {
            out = dataToByteArray(schema, record);
        } catch (IOException e) { }

        //todo key?
        return new ProducerRecord(topic,null, out);
    }

    public static byte[] dataToByteArray(Schema schema, GenericRecord datum) throws IOException {
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Encoder e = EncoderFactory.get().binaryEncoder(os, null);
            writer.write(datum, e);
            e.flush();
            byte[] byteData = os.toByteArray();
            return byteData;
        } finally {
            os.close();
        }
    }


}
