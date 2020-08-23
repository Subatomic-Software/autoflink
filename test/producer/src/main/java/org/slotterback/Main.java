package org.slotterback;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {


    public static void main(String[] args) throws InterruptedException, IOException {


        Properties properties = new Properties();
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put("bootstrap.servers", "localhost:9092");

        KafkaProducer producer = new KafkaProducer(properties, new StringSerializer(), new TestSerializer());
        producer.flush();

        Schema schema = SpecificData.get().getSchema(SateliteTelemetry.class);
        DatumWriter<GenericRecord> writer = new SpecificDatumWriter(schema);


        String topic = args[0];
        Long pause = Long.valueOf(args[1]);

        while(true) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            String key = "Key" + (int)Math.floor(Math.random()*5);
            Integer val = (int)Math.floor(Math.random()*10);

            SateliteTelemetry test = new SateliteTelemetry();
            Telemetry telemetry = new Telemetry();
            test.setId("testid");
            test.setLongitude(0.1111);
            test.setLatitude(-0.1111);
            test.setTime(System.currentTimeMillis());
            telemetry.setBattery(100);
            telemetry.setComputer(1);
            telemetry.setTemperature(-200);
            telemetry.setError(0);
            test.setTelemetry(telemetry);

            try {
                writer.write(test, encoder);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    encoder.flush();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byte[] serializedBytes = out.toByteArray();
            System.out.println("sending: " + test.toString());
            producer.send(new ProducerRecord(topic, serializedBytes));
            producer.flush();
            Thread.sleep(pause);
        }
    }


}
