package com.subatomicsoftware.autoflink.sink;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

public class PrintSink extends GenericSink{

    public PrintSink(SingleOutputStreamOperator stream, String name) {
        stream.print(name);
    }
}
