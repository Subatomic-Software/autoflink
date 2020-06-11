package org.slotterback;

import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.slotterback.operator.GenericOperator;
import org.slotterback.sink.GenericSink;
import org.slotterback.source.GenericSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class StreamBuilder {

    private StreamExecutionEnvironment env;
    private Map schemas;

    public void buildStream(Map streamBuilder, StreamExecutionEnvironment env, Map schemas, String killDirectory){
        this.env = env;
        this.schemas = schemas;
        Set<String> keys = streamBuilder.keySet();

        for(String entry : keys) {
            buildStream(entry, (Map) streamBuilder.get(entry), null);
        }

        if(killDirectory != null) {
            attachStreamStopper(env, killDirectory);
        }

    }

    private void buildStream(String entry, Map streamBuilder, SingleOutputStreamOperator stream){

        String function = StreamBuilderUtil.Base.getFunction(streamBuilder);
        String type = StreamBuilderUtil.Base.getType(streamBuilder);
        Map config = (Map) streamBuilder.remove(type);

        if(function.equals("source")){
            stream = GenericSource.sourceBuilder(env, schemas, stream, entry, type, config).getSourceStream();
        }else if(function.equals("operation")){
            stream = GenericOperator.functionBuilder(env, schemas, stream, entry, type, config).getSourceStream();
        }else if(function.equals("key")){
            //todo
        }else if(function.equals("sink")){
            GenericSink.sinkBuilder(env, schemas, stream, entry, type, config);
        }else{ }

        Set<String> keys = streamBuilder.keySet();
        for(String key : keys){
            buildStream(key, (Map) streamBuilder.get(key), stream);
        }
    }

    private static void attachStreamStopper(StreamExecutionEnvironment env, String listenDirectory){
        FileInputFormat<String> fileInputFormat = new DelimitedInputFormat<String>() {
            @Override
            public String readRecord(String reuse, byte[] bytes, int offset, int numBytes) throws IOException {
                throw new IOException("killing job from trigger source at " + LocalDateTime.now());
            }
        };
        env.readFile(fileInputFormat, listenDirectory, FileProcessingMode.PROCESS_CONTINUOUSLY, 1000).setParallelism(1);
    }
}
