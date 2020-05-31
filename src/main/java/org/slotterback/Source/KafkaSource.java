package org.slotterback.Source;

import com.google.gson.JsonObject;
import org.apache.flink.streaming.api.datastream.DataStream;
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

    public KafkaSource(StreamExecutionEnvironment env, Set<String> keys, String schema, JsonObject obj) {
        super();
        Properties properties = new Properties();
        String broker = obj.get("broker").toString().replaceAll("\"","");
        String groupId = obj.get("groupid").toString().replaceAll("\"","");
        String topic = obj.get("topic").toString().replaceAll("\"","");

        keys.remove("broker");
        keys.remove("groupid");
        keys.remove("topic");

        properties.setProperty("bootstrap.servers", broker);
        properties.setProperty("group.id", groupId);
        source = env.addSource(new FlinkKafkaConsumer(topic, new GenericDeserializationSchema(schema), properties));
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return this.source.returns(Map.class);
    }
}
