package org.slotterback.SerDes;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericDeserializationSchema implements DeserializationSchema<Map<String, Object>> {

    private String schemaFile;

    public GenericDeserializationSchema(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    @Override
    public Map<String, Object> deserialize(byte[] bytes) throws IOException {

        Schema schema = new Schema.Parser().parse(new File(schemaFile));
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        GenericRecord record = reader.read(null, DecoderFactory.get().binaryDecoder(bytes, null));

        Map<String, Object> map = new HashMap<>();
        record.getSchema().getFields().forEach(field ->
                map.put(field.name(), record.get(field.name())));

        return map;
    }

    @Override
    public boolean isEndOfStream(Map<String, Object> map) {
        return false;
    }

    @Override
    public TypeInformation<Map<String, Object>> getProducedType() {
        return null;
    }
}
