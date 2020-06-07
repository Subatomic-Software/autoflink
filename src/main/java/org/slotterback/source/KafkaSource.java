package org.slotterback.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slotterback.serdes.deserialization.GenericDeserializationSchema;
import org.slotterback.StreamBuilderUtil;

import java.util.Map;
import java.util.Properties;

public class KafkaSource extends GenericSource{

    private DataStreamSource source;

    public KafkaSource(StreamExecutionEnvironment env, Map schemas, Map config) {

        Map format = StreamBuilderUtil.Base.Source.KafkaSource.getFormat(config);
        String type = StreamBuilderUtil.Base.Source.KafkaSource.Format.getType(format);
        String schema = StreamBuilderUtil.Base.Source.KafkaSource.Format.getSchema(format);

        String broker = StreamBuilderUtil.Base.Source.KafkaSource.getBroker(config);
        String groupId = StreamBuilderUtil.Base.Source.KafkaSource.getGroupId(config);
        String topic = StreamBuilderUtil.Base.Source.KafkaSource.getTopic(config);

        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", broker);
        properties.setProperty("group.id", groupId);
        source = env.addSource(new FlinkKafkaConsumer(
                topic,
                GenericDeserializationSchema.getDeserializationSchema(type, schemas.get(schema).toString()),
                properties));
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return this.source.returns(Map.class);
    }
}
