package org.slotterback.sink;

import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.slotterback.StreamBuilderUtil;
import org.slotterback.serdes.serialization.GenericSerializationSchema;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FileSink extends GenericSink{


    public FileSink(Map schemas, SingleOutputStreamOperator stream, String name, Map config) {

        Map format = StreamBuilderUtil.Base.Sink.FileSink.getFormat(config);
        String type = StreamBuilderUtil.Base.Sink.FileSink.Format.getType(format);
        String schema = StreamBuilderUtil.Base.Sink.FileSink.Format.getSchema(format);

        String directory = StreamBuilderUtil.Base.Sink.FileSink.getDirectory(config);

        GenericSerializationSchema serializationSchema = GenericSerializationSchema.getSerializationSchema(type, schemas.get(schema).toString());
        final Encoder encoder = new FileSinkStringEncoder(serializationSchema);

        final StreamingFileSink<String> sink = StreamingFileSink
                .forRowFormat(new Path(directory), encoder)
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Time.minutes(15).toMilliseconds())
                                .withInactivityInterval(Time.minutes(5).toMilliseconds())
                                .withMaxPartSize(1024 * 1024 * 1024)
                                .build())
                .build();


        stream.addSink(sink);
    }

    private class FileSinkStringEncoder<Map> implements Encoder<Map>{

        private GenericSerializationSchema serializationSchema;

        public FileSinkStringEncoder(GenericSerializationSchema serializationSchema) {
            this.serializationSchema = serializationSchema;
        }

        @Override
        public void encode(Map element, OutputStream stream) throws IOException {
            byte[] bytes = serializationSchema.serialize((java.util.Map<String, Object>) element);
            stream.write(bytes);
            stream.write('\n');
        }
    }
}
