package org.slotterback.serdes.deserialization;

import com.esotericsoftware.kryo.util.GenericsUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.slotterback.GenericUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AvroDeserializationSchema extends GenericDeserializationSchema {

    private Schema schema;

    public AvroDeserializationSchema(String schema) {
        if(schema.contains(".avsc")) {
            try {
                this.schema = new Schema.Parser().parse(new File(schema));
            } catch (IOException e) { }
        }else{
            this.schema = new Schema.Parser().parse(schema);
        }
    }

    @Override
    public Map<String, Object> deserialize(byte[] bytes) throws IOException {
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        GenericRecord record = reader.read(null, DecoderFactory.get().binaryDecoder(bytes, null));
        return GenericUtil.mapFromGeneric(record);
    }

    @Override
    public void setSchema(String schemaFile) {
        try {
            this.schema = new Schema.Parser().parse(new File(schemaFile));
        } catch (IOException e) { }
    }
}
