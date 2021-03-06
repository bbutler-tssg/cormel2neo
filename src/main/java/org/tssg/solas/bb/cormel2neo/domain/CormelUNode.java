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
import org.neo4j.driver.v1.Record;
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

/**
 * @author bbutler
 *
 */
public class CormelUNode implements ICormelNode {

  Logger log = LoggerFactory.getLogger(CormelUNode.class);
  private static final String defaultConnectionString = "bolt://localhost:7687";
  private static final NodeType nodeType = NodeType.Usegment;
  private Map<String, Object> params = new HashMap<>();
  private Transaction tx;
  private CormelNodeShared cormelNode;
  
  public CormelUNode(Transaction tx) {
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
    sb.append(String.valueOf(params.get(Field.U_DcxId)));
    sb.append("_");
    sb.append(String.valueOf(params.get(Field.U_TreeId)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_TimeStamp)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_Initiator)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_Destination)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_SAPName)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_OfficeID)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_ATID)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_ServiceType)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_BEType)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_DCD)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_PnrId)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_QueryIPAddressPort)));
//    sb.append("_");
//    sb.append(String.valueOf(params.get(Fields.U_ParentDcxId)));
    return sb.toString();
  }

  /* (non-Javadoc)
   * @see org.tssg.solas.bb.cormel2neo.domain.ICormelNode#addNode(java.util.Map)
   */
  @Override
  public NodeOpSummary addNode(Map<String,Object> params) {
    String key = deriveKey(params);
    StatementResult result;
    if (params.keySet().contains(Field.Node_AppEventFile)) {
      result = tx.run("MERGE (n:" + nodeType + " {" +
        Field.Node_CormelFile + ": {" + Field.Node_CormelFile + "}," +
        Field.Node_AppEventFile + ": {" + Field.Node_AppEventFile + "}," +
        Field.U_DcxId + ": {" + Field.U_DcxId + "}," +
        Field.U_TreeId + ": {" + Field.U_TreeId + "}," +
        Field.U_TimeStamp + ": {" + Field.U_TimeStamp + "}," +
        Field.U_Initiator + ": {" + Field.U_Initiator + "}," +
        Field.U_Destination + ": {" + Field.U_Destination + "}," +
        Field.U_SAPName + ": {" + Field.U_SAPName + "}," +
        Field.U_OfficeID + ": {" + Field.U_OfficeID + "}," +
        Field.U_ATID + ": {" + Field.U_ATID + "}," +
        Field.U_ServiceType + ": {" + Field.U_ServiceType + "}," +
        Field.U_BEType + ": {" + Field.U_BEType + "}," +
        Field.U_DCD + ": {" + Field.U_DCD + "}," +
        Field.U_PnrId + ": {" + Field.U_PnrId + "}," +
        Field.U_QueryIPAddressPort + ": {" + Field.U_QueryIPAddressPort + "}," +
        Field.U_ParentDcxId + ": {" + Field.U_ParentDcxId + "}," +
        "nodeType: {nodeType}," +
        "key: {key}" +
        "}) RETURN ID(n) AS nodeId",
        parameters(
          Field.Node_CormelFile, params.get(Field.Node_CormelFile),
          Field.Node_AppEventFile, params.get(Field.Node_AppEventFile),
          Field.U_DcxId, params.get(Field.U_DcxId),
          Field.U_TreeId, params.get(Field.U_TreeId),
          Field.U_TimeStamp, params.get(Field.U_TimeStamp),
          Field.U_Initiator, params.get(Field.U_Initiator),
          Field.U_Destination, params.get(Field.U_Destination),
          Field.U_SAPName, params.get(Field.U_SAPName),
          Field.U_OfficeID, params.get(Field.U_OfficeID),
          Field.U_ATID, params.get(Field.U_ATID),
          Field.U_ServiceType, params.get(Field.U_ServiceType),
          Field.U_BEType, params.get(Field.U_BEType),
          Field.U_DCD, params.get(Field.U_DCD),
          Field.U_PnrId, params.get(Field.U_PnrId),
          Field.U_QueryIPAddressPort, params.get(Field.U_QueryIPAddressPort),
          Field.U_ParentDcxId, params.get(Field.U_ParentDcxId),
          "nodeType", String.valueOf(nodeType),
          "key", key
          )
        );
    } else {
      result = tx.run("MERGE (n:" + nodeType + " {" +
          Field.Node_CormelFile + ": {" + Field.Node_CormelFile + "}," +
          Field.U_DcxId + ": {" + Field.U_DcxId + "}," +
          Field.U_TreeId + ": {" + Field.U_TreeId + "}," +
          Field.U_TimeStamp + ": {" + Field.U_TimeStamp + "}," +
          Field.U_Initiator + ": {" + Field.U_Initiator + "}," +
          Field.U_Destination + ": {" + Field.U_Destination + "}," +
          Field.U_SAPName + ": {" + Field.U_SAPName + "}," +
          Field.U_OfficeID + ": {" + Field.U_OfficeID + "}," +
          Field.U_ATID + ": {" + Field.U_ATID + "}," +
          Field.U_ServiceType + ": {" + Field.U_ServiceType + "}," +
          Field.U_BEType + ": {" + Field.U_BEType + "}," +
          Field.U_DCD + ": {" + Field.U_DCD + "}," +
          Field.U_PnrId + ": {" + Field.U_PnrId + "}," +
          Field.U_QueryIPAddressPort + ": {" + Field.U_QueryIPAddressPort + "}," +
          Field.U_ParentDcxId + ": {" + Field.U_ParentDcxId + "}," +
          "nodeType: {nodeType}," +
          "key: {key}" +
          "}) RETURN ID(n) AS nodeId",
          parameters(
              Field.Node_CormelFile, params.get(Field.Node_CormelFile),
              Field.U_DcxId, params.get(Field.U_DcxId),
              Field.U_TreeId, params.get(Field.U_TreeId),
              Field.U_TimeStamp, params.get(Field.U_TimeStamp),
              Field.U_Initiator, params.get(Field.U_Initiator),
              Field.U_Destination, params.get(Field.U_Destination),
              Field.U_SAPName, params.get(Field.U_SAPName),
              Field.U_OfficeID, params.get(Field.U_OfficeID),
              Field.U_ATID, params.get(Field.U_ATID),
              Field.U_ServiceType, params.get(Field.U_ServiceType),
              Field.U_BEType, params.get(Field.U_BEType),
              Field.U_DCD, params.get(Field.U_DCD),
              Field.U_PnrId, params.get(Field.U_PnrId),
              Field.U_QueryIPAddressPort, params.get(Field.U_QueryIPAddressPort),
              Field.U_ParentDcxId, params.get(Field.U_ParentDcxId),
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
    StatementResult result = tx.run(
        "MATCH (n:" + nodeType + ") "+
        "WHERE n.key = {key} "+
        "SET n += {"+
        Field.Node_AppEventFile + ": {" + Field.Node_AppEventFile + "}"+
        "} RETURN ID(n) AS nodeId;"
        ,
        parameters(
            "key", key,
            Field.Node_AppEventFile, params.get(Field.Node_AppEventFile)
            )
        );
      NodeOpSummary ns = cormelNode.updateNodeExtra(params, key, result);
      return ns;
  }

  public static Map<String,Object> selectTreeKey(Map<String,Object> params, int surrogateId) {
    Map<String,Object> treeKeyParams = new HashMap<>();
    treeKeyParams.put(Field.Rel_DcxId, params.get(Field.U_DcxId));
    treeKeyParams.put(Field.Rel_TreeId, params.get(Field.U_TreeId));
    treeKeyParams.put(Field.Rel_TreeKey, surrogateId);
    return treeKeyParams;
  }

  public NodeOpSummary updateSilhouette(String key) {
    // MATCH (u:Usegment)-[r]->(tRoot:Tsegment) MATCH (tRoot)-[*]->(t:Tsegment) WITH u, t.DcxId AS DcxId, t.TrxNb AS TrxNb ORDER BY DcxId, TrxNb WITH u, DcxId, TrxNb RETURN DcxId, u.TreeId AS TreeId, COLLECT(TrxNb) AS TrxNbList;
    StatementResult result = tx.run(
        "MATCH (u:Usegment)-[r]->(tRoot:Tsegment) "+
        "WHERE u.key = {key} "+
        "MATCH (tRoot)-[*]->(t:Tsegment) "+
        "WITH u, t.DcxId AS DcxId, t.TrxNb AS TrxNb ORDER BY DcxId, TrxNb "+
        "WITH TrxNb RETURN COLLECT(TrxNb) AS TrxNbList;"
        ,
        parameters(
            "key", key
            )
        );
    while (result.hasNext()) { // should be just one result
      Record record = result.next();
      String trxNbList = normaliseSilhouette(String.valueOf(record.get("TrxNbList")));
      System.out.println("Need to normalise trxNbList "+trxNbList+" before updating U node with key "+key);
    }
    result = tx.run(
        "MATCH (n:Usegment) "+
        "WHERE n.key = {key} "+
        "SET n += {"+
        Field.Silhouette + ": {" + Field.Silhouette + "}"+
        "} RETURN ID(n) AS nodeId;"
        ,
        parameters(
            "key", key,
            Field.Silhouette, params.get(Field.Silhouette)
            )
        );
    NodeOpSummary ns = cormelNode.updateNodeExtra(params, key, result);
    return ns;
  }
  
  private String normaliseSilhouette(String rawTrxNbList) {
    System.out.println("Dummy normalise - TODO");
    return rawTrxNbList;
  }
  
  public static void main(String[] args) {
    Map<String,Object> params = new HashMap<>();
    params.put(Field.Node_CormelFile, "dataSource");
    params.put(Field.U_DcxId, "DcxId");
    params.put(Field.U_TreeId, "TreeId");
    params.put(Field.U_TimeStamp, "TimeStamp");
    params.put(Field.U_Initiator, "Initiator");
    params.put(Field.U_Destination, "Destination");
    params.put(Field.U_SAPName, "SAPName");
    params.put(Field.U_OfficeID, "OfficeID");
    params.put(Field.U_ATID, "ATID");
    params.put(Field.U_ServiceType, "ServiceType");
    params.put(Field.U_BEType, "BEType");
    params.put(Field.U_DCD, "DCD");
    params.put(Field.U_PnrId, "PnrId");
    params.put(Field.U_QueryIPAddressPort, "QueryIPAddressPort");
    params.put(Field.U_ParentDcxId, "ParentDcxId");

    try (Driver driver = GraphDatabase.driver(defaultConnectionString)) {
    // try (Driver driver = GraphDatabase.driver(connectionString,
    // AuthTokens.basic("neo4j", "neo4j"))) {
      try (Session session = driver.session()) {
        Transaction tx = session.beginTransaction();
        ICormelNode ex = new CormelUNode(tx);
        CormelNodeShared cns = ex.getCormelNode();
        
        int surrogateKey = 1;
        Map<String, Object> treeKeyParams = CormelUNode.selectTreeKey(params, surrogateKey);
        cns.progressReport(System.out, "Derived treeKeyParams = "+treeKeyParams);
        
        String key = CormelUNode.deriveKey(params);
        Map<String,Object> params2 = new HashMap<>();
        cns.testCRUD(params, ex, key, params2);

        tx.close();
      }
    }
    
  }

}
