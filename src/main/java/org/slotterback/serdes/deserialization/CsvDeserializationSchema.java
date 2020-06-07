package org.slotterback.serdes.deserialization;

import org.slotterback.GenericUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvDeserializationSchema extends GenericDeserializationSchema {

    private String[] schema;

    public CsvDeserializationSchema(String schema) {
        if(schema == null){
            this.schema = new String[0];
        }else{
            this.schema = schema.split(",");
        }
    }

    @Override
    public Map<String, Object> deserialize(byte[] message) throws IOException {

        Map map = new HashMap<>();
        Object[] vars = (new String(message).split(","));
        if(vars.length < schema.length){
            return map;
        }

        for (int i = 0; i < schema.length; i++) {
            try{
                GenericUtil.putEmbeddedValue(map, schema[i], Double.valueOf(vars[i].toString()));
            }catch (Exception e) {
                GenericUtil.putEmbeddedValue(map, schema[i], vars[i]);
            }
        }
        return map;
    }

    @Override
    public void setSchema(String schema) {
        this.schema = schema.split(",");
    }
}
