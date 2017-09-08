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
package org.tssg.solas.bb.cormel2neo.domain;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tssg.solas.bb.cormel2neo.domain.shared.CormelNodeShared;
import org.tssg.solas.bb.cormel2neo.domain.shared.Field;
import org.tssg.solas.bb.cormel2neo.domain.shared.ICormelNode;
import org.tssg.solas.bb.cormel2neo.domain.shared.NodeOpSummary;
import org.tssg.solas.bb.cormel2neo.domain.shared.NodeType;
import org.tssg.solas.bb.cormel2neo.learn.ExampleCRUD;

/**
 * @author bbutler
 *
 */
public class CormelHNode implements ICormelNode {

  Logger log = LoggerFactory.getLogger(ExampleCRUD.class);
  private static final String defaultConnectionString = "bolt://localhost:7687";
  private static final NodeType nodeType = NodeType.Hsegment;
  private Map<String, Object> params = new HashMap<>();
  private Transaction tx;
  private CormelNodeShared cormelNode;
  
  public CormelHNode(Transaction tx) {
    this.tx = tx;
    this.cormelNode = new CormelNodeShared(nodeType, tx);
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#getCormelNode()
   */
  @Override
  public CormelNodeShared getCormelNode() {
    return cormelNode;
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#getParams()
   */
  @Override
  public Map<String, Object> getParams() {
    return params;
  }

  public static String deriveKey(Map<String,Object> params) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.valueOf(params.get(Field.Node_CormelFile)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_DuName)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_HopTimeSec)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_RespTimeSec)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_ContraHopTimeSec)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_InFlightTimeSec)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_InboundQuerySizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_OutboundQuerySizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_InboundReplySizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_OutboundReplySizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_InboundQZippedSizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_OutboundQZippedSizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_InboundRZippedSizeB)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.H_OutboundRZippedSizeB)));
    return sb.toString();
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#addNode(java.util.Map)
   */
  @Override
  public NodeOpSummary addNode(Map<String,Object> params) {
    this.params = params;
    String key = deriveKey(params);
    StatementResult result = tx.run("MERGE (n:" + nodeType + " {" +
      Field.Node_CormelFile + ": {" + Field.Node_CormelFile + "}," +
      Field.H_DuName + ": {" + Field.H_DuName + "}," +
      Field.H_HopTimeSec + ": {" + Field.H_HopTimeSec + "}," +
      Field.H_RespTimeSec + ": {" + Field.H_RespTimeSec + "}," +
      Field.H_ContraHopTimeSec + ": {" + Field.H_ContraHopTimeSec + "}," +
      Field.H_InFlightTimeSec + ": {" + Field.H_InFlightTimeSec + "}," +
      Field.H_InboundQuerySizeB + ": {" + Field.H_InboundQuerySizeB + "}," +
      Field.H_OutboundQuerySizeB + ": {" + Field.H_OutboundQuerySizeB + "}," +
      Field.H_InboundReplySizeB + ": {" + Field.H_InboundReplySizeB + "}," +
      Field.H_OutboundReplySizeB + ": {" + Field.H_OutboundReplySizeB + "}," +
      Field.H_InboundQZippedSizeB + ": {" + Field.H_InboundQZippedSizeB + "}," +
      Field.H_OutboundQZippedSizeB + ": {" + Field.H_OutboundQZippedSizeB + "}," +
      Field.H_InboundRZippedSizeB + ": {" + Field.H_InboundRZippedSizeB + "}," +
      Field.H_OutboundRZippedSizeB + ": {" + Field.H_OutboundRZippedSizeB + "}," +
      "nodeType: {nodeType}," +
      "key: {key}" +
      "}) RETURN ID(n) AS nodeId",
      parameters(
          Field.Node_CormelFile, params.get(Field.Node_CormelFile),
          Field.H_DuName, params.get(Field.H_DuName),
          Field.H_HopTimeSec, params.get(Field.H_HopTimeSec),
          Field.H_RespTimeSec, params.get(Field.H_RespTimeSec),
          Field.H_ContraHopTimeSec, params.get(Field.H_ContraHopTimeSec),
          Field.H_InFlightTimeSec, params.get(Field.H_InFlightTimeSec),
          Field.H_InboundQuerySizeB, params.get(Field.H_InboundQuerySizeB),
          Field.H_OutboundQuerySizeB, params.get(Field.H_OutboundQuerySizeB),
          Field.H_InboundReplySizeB, params.get(Field.H_InboundReplySizeB),
          Field.H_OutboundReplySizeB, params.get(Field.H_OutboundReplySizeB),
          Field.H_InboundQZippedSizeB, params.get(Field.H_InboundQZippedSizeB),
          Field.H_OutboundQZippedSizeB, params.get(Field.H_OutboundQZippedSizeB),
          Field.H_InboundRZippedSizeB, params.get(Field.H_InboundRZippedSizeB),
          Field.H_OutboundRZippedSizeB, params.get(Field.H_OutboundRZippedSizeB),
          "nodeType", String.valueOf(nodeType),
          "key", key
          )
      );
    NodeOpSummary ns = cormelNode.addNodeExtra(params, key, result);
    return ns;
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#lookupNode(java.util.Map)
   */
  @Override
  public NodeOpSummary lookupNode(Map<String, Object> params) {
    String key = deriveKey(params);
    NodeOpSummary ns = cormelNode.lookupNode(params, key);
    return ns;
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#deleteNode(java.util.Map)
   */
  @Override
  public NodeOpSummary deleteNode(Map<String, Object> params) {
    String key = deriveKey(params);
    NodeOpSummary ns = cormelNode.deleteNode(params, key);
    return ns;
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#updateNode(java.util.Map, java.util.String)
   */
  @Override
  public NodeOpSummary updateNode(Map<String, Object> params, String key) {
    return null; // Not implemented yet
  }

  public static void main(String[] args) {
    Map<String,Object> params = new HashMap<>();
    params.put(Field.Node_CormelFile, "dataSource");
    params.put(Field.H_DuName, "duName");
    params.put(Field.H_HopTimeSec, 100);
    params.put(Field.H_RespTimeSec, 90);
    params.put(Field.H_ContraHopTimeSec, 80);
    params.put(Field.H_InFlightTimeSec, 70);
    params.put(Field.H_InboundQuerySizeB, 60);
    params.put(Field.H_OutboundQuerySizeB, 50);
    params.put(Field.H_InboundReplySizeB, 40);
    params.put(Field.H_OutboundReplySizeB, 30);
    params.put(Field.H_InboundQZippedSizeB, 20);
    params.put(Field.H_OutboundQZippedSizeB, "");
    params.put(Field.H_InboundRZippedSizeB, 10);
    params.put(Field.H_OutboundRZippedSizeB, 0);

    try (Driver driver = GraphDatabase.driver(defaultConnectionString)) {
    // try (Driver driver = GraphDatabase.driver(connectionString,
    // AuthTokens.basic("neo4j", "neo4j"))) {
      try (Session session = driver.session()) {
        Transaction tx = session.beginTransaction();
        ICormelNode ex = new CormelHNode(tx);
        CormelNodeShared cns = ex.getCormelNode();
        
        String key = CormelUNode.deriveKey(params);
        Map<String,Object> params2 = new HashMap<>();
        cns.testCRUD(params, ex, key, params2);

        tx.close();
      }
    }
    
  }

}
