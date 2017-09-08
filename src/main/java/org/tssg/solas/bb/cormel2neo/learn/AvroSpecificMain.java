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

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.avro.User;

public class AvroSpecificMain {
  public static void main(String[] args) {

    Logger log = LoggerFactory.getLogger(AvroSpecificMain.class);
    User user1 = new User();
    user1.setName("Alyssa");
    user1.setFavoriteNumber(256);
    // Leave favorite color null

    // Alternate constructor
    User user2 = new User("Ben", 7, "red");

    // Construct via builder
    User user3 = User.newBuilder().setName("Charlie").setFavoriteColor("blue").setFavoriteNumber(null).build();

    // Serialize user1 and user2 to disk
    File avroFile = new File("target/users.avro");
    DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
    DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
    try {
      dataFileWriter.create(user1.getSchema(), avroFile);
      dataFileWriter.append(user1);
      dataFileWriter.append(user2);
      dataFileWriter.append(user3);
    } catch (IOException e1) {
      log.error("IOException creating or appending to {}: {}", avroFile, e1.getMessage());
      e1.printStackTrace();
    } finally {
      try {
        dataFileWriter.close();
      } catch (IOException e) {
        log.error("IOException closing dataFileWriter on {}: {}", avroFile, e.getMessage());
        e.printStackTrace();
      }
    }

    // Deserialize Users from disk
    DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
    DataFileReader<User> dataFileReader = null;
    try {
      User user = null;
      dataFileReader = new DataFileReader<User>(avroFile, userDatumReader);
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
