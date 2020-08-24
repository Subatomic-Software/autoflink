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

            String id = "SV" + (int)Math.floor(Math.random()*77+1);
            Double lati = (Math.random()*2-1)*90;
            Double longi = (Math.random()*2-1)*180;
            Integer battery = (int)Math.floor((Math.random()*60)+40);
            Integer computer = (int)(Math.random()*2)+1;
            Double temp = (Math.random()*300)-150;
            Integer error = (Math.random() < 0.1) ? 1 : 0;

            SateliteTelemetry test = new SateliteTelemetry();
            Telemetry telemetry = new Telemetry();

            test.setId(id);
            test.setLongitude(longi);
            test.setLatitude(lati);
            test.setTime(System.currentTimeMillis());
            telemetry.setBattery(battery);
            telemetry.setComputer(computer);
            telemetry.setTemperature(temp);
            telemetry.setError(error);
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
