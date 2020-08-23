package com.subatomicsoftware.autoflink.serdes.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonSerializationSchema extends GenericSerializationSchema{

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Map<String, Object> element) {
        String json = "";
        try {
            json = mapper.writeValueAsString(element);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json.getBytes();
    }
}
