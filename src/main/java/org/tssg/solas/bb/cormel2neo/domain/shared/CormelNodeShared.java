/*
Copyright (c) 2017, Bernard Butler (Waterford Institute of Technology, Ireland), Project: SOLAS placement in Amadeus SA, where SOLAS (Project ID: 612480) is funded by the European Commision FP7 MC-IAPP-Industry-Academia Partnerships and Pathways scheme.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 -  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 -  Neither the name of WATERFORD INSTITUTE OF TECHNOLOGY nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/**
 * 
 */
package org.tssg.solas.bb.cormel2neo.domain.shared;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

/**
 * @author bbutler
 *
 */
public class CormelNodeShared {

  private NodeType nodeType;
  private Transaction tx;
  
  public CormelNodeShared(NodeType nodeType, Transaction tx) {
    this.nodeType = nodeType;
    this.tx = tx;
  }

  public void progressReport(PrintStream outputStream, String s) {
    // See https://stackoverflow.com/a/20677345
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm:ss"));
    outputStream.println(now+" "+s);
  }

  public static void applyConstraint(Session session, NodeType nodeType) {
    session.run("CREATE CONSTRAINT ON (n:" + nodeType + ") ASSERT n.key IS UNIQUE");
  }

  public static void applyIndex(Session session, NodeType nodeType, String property) {
    session.run("CREATE INDEX ON :" + nodeType + "("+property+")");
  }

  public static void applyIndex(Session session, NodeType nodeType, Set<String> properties) {
    String propertiesStr = properties.toString().replace("[", "").replace("]", "");
    session.run("CREATE INDEX ON :" + nodeType + "("+propertiesStr+")");
  }

  public NodeOpSummary addNodeExtra(Map<String, Object> params, String key, StatementResult result) {
    long nodeId = -1;
//    if (!result.hasNext()) {
//      progressReport("Oops: expecting to find a result of inserting/updating " + nodeType + " with key " + key);
//    }
    while (result.hasNext()) {
      Record record = result.next();
      nodeId = Long.valueOf(String.valueOf(record.get("nodeId")));
//      progressReport("Added/Found " + nodeType + " with nodeId = " + nodeId + " and key " + key);
    }
    int nrows = result.consume().counters().nodesCreated();
    NodeOpSummary ns = new NodeOpSummary(nodeType, params, key, nodeId, EntityOperation.CREATE, nrows);
//    progressReport(nodeType + " " + nrows+" nodes were created");
    return ns;
  }

  public NodeOpSummary lookupNode(Map<String, Object> params, String key) {
    int nrows = 0;
    StatementResult result = tx.run(
        "MATCH (n:" + nodeType + ") WHERE n.key = {key} RETURN ID(n) AS nodeId",
        parameters("key", key));
    long nodeId = -1;
    if (result.hasNext()) {
      while (result.hasNext()) {
        Record record = result.next();
        nodeId = Long.valueOf(String.valueOf(record.get("nodeId")));
//        progressReport("Found " + nodeType + " with nodeId = " + nodeId);
      }
    } else {
//      progressReport(nodeType + " node with params " + params + " not found!");
    }
    NodeOpSummary ns = new NodeOpSummary(nodeType, params, key, nodeId, EntityOperation.FIND, nrows);
    return ns;
  }

  public NodeOpSummary deleteNode(Map<String, Object> params, String key) {
    StatementResult result = tx.run(
        "MATCH (n:" + nodeType + ") WHERE n.key = {key} DELETE n",
        parameters("key", key));
    int nrows = result.consume().counters().nodesDeleted();
//    progressReport(nrows + " nodes were deleted");
    Long nodeId = null;
    NodeOpSummary ns = new NodeOpSummary(nodeType, params, key, nodeId, EntityOperation.FIND, nrows);
    return ns;
  }

  public NodeOpSummary updateNodeExtra(Map<String, Object> params, String key, StatementResult result) {
    long nodeId = -1;
    while (result.hasNext()) {
      Record record = result.next();
      nodeId = Long.valueOf(String.valueOf(record.get("nodeId")));
    }
    int nrows = result.consume().counters().propertiesSet();
    NodeOpSummary ns = new NodeOpSummary(nodeType, params, key, nodeId, EntityOperation.UPDATE, nrows);
    return ns;
  }

  public void testCRUD(Map<String,Object> params, ICormelNode ex, String key, Map<String,Object> params2) {
    NodeOpSummary nos;
    progressReport(System.out,"Delete node - should be nothing to delete");
    nos = ex.deleteNode(params);
    progressReport(System.out,"Delete node - result is "+nos);

    progressReport(System.out,"Lookup node - should be nothing there");
    nos = ex.deleteNode(params);
    progressReport(System.out,"Lookup node - result is "+nos);

    progressReport(System.out,"Add node - should be something to add");
    nos = ex.addNode(params);
    progressReport(System.out,"Add node - result is "+nos);

    progressReport(System.out,"Update node - have "+params2.size()+" properties to set");
    nos = ex.updateNode(params2, key);
    progressReport(System.out,"Updated node - set "+nos.getOpCount()+" properties.");
    
    progressReport(System.out,"Add same node again - should have no effect");
    nos = ex.addNode(params);
    progressReport(System.out,"Add node again - result is "+nos);

    progressReport(System.out,"Lookup node - should be a node there");
    nos = ex.lookupNode(params);
    progressReport(System.out,"Lookup node - result is "+nos);

    progressReport(System.out,"Delete node - should be a node to delete");
    nos = ex.deleteNode(params);
    progressReport(System.out,"Delete node - result is "+nos);

    progressReport(System.out,"Delete node - should be NO node to delete");
    nos = ex.deleteNode(params);
    progressReport(System.out,"Delete node - result is "+nos);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
  }

}
