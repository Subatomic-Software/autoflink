package org.slotterback.Sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slotterback.SerDes.KafkaGenericSerializationSchema;

import java.util.Map;
import java.util.Properties;

public class KafkaSink extends GenericSink{
    public KafkaSink(Map schemas, SingleOutputStreamOperator stream, Map config) {
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
        String topic = config.get("topic").toString();

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
