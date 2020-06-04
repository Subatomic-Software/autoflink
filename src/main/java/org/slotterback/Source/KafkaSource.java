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

    public KafkaSource(StreamExecutionEnvironment env, Map schemas, Map config) {
        Properties properties = new Properties();

        String schema = null;
        try {
            schema = config.get("schema").toString();
        } catch (Exception e){ }
        String format = null;
        try {
            format = config.get("format").toString();
        } catch (Exception e){ }

        String broker = config.get("broker").toString();
        String groupId = config.get("groupid").toString();
        String topic = config.get("topic").toString();

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
