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

import static org.neo4j.driver.v1.Values.parameters;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleCRUD {

  Logger log = LoggerFactory.getLogger(ExampleCRUD.class);
  private static final String defaultConnectionString = "bolt://localhost:7687";
  private Session session;
  
  public ExampleCRUD(Session session) {
    this.session = session;
  }
  
  public void applyConstraint() {
    session.run("CREATE CONSTRAINT ON (n:Person) ASSERT n.key IS UNIQUE");
  }

  private String deriveKey(Map<String, Object> params) {
    StringBuffer sb = new StringBuffer();
    sb.append(String.valueOf(params.get("title")));
    sb.append("_");
    sb.append(String.valueOf(params.get("name")));
    return sb.toString();
  }
  
  public int addNode(Map<String, Object> params) {
    StatementResult result = session.run("MERGE (a:Person {name: {name}, title: {title}, key: {key}})",
        parameters("name", params.get("name"), "title", params.get("title"), "key", deriveKey(params)));
    int nrows = result.consume().counters().nodesCreated();
    log.info("{} nodes were created",nrows);
    return nrows;
  }

  public int lookupNode(Map<String, Object> params) {
    int nrows = 0;
    StatementResult result = session.run(
        "MATCH (a:Person) WHERE a.key = {key} RETURN a.name AS name, a.title AS title",
        parameters("key", deriveKey(params)));
    if (result.hasNext()) {
      while (result.hasNext()) {
        Record record = result.next();
        System.out.println(record.get("title").asString() + " " + record.get("name").asString());
        nrows++;
      }
    } else {
      System.out.println("Node with params " + params.toString() + " not found!");
    }
    return nrows;
  }

  public int deleteNode(Map<String, Object> params) {
    StatementResult result = session.run("MATCH (a:Person {key: {key}}) DELETE a",
        parameters("key", deriveKey(params)));
    int nrows = result.consume().counters().nodesDeleted();
    log.info("{} nodes were deleted",nrows);
    return nrows;
  }

  public static void main(String[] args) {
    Map<String,Object> params = new HashMap<>();
    params.put("title", "King");
    params.put("name", "Arthur");

    try (Driver driver = GraphDatabase.driver(defaultConnectionString)) {
    // try (Driver driver = GraphDatabase.driver(connectionString,
    // AuthTokens.basic("neo4j", "neo4j"))) {
      try (Session session = driver.session()) {
        ExampleCRUD ex = new ExampleCRUD(session);
        System.out.println("Apply node key constraint");
        ex.applyConstraint();
        System.out.println("Delete node - should be nothing to delete");
        ex.deleteNode(params);
        System.out.println("Lookup node - should be nothing there");
        ex.lookupNode(params);
        System.out.println("Add node - should be something to add");
        ex.addNode(params);
        System.out.println("Add same node again - should have no effect");
        ex.addNode(params);
        System.out.println("Lookup node - should be a node there");
        ex.lookupNode(params);
        System.out.println("Delete node - should be a node to delete");
        ex.deleteNode(params);
        System.out.println("Delete node - should be NO node to delete");
        ex.deleteNode(params);
      }
    }
    
  }
}
