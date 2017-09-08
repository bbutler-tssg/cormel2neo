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

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bbutler
 *
 */
public class CormelTNode implements ICormelNode {

  Logger log = LoggerFactory.getLogger(CormelTNode.class);
  private static final String defaultConnectionString = "bolt://localhost:7687";
  private static final NodeType nodeType = NodeType.Tsegment;
  private Map<String, Object> params = new HashMap<>();
  private Transaction tx;
  private CormelNodeShared cormelNode;
  
  public CormelTNode(Transaction tx) {
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
    sb.append(String.valueOf(params.get(Field.T_DcxId)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.T_TrxNb)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_CausingId)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_TimeStamp)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_Initiator)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_Destination)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_SAPName)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_QueryType)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_QueryIPAddressPort)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_ReplyType)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_ReplyErrorCode)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.T_Flags)));
    return sb.toString();
  }

  public static Map<String,Object> deriveTreeKeyParams(Transaction tx, Map<String,Object> params) {
    Map<String, Object> treeKeyParams = new HashMap<>();
  // MATCH ()-[r]->(n:Tsegment) WHERE n.dataSource = "par..." AND n.DcxId = "ZWF0YYNP9QH21HMBYZ$T6W5U91" AND n.TrxNb = "1-1" RETURN r.Rel_DcxId, r.Rel_TreeId, r.Rel_TreeKey;
    StatementResult result = tx.run("MATCH ()-[r]->(n:" + nodeType + ") WHERE n." +
//        Field.Node_CormelFile + " = {" + Field.Node_CormelFile + "}, AND n." +
        Field.T_DcxId + " = {" + Field.T_DcxId + "}, AND n." +
        Field.T_TrxNb + " = {" + Field.T_TrxNb + "} RETURN r." +
        Field.Rel_DcxId + ", r." +
        Field.Rel_TreeId + ", r." +
        Field.Rel_TreeKey + ";",
        parameters(
            Field.T_DcxId, params.get(Field.T_DcxId),
            Field.T_TrxNb, params.get(Field.T_TrxNb)
            )
        );
    if (result.hasNext()) {
      Record rec = result.next();
      if (null != rec) {
        Map<String, Object> dMap = rec.asMap();
        String[] relFields = { Field.Rel_DcxId, Field.Rel_TreeId, Field.Rel_TreeKey };
        for (String f : relFields) {
          treeKeyParams.put(f, dMap.get("r." + f));
        }
      }
      if (result.hasNext()) {
        System.err.println("WARNING: Transaction Node has more than 1 parent (U or T node) when only 1 is expected");
      }
    } else {
      System.err.println("ERROR: Transaction Node has no parent (U or T node) when 1 is required");      
    }
    return treeKeyParams;
  }
  
  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#addNode(java.util.Map)
   */
  @Override
  public NodeOpSummary addNode(Map<String,Object> params) {
    this.params = params;
    String key = deriveKey(params);
    StatementResult result;
    if (params.keySet().contains(Field.AppEvent_T_Status)) {
      result = tx.run("MERGE (n:" + nodeType + " {" +
          Field.AppEvent_T_Status + ": {" + Field.AppEvent_T_Status + "}," +
          Field.Node_CormelFile + ": {" + Field.Node_CormelFile + "}," +
          Field.Node_AppEventFile + ": {" + Field.Node_AppEventFile + "}," +
          Field.T_DcxId + ": {" + Field.T_DcxId + "}," +
          Field.T_TrxNb + ": {" + Field.T_TrxNb + "}," +
          Field.T_CausingId + ": {" + Field.T_CausingId + "}," +
          Field.T_TimeStamp + ": {" + Field.T_TimeStamp + "}," +
          Field.T_Initiator + ": {" + Field.T_Initiator + "}," +
          Field.T_Destination + ": {" + Field.T_Destination + "}," +
          Field.T_SAPName + ": {" + Field.T_SAPName + "}," +
          Field.T_QueryType + ": {" + Field.T_QueryType + "}," +
          Field.T_QueryIPAddressPort + ": {" + Field.T_QueryIPAddressPort + "}," +
          Field.T_ReplyType + ": {" + Field.T_ReplyType + "}," +
          Field.T_ReplyErrorCode + ": {" + Field.T_ReplyErrorCode + "}," +
          Field.T_Flags + ": {" + Field.T_Flags + "}," +
          "nodeType: {nodeType}," +
          "key: {key}" +
          "}) RETURN ID(n) AS nodeId",
          parameters(
              Field.AppEvent_T_Status, params.get(Field.AppEvent_T_Status),
              Field.Node_CormelFile, params.get(Field.Node_CormelFile),
              Field.Node_AppEventFile, params.get(Field.Node_AppEventFile),
              Field.T_DcxId, params.get(Field.T_DcxId),
              Field.T_TrxNb, params.get(Field.T_TrxNb),
              Field.T_CausingId, params.get(Field.T_CausingId),
              Field.T_TimeStamp, params.get(Field.T_TimeStamp),
              Field.T_Initiator, params.get(Field.T_Initiator),
              Field.T_Destination, params.get(Field.T_Destination),
              Field.T_SAPName, params.get(Field.T_SAPName),
              Field.T_QueryType, params.get(Field.T_QueryType),
              Field.T_QueryIPAddressPort, params.get(Field.T_QueryIPAddressPort),
              Field.T_ReplyType, params.get(Field.T_ReplyType),
              Field.T_ReplyErrorCode, params.get(Field.T_ReplyErrorCode),
              Field.T_Flags, params.get(Field.T_Flags),
              "nodeType", String.valueOf(nodeType),
              "key", key
              )
          );
    } else {
      result = tx.run("MERGE (n:" + nodeType + " {" +
          Field.Node_CormelFile + ": {" + Field.Node_CormelFile + "}," +
          Field.T_DcxId + ": {" + Field.T_DcxId + "}," +
          Field.T_TrxNb + ": {" + Field.T_TrxNb + "}," +
          Field.T_CausingId + ": {" + Field.T_CausingId + "}," +
          Field.T_TimeStamp + ": {" + Field.T_TimeStamp + "}," +
          Field.T_Initiator + ": {" + Field.T_Initiator + "}," +
          Field.T_Destination + ": {" + Field.T_Destination + "}," +
          Field.T_SAPName + ": {" + Field.T_SAPName + "}," +
          Field.T_QueryType + ": {" + Field.T_QueryType + "}," +
          Field.T_QueryIPAddressPort + ": {" + Field.T_QueryIPAddressPort + "}," +
          Field.T_ReplyType + ": {" + Field.T_ReplyType + "}," +
          Field.T_ReplyErrorCode + ": {" + Field.T_ReplyErrorCode + "}," +
          Field.T_Flags + ": {" + Field.T_Flags + "}," +
          "nodeType: {nodeType}," +
          "key: {key}" +
          "}) RETURN ID(n) AS nodeId",
          parameters(
              Field.Node_CormelFile, params.get(Field.Node_CormelFile),
              Field.T_DcxId, params.get(Field.T_DcxId),
              Field.T_TrxNb, params.get(Field.T_TrxNb),
              Field.T_CausingId, params.get(Field.T_CausingId),
              Field.T_TimeStamp, params.get(Field.T_TimeStamp),
              Field.T_Initiator, params.get(Field.T_Initiator),
              Field.T_Destination, params.get(Field.T_Destination),
              Field.T_SAPName, params.get(Field.T_SAPName),
              Field.T_QueryType, params.get(Field.T_QueryType),
              Field.T_QueryIPAddressPort, params.get(Field.T_QueryIPAddressPort),
              Field.T_ReplyType, params.get(Field.T_ReplyType),
              Field.T_ReplyErrorCode, params.get(Field.T_ReplyErrorCode),
              Field.T_Flags, params.get(Field.T_Flags),
              "nodeType", String.valueOf(nodeType),
              "key", key
              )
          );
    }
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
    //MATCH (n:Tsegment) WHERE n.key = "C9WYTQMA07J0194CPGAVIUZT91_1" SET n += {TransactionStatus : "KO"} RETURN ID(n);
    StatementResult result = tx.run(
      "MATCH (n:" + nodeType + ") "+
      "WHERE n.key = {key} "+
      "SET n += {"+
      Field.AppEvent_T_Status + ": {" + Field.AppEvent_T_Status + "}, "+
      Field.Node_AppEventFile + ": {" + Field.Node_AppEventFile + "}"+
      "} RETURN ID(n) AS nodeId;"
      ,
      parameters(
          "key", key,
          Field.AppEvent_T_Status, params.get(Field.AppEvent_T_Status),
          Field.Node_AppEventFile, params.get(Field.Node_AppEventFile)
          )
      );
    NodeOpSummary ns = cormelNode.updateNodeExtra(params, key, result);
    return ns;
  }

  public static void main(String[] args) {
    Map<String,Object> params = new HashMap<>();
    params.put(Field.Node_CormelFile, "dataSource");
    params.put(Field.T_DcxId, "DcxId");
    params.put(Field.T_TrxNb, "TrxNb");
    params.put(Field.T_CausingId, "CausingId");
    params.put(Field.T_TimeStamp, 123456789);
    params.put(Field.T_Initiator, "Initiator");
    params.put(Field.T_Destination, "Destination");
    params.put(Field.T_SAPName, "SAPName");
    params.put(Field.T_QueryType, "QueryType");
    params.put(Field.T_QueryIPAddressPort, "QueryIPAddressPort");
    params.put(Field.T_ReplyType, "ReplyType");
    params.put(Field.T_ReplyErrorCode, "ReplyErrorCode");
    params.put(Field.T_Flags, "Flags");

    Map<String,Object> params2 = new HashMap<>();
    params2.put(Field.Node_CormelFile, "dataSource");
    params2.put(Field.AppEvent_M_Code, "OK");

    try (Driver driver = GraphDatabase.driver(defaultConnectionString)) {
    // try (Driver driver = GraphDatabase.driver(connectionString,
    // AuthTokens.basic("neo4j", "neo4j"))) {
      try (Session session = driver.session()) {
        Transaction tx = session.beginTransaction();
        ICormelNode ex = new CormelTNode(tx);
        CormelNodeShared cns = ex.getCormelNode();
        String key = CormelTNode.deriveKey(params);
        
        cns.testCRUD(params, ex, key, params2);

        tx.close();
      }
    }
    
  }

}
