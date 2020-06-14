package org.slotterback;

import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
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

    private Map nodes;
    private Map streams;

    public void buildStream(Map streamBuilder, StreamExecutionEnvironment env, Map schemas, String killDirectory){

        this.env = env;
        this.schemas = schemas;

        this.nodes = new HashMap();
        this.streams = new HashMap();


        Set<String> keys = streamBuilder.keySet();
        for(String entry : keys) {
            Map node = (Map) streamBuilder.get(entry);
            nodes.put(entry, node);

            String function = StreamBuilderUtil.Base.getFunction(node);
            String type = StreamBuilderUtil.Base.getType(node);
            Map config = (Map) node.get(type);

            if(function.equals("source")){
                streams.put(entry, GenericSource.sourceBuilder(env, schemas, null, entry, type, config).getSourceStream());
            }
        }

        Set<String> nodeKeys = streams.keySet();
        for(String nodeKey : nodeKeys){
            Map node = (Map) nodes.get(nodeKey);
            List outputs = StreamBuilderUtil.Base.getOutputs(node);
            for(Object output : outputs){
                buildStream2(nodeKey, (SingleOutputStreamOperator) streams.get(nodeKey), (Map) nodes.get(output));
            }
            int i = 0;
        }

        if(killDirectory != null) {
            attachStreamStopper(env, killDirectory);
        }

    }

    private void buildStream2(String name, SingleOutputStreamOperator stream, Map nextNode){
        String function = StreamBuilderUtil.Base.getFunction(nextNode);
        String type = StreamBuilderUtil.Base.getType(nextNode);
        Map config = (Map) nextNode.remove(type);

        if(function.equals("source")){
            //shouldnt happen now
            stream = GenericSource.sourceBuilder(env, schemas, stream, name, type, config).getSourceStream();
        }else if(function.equals("operation")){
            stream = GenericOperator.functionBuilder(env, schemas, stream, name, type, config).getSourceStream();
        }else if(function.equals("join")){
            //todo:
            //wait until everything completes,
            //then take the 2 streams to join,
            //join into single stream,
            //redo build on substream
        }else if(function.equals("sink")){
            GenericSink.sinkBuilder(env, schemas, stream, name, type, config);
        }else{ }

        List outputs = StreamBuilderUtil.Base.getOutputs(nextNode);
        for(Object output : outputs){
            buildStream2(output.toString(), stream, (Map) nodes.get(output));
        }
        int i = 0;
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
