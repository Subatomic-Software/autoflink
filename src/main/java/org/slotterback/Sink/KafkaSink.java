package org.slotterback.Sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slotterback.SerDes.KafkaGenericSerializationSchema;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class KafkaSink extends GenericSink{
    public KafkaSink(StreamExecutionEnvironment env, Set<String> keys, Map streamBuilder, Map schemas, SingleOutputStreamOperator stream) {
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
        String topic = kafka.get("topic").toString();

        properties.setProperty("bootstrap.servers", broker);

        FlinkKafkaProducer producer = new FlinkKafkaProducer(
                topic,
                KafkaGenericSerializationSchema.getSerializationSchema(format, schemas.get(schema).toString(), topic),
                properties,
                FlinkKafkaProducer.Semantic.NONE
        );

        stream.addSink(producer);
    }
}
