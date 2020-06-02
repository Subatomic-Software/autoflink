package org.slotterback.Source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slotterback.SerDes.GenericDeserializationSchema;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class KafkaSource extends GenericSource{

    private DataStreamSource source;

    public KafkaSource(StreamExecutionEnvironment env, Set<String> keys, Map streamBuilder, Map schemas) {
        Properties properties = new Properties();
        Map kafka = (Map) streamBuilder.get("kafka");

        String schema = null;
        try {
            schema = kafka.get("schema").toString();
        } catch (Exception e){ }
        String format = null;
        try {
            format = kafka.get("format").toString();
        } catch (Exception e){ }

        String broker = kafka.get("broker").toString();
        String groupId = kafka.get("groupid").toString();
        String topic = kafka.get("topic").toString();

        /*
        keys.remove("broker");
        keys.remove("groupid");
        keys.remove("topic");
        */

        properties.setProperty("bootstrap.servers", broker);
        properties.setProperty("group.id", groupId);
        source = env.addSource(new FlinkKafkaConsumer(
                topic,
                GenericDeserializationSchema.getDeserializationSchema(format, schemas.get(schema).toString()),
                properties));
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return this.source.returns(Map.class);
    }
}
