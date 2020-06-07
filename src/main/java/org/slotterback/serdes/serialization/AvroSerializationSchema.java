package org.slotterback.serdes.serialization;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AvroSerializationSchema extends GenericSerializationSchema{

    static private Schema schema;

    public AvroSerializationSchema(String schemaFile) {
        try {
            this.schema = new Schema.Parser().parse(new File(schemaFile));
        } catch (IOException e) { }
    }

    @Override
    public byte[] serialize(Map<String, Object> element) {
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);

        GenericRecordBuilder recordBuilder = new GenericRecordBuilder(schema);
        for (String key: element.keySet()) {
            recordBuilder.set(key, element.get(key));
        }
        GenericRecord record = recordBuilder.build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        try {
            writer.write(record, encoder);
            encoder.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }
}
