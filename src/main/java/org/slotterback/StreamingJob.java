/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slotterback;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slotterback.Source.GenericSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StreamingJob {

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env;

		if(Arrays.stream(args).anyMatch(str -> str.toString().equals("dev"))){
			env = StreamExecutionEnvironment.createLocalEnvironment(1);
		}else{
			env = StreamExecutionEnvironment.getExecutionEnvironment();
		}

		String jsonFile = args[0];
		String schema = args[1];

		JsonObject jsonObject = JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();
		Set<String> keys = jsonObject.keySet();

		for(String tmp : keys){
			System.out.println(tmp);
			JsonObject str = jsonObject.getAsJsonObject(tmp);

			DataStream<GenericRecord> ds = null;
			if(tmp.equals("source"))
				GenericSource.sourceBuilder(str, env, schema).getSourceStream().print();
		}

		env.execute("Flink Streaming Java API Skeleton");
	}

	/*
	private static DataStream getSource(StreamExecutionEnvironment env, JsonObject str, String schema){

		Set<String> keys1 = str.keySet();

		if(str.get("type").toString().replaceAll("\"","").equals("kafka")){

			Properties properties = new Properties();
			String broker = str.get("broker").toString().replaceAll("\"","");
			String groupId = str.get("groupid").toString().replaceAll("\"","");
			String topic = str.get("topic").toString().replaceAll("\"","");

			properties.setProperty("bootstrap.servers", broker);
			properties.setProperty("group.id", groupId);
			DataStreamSource<Map> stream = env
					.addSource(new FlinkKafkaConsumer(topic,
							GenericDeserializationSchema(schema),
							properties));
			return stream.returns(Map.class);
		}

		return null;
	}
	*/

}

