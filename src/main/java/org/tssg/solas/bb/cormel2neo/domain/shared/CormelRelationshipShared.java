/*
Copyright (c) 2017, Bernard Butler (Waterford Institute of Technology, Ireland), Project: SOLAS placement in Amadeus SA, where SOLAS (Project ID: 612480) is funded by the European Commision FP7 MC-IAPP-Industry-Academia Partnerships and Pathways scheme.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 -  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 -  Neither the name of WATERFORD INSTITUTE OF TECHNOLOGY nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.tssg.solas.bb.cormel2neo.domain.shared;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class CormelRelationshipShared {

  private static Table<String, String, Integer> treeKeyLookup = HashBasedTable.create();
  
  public static Map<Long, List<Long>> deriveRelSummary(Transaction tx) {
    // First accumulate, for each combination of fromNodeId and toNodeId, the associated list of relId (usually just one element)
    Table<Long, Long, List<Long>> relSummary = HashBasedTable.create();
    long fromNodeId = -1;
    long toNodeId = -1;
    long relId = -1;
    List<Long> relIdList;
    StatementResult result = tx
        .run("MATCH (n1)-[r]->(n2) WHERE NOT EXISTS (r." + Field.Rel_TreeList + ") RETURN ID(n1) AS fromNodeId, ID(n2) AS toNodeId, ID(r) AS relId;");
    while (result.hasNext()) {
      Record record = result.next();
      fromNodeId = Long.valueOf(String.valueOf(record.get("fromNodeId")));
      toNodeId = Long.valueOf(String.valueOf(record.get("toNodeId")));
      relId = Long.valueOf(String.valueOf(record.get("relId")));
      if (relSummary.contains(fromNodeId, toNodeId)) {
        relIdList = relSummary.get(fromNodeId, toNodeId);
      } else {
        relIdList = new ArrayList<Long>();
        relSummary.put(fromNodeId, toNodeId, relIdList);
      }
      relIdList.add(relId);
    }
    // Now generate a map from relId to the list of relId that share this edge, including relId itself
    Map<Long, List<Long>> relMap = new HashMap<>();
    for (Cell<Long, Long, List<Long>> relDetail : relSummary.cellSet()) {
      List<Long> relIds = relDetail.getValue();
      for (Long relIdFromList : relIds) {
        relMap.put(relIdFromList, relIds);
      }
    }
    return relMap;
  }
  
  public static int addRelTreeListToRelationship(Transaction tx, Long relId, List<Long> relIdList) {
    int totalPropertiesSet = 0;
    StatementResult result;
    result = tx.run("MATCH ()-[r]->() WHERE ID(r) IN {relIdList} RETURN COLLECT(r.Rel_TreeKey) AS relTreeKeyList;",
        parameters("relIdList", relIdList));
    while (result.hasNext()) { // should be just one result
      Record record = result.next();
      String relTreeKeyListStr = String.valueOf(record.get("relTreeKeyList"));
      result = tx.run(
          "MATCH ()-[r]->() WHERE ID(r) = {relId} SET r." + Field.Rel_TreeList + " = {" + Field.Rel_TreeList + "};",
          parameters("relId", relId, Field.Rel_TreeList, relTreeKeyListStr));
      ResultSummary resultSummary = result.summary();
      SummaryCounters summaryCounters = resultSummary.counters();
      totalPropertiesSet = summaryCounters.propertiesSet();
    }
    return totalPropertiesSet;
  }

  public static RelOpSummary createRel(Transaction tx, NodeType fromNodeType, String fromKey, NodeType toNodeType, String toKey, RelType relType, Map<String, Object> treeKeyParams) {
    //System.out.println("fromKey, toKey = "+fromKey+", "+toKey);
    int nrows = 0;
    RelOpSummary relOpSummary = null;
    if (!toKey.isEmpty()) {
      StatementResult result = tx.run(
          "MATCH (from:" + fromNodeType + "),(to:" + toNodeType + ") " +
          "WHERE from.key = {fromKey} AND to.key = {toKey} "+
          "MERGE (from)-[:" + relType + " { " +
          Field.Rel_DcxId + ": {" + Field.Rel_DcxId + "}, " +
          Field.Rel_TreeId + ": {" + Field.Rel_TreeId + "}, " +
          Field.Rel_TreeKey + ": {" + Field.Rel_TreeKey + "} " +
          "}]->(to)",
          parameters(
              "fromKey", fromKey,
              "toKey", toKey,
              Field.Rel_DcxId, treeKeyParams.get(Field.Rel_DcxId),
              Field.Rel_TreeId, treeKeyParams.get(Field.Rel_TreeId),
              Field.Rel_TreeKey, treeKeyParams.get(Field.Rel_TreeKey)
              )
          );
     nrows = result.consume().counters().relationshipsCreated();
     relOpSummary = new RelOpSummary(relType, fromNodeType, toNodeType, EntityOperation.CREATE, nrows);
    }
//    progressReport(nrows + " relationships were created");
    return relOpSummary;
  }
  
  public static int addOrFindTreeKey(String dcxId, String treeId, int maxSurrogateId) {
    int foundId = 0;
    if (!treeKeyLookup.contains(dcxId, treeId)) {
      foundId = maxSurrogateId+1;
      treeKeyLookup.put(dcxId, treeId, foundId);
    } else {
      foundId = treeKeyLookup.get(dcxId, treeId);
    }
    return foundId;
  }

}
