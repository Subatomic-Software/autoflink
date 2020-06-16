package org.slotterback;

import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.slotterback.join.GenericJoin;
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

    private Map<String, List> outputRefs;
    private Map<String, SingleOutputStreamOperator> outputRefStreams;

    public void buildStream(Map streamBuilder, StreamExecutionEnvironment env, Map schemas, String killDirectory){

        this.env = env;
        this.schemas = schemas;

        this.nodes = new HashMap();
        this.streams = new HashMap();
        this.outputRefs = new HashMap();
        this.outputRefStreams = new HashMap();

        Set<String> keys = streamBuilder.keySet();
        for(String entry : keys) {
            Map node = (Map) streamBuilder.get(entry);
            nodes.put(entry, node);

            String function = StreamBuilderUtil.Base.getFunction(node);
            String type = StreamBuilderUtil.Base.getType(node);
            Map config = (Map) node.get(type);
            List<String> outputs = StreamBuilderUtil.Base.getOutputs(node);
            for(String output : outputs){
                List list = (List) outputRefs.get(output);
                if(list != null){
                    list.add(entry);
                }else{
                    outputRefs.put(output, new ArrayList(){{ add(entry); }});
                }
            }

            if(function.equals("source")){
                streams.put(entry, GenericSource.sourceBuilder(env, schemas, null, entry, type, config).getSourceStream());
            }
        }

        Set<String> nodeKeys = streams.keySet();
        for(String nodeKey : nodeKeys){
            Map node = (Map) nodes.get(nodeKey);
            List outputs = StreamBuilderUtil.Base.getOutputs(node);
            for(Object output : outputs){
                buildStream(output.toString(), (SingleOutputStreamOperator) streams.get(nodeKey), (Map) nodes.get(output));
            }
        }

        if(killDirectory != null) {
            attachStreamStopper(env, killDirectory);
        }

    }

    private void buildStream(String name, SingleOutputStreamOperator stream, Map nextNode){
        String function = StreamBuilderUtil.Base.getFunction(nextNode);
        String type = StreamBuilderUtil.Base.getType(nextNode);
        Map config = (Map) nextNode.remove(type);

        if(outputRefs.get(name).size() > 1) {
            if(outputRefStreams.containsKey(name)){
                stream = GenericJoin.functionBuilder(env, schemas, stream, outputRefStreams.get(name), name, type, config).getSourceStream();
            }else{
                outputRefStreams.put(name, stream);
                return;
            }
        }else if(function.equals("source")){
            //shouldnt happen now
            //stream = GenericSource.sourceBuilder(env, schemas, stream, name, type, config).getSourceStream();
        }else if(function.equals("operation")){
            stream = GenericOperator.functionBuilder(env, schemas, stream, name, type, config).getSourceStream();
        }else if(function.equals("sink")){
            GenericSink.sinkBuilder(env, schemas, stream, name, type, config);
        }else{ }

        List outputs = StreamBuilderUtil.Base.getOutputs(nextNode);
        for(Object output : outputs){
            buildStream(output.toString(), stream, (Map) nodes.get(output));
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
