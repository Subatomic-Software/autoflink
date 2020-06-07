package org.slotterback.source;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.slotterback.serdes.deserialization.GenericDeserializationSchema;
import org.slotterback.StreamBuilderUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileSource extends GenericSource {

    private DataStreamSource source;

    public FileSource(StreamExecutionEnvironment env, Map schemas, Map config) {

        Map format = StreamBuilderUtil.Base.Source.FileSource.getFormat(config);
        String type = StreamBuilderUtil.Base.Source.FileSource.Format.getType(format);
        String schema = StreamBuilderUtil.Base.Source.FileSource.Format.getSchema(format);

        String directory = StreamBuilderUtil.Base.Source.FileSource.getDirectory(config);

        final GenericDeserializationSchema deserializer = GenericDeserializationSchema.getDeserializationSchema(type, schema);


        DelimitedInputFormat inputFormat = new DelimitedInputFormat() {

            Boolean schemaHeader = false;
            private transient int currOffset;

            @Override
            public void open(FileInputSplit split) throws IOException {
                super.open(split);
                if(schema == null && !format.equals("json")){
                    schemaHeader = true;
                    String schema = Files.lines(Paths.get(split.getPath().getPath()))
                            .findFirst()
                            .get();
                    deserializer.setSchema(schema);
                    currOffset = currOffset + 1;
                }
            }

            @Override
            public Object readRecord(Object reuse, byte[] bytes, int offset, int numBytes) throws IOException {
                if(offset == 0 && schema == null && !format.equals("json") ||
                        offset == 0 && schema != null && schema.equals(new String(bytes, offset, numBytes))){
                    return new HashMap<>();
                }
                byte[] report = Arrays.copyOfRange(bytes, offset, offset+numBytes);
                return  deserializer.deserialize(report);
            }

        };
        inputFormat.setDelimiter('\n');

        TypeInformation<Map> info = TypeInformation.of(Map.class);
        source = env.readFile(inputFormat, directory, FileProcessingMode.PROCESS_CONTINUOUSLY, 1000, info);
    }

    @Override
    public SingleOutputStreamOperator<Map> getSourceStream() {
        return this.source.returns(Map.class).filter(new FilterFunction<Map>() {
            @Override
            public boolean filter(Map value) throws Exception {
                return !value.isEmpty();
            }
        });
    }
}
