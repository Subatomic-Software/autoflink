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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.FileReader;
import java.util.*;

public class StreamingJob {

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env;

		if(Arrays.stream(args).anyMatch(str -> str.equals("dev"))){
			env = StreamExecutionEnvironment.createLocalEnvironment(1);
		}else{
			env = StreamExecutionEnvironment.getExecutionEnvironment();
		}

		String jsonFile = args[0];
		String schema = args[1];
		String schema2 = args[2];
		String schema3 = args[3];
		Map schemas = new HashMap<String, String>();
		schemas.put("Tester", schema);
		schemas.put("Tester2", schema2);
		schemas.put("Tester3", schema3);
		schemas.put(null, "");

		ObjectMapper objectMapper = new ObjectMapper();
		Map streamBuilder = objectMapper.readValue(new FileReader(jsonFile), HashMap.class);

		StreamBuilder builder = new StreamBuilder();
		builder.buildStream(streamBuilder, env, schemas);


		env.execute("Flink Streaming Java API Skeleton");
	}




}

