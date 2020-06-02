package org.slotterback.SerDes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonDeserializationSchema extends GenericDeserializationSchema {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> deserialize(byte[] message) throws IOException {
        return mapper.readValue(new String(message), HashMap.class);
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
