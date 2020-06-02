package org.slotterback.SerDes;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AvroDeserializationSchema extends GenericDeserializationSchema {

    private Schema schema;

    public AvroDeserializationSchema(String schemaFile) {
        try {
            this.schema = new Schema.Parser().parse(new File(schemaFile));
        } catch (IOException e) { }
    }

    @Override
    public Map<String, Object> deserialize(byte[] bytes) throws IOException {

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        GenericRecord record = reader.read(null, DecoderFactory.get().binaryDecoder(bytes, null));

        Map<String, Object> map = new HashMap<>();
        record.getSchema().getFields().forEach(field ->
                map.put(field.name(), record.get(field.name())));

        return map;
    }

    @Override
    public boolean isEndOfStream(Map<String, Object> nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Map<String, Object>> getProducedType() {
        return null;
    }
}
