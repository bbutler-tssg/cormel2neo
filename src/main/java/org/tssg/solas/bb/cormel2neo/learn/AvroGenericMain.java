/*
Copyright (c) 2017, Bernard Butler (Waterford Institute of Technology, Ireland), Project: SOLAS placement in Amadeus SA, where SOLAS (Project ID: 612480) is funded by the European Commision FP7 MC-IAPP-Industry-Academia Partnerships and Pathways scheme.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 -  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 -  Neither the name of WATERFORD INSTITUTE OF TECHNOLOGY nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.tssg.solas.bb.cormel2neo.learn;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvroGenericMain {
	public static void main(String[] args) {

		Logger log = LoggerFactory.getLogger(AvroGenericMain.class);
		DataFileWriter<GenericRecord> dataFileWriter;
		GenericRecord user1 = null;
		GenericRecord user2 = null;
		File schemaFile = new File("user.avsc");
		File avroFile = new File("target/users.avro");
		Schema schema = null;
		try {
			schema = new Parser().parse(schemaFile);
			user1 = new GenericData.Record(schema);
			user1.put("name", "Alyssa");
			user1.put("favorite_number", 256);
			// Leave favorite color null

			user2 = new GenericData.Record(schema);
			user2.put("name", "Ben");
			user2.put("favorite_number", 7);
			user2.put("favorite_color", "red");
		} catch (IOException e1) {
			log.error("IOException parsing {}: {}", schemaFile, e1.getMessage());
			e1.printStackTrace();
		}

		// Serialize user1 and user2 to disk
		DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
		dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
		try {
			dataFileWriter.create(schema, avroFile);
			dataFileWriter.append(user1);
			dataFileWriter.append(user2);
		} catch (IOException e1) {
			log.error("IOException creating or appending to {} using schema {}: {}", avroFile, schemaFile,
					e1.getMessage());
			e1.printStackTrace();
		} finally {
			try {
				dataFileWriter.close();
			} catch (IOException e) {
				log.error("IOException closing dataFileWriter on {}: {}", avroFile, e.getMessage());
				e.printStackTrace();
			}
		}

		// Deserialize users from disk
		DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
		DataFileReader<GenericRecord> dataFileReader = null;
		try {
			dataFileReader = new DataFileReader<GenericRecord>(avroFile, datumReader);
			GenericRecord user = null;
			while (dataFileReader.hasNext()) {
				// Reuse user object by passing it to next(). This saves us from
				// allocating and garbage collecting many objects for files with
				// many items.
				user = dataFileReader.next(user);
				log.info(user.toString());
			}
		} catch (IOException e) {
			log.error("IOException with dataFileReader: {}", e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				dataFileReader.close();
			} catch (IOException e) {
				log.error("IOException closing dataFileReader on {}: {}", avroFile, e.getMessage());
				e.printStackTrace();
			}
		}

	}
}
