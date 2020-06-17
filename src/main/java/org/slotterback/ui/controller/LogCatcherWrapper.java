package org.slotterback.ui.controller;

import org.apache.flink.core.execution.JobClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Service
public class LogCatcherWrapper {

    public LogCatcherWrapper(){

    }

    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }
}
