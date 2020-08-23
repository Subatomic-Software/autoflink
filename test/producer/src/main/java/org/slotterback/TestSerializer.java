package org.slotterback;

import org.apache.kafka.common.serialization.Serializer;


public class TestSerializer extends Adapter implements Serializer<byte[]> {

    public byte[] serialize(final String topic, final byte[] data) {
        return data;
    }
}
