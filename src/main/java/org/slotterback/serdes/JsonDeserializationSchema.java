package org.slotterback.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonDeserializationSchema extends GenericDeserializationSchema {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> deserialize(byte[] message) throws IOException {
        return mapper.readValue(new String(message), HashMap.class);
    }
}
