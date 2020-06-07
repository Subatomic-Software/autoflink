package org.slotterback.sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slotterback.serdes.serialization.kafka.KafkaGenericSerializationSchema;
import org.slotterback.StreamBuilderUtil;

import java.util.Map;
import java.util.Properties;

public class KafkaSink extends GenericSink{
    public KafkaSink(Map schemas, SingleOutputStreamOperator stream, Map config) {

        Map format = StreamBuilderUtil.Base.Sink.KafkaSink.getFormat(config);
        String type = StreamBuilderUtil.Base.Sink.KafkaSink.Format.getType(format);
        String schema = StreamBuilderUtil.Base.Sink.KafkaSink.Format.getSchema(format);

        String broker = StreamBuilderUtil.Base.Sink.KafkaSink.getBroker(config);
        String topic = StreamBuilderUtil.Base.Sink.KafkaSink.getTopic(config);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", broker);

        FlinkKafkaProducer producer = new FlinkKafkaProducer(
                topic,
                KafkaGenericSerializationSchema.getSerializationSchema(type, schemas.get(schema).toString(), topic),
                properties,
                FlinkKafkaProducer.Semantic.NONE
        );

        stream.addSink(producer);
    }
}
