/*
Copyright (c) 2017, Bernard Butler (Waterford Institute of Technology, Ireland), Project: SOLAS placement in Amadeus SA, where SOLAS (Project ID: 612480) is funded by the European Commision FP7 MC-IAPP-Industry-Academia Partnerships and Pathways scheme.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 -  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 -  Neither the name of WATERFORD INSTITUTE OF TECHNOLOGY nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.tssg.solas.bb.cormel2neo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tssg.solas.bb.cormel2neo.domain.CormelANode;
import org.tssg.solas.bb.cormel2neo.domain.CormelENode;
import org.tssg.solas.bb.cormel2neo.domain.CormelHNode;
import org.tssg.solas.bb.cormel2neo.domain.CormelUNode;
import org.tssg.solas.bb.cormel2neo.domain.shared.CormelNodeShared;
import org.tssg.solas.bb.cormel2neo.domain.shared.CormelRelationshipShared;
import org.tssg.solas.bb.cormel2neo.domain.shared.CormelTNode;
import org.tssg.solas.bb.cormel2neo.domain.shared.Field;
import org.tssg.solas.bb.cormel2neo.domain.shared.ICormelNode;
import org.tssg.solas.bb.cormel2neo.domain.shared.NodeOpSummary;
import org.tssg.solas.bb.cormel2neo.domain.shared.NodeType;
import org.tssg.solas.bb.cormel2neo.domain.shared.RelOpSummary;
import org.tssg.solas.bb.cormel2neo.domain.shared.RelType;
import org.tssg.solas.bb.fum2neo.domain.AppEventSummary;

import com.amadeus.cormel.types.ASegment;
import com.amadeus.cormel.types.ESegment;
import com.amadeus.cormel.types.HSegment;
import com.amadeus.cormel.types.TSegment;
import com.amadeus.cormel.types.USegment;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CormelAvroToNeo {

  private static Logger log = LoggerFactory.getLogger(CormelAvroToNeo.class);
  private static final String defaultConnectionString = "bolt://localhost:7687";
  private static final File default_CormelAvroFile = null;
  private static final File default_AppEventFilterFile = null;
  private static final int default_uRecordsToProcess = Integer.MAX_VALUE;
  private static final int default_reportFrequency = 100;
  private static final int default_batchSize = 2000;
  private static final boolean default_excludeAE = true;

  private int maxSurrogateId = 0;

  @Parameter(names = {"-i", "--input"}, description = "name of Cormel file to process", required=true)
  private File cormelAvroFile = default_CormelAvroFile;
  
  @Parameter(names = {"-n", "--uRecordsToProcess"}, description = "number of U records to process", required=true)
  private int uRecordsToProcess = default_uRecordsToProcess;
  
  @Parameter(names = {"-p", "--printFrequency"}, description = "database operation report frequency")
  private int reportFrequency = default_reportFrequency;

  @Parameter(names = {"-b", "--batchSize"}, description = "insert transaction size")
  private int batchSize = default_batchSize;
  
  @Parameter(names = {"-s", "--sourceCormelFile"}, description = "string identifying the source of the data")
  private String sourceCormelFile = "dummy";
  
  @Parameter(names = {"-e", "--excludeAE"}, description = "do NOT upload ASegments and ESegments from the input file")
  private boolean excludeAE = default_excludeAE;
  
  @Parameter(names = {"-a", "--appEventFilterFile"}, description = "Avro file used to store AppEvent summaries")
  private File appEventFilterFile = default_AppEventFilterFile;
  
  @Parameter(names = {"-f", "--filterFirst"}, description = "upload only those transaction trees that have matching AppEvent records summarised in the supporting Avro file.")
  private boolean filterFirst;
  
  @Parameter(names = {"-h", "--help"}, help = true)
  private boolean help;
  
  public CormelAvroToNeo() {
  }

  public File getCormelAvroFile() {
    return cormelAvroFile;
  }

  public String toJson(String kind, Map<String, Object> elementMap) {
    // Unfortunately, Jackson automatically base64 encodes strings like the DcXId value when converting them to JSON. For convenience, just use inbuilt Java implicit toString() on Maps.
    ObjectMapper jsonMapper = new ObjectMapper();
    String json = null;
    try {
      json = jsonMapper.writeValueAsString(elementMap);
    } catch (IOException e) {
      log.error("IOException mapping {} to JSON : {}", kind, e.getMessage());
      e.printStackTrace();
    }
    return json;
  }

  private NodeOpSummary parse(Transaction tx, ASegment aSeg) {
    NodeOpSummary ns = new NodeOpSummary();
    Map<String, Object> aMap = ns.getProperties();
    if (aSeg != null) {
      // See https://stackoverflow.com/a/42914980
      ICormelNode aNode = new CormelANode(tx);
      aMap.put(Field.Node_CormelFile, sourceCormelFile);
      aMap.put(Field.A_ActionCode, String.valueOf(Optional.ofNullable(aSeg.getActionCode()).orElse("-")));
      ns = aNode.addNode(aMap);
//      System.out.println(toJson("aMap", aMap));
//      System.out.println("aMap = "+aMap);
    } else {
      //log.info("aMap is empty");
    }
    return ns;
  }

  private NodeOpSummary parse(Transaction tx, ESegment eSeg) {
    NodeOpSummary ns = new NodeOpSummary();
    Map<String, Object> eMap = ns.getProperties();
    if (eSeg != null) {
      ICormelNode eNode = new CormelENode(tx);
      eMap.put(Field.Node_CormelFile, sourceCormelFile);
      eMap.put(Field.E_Temporality, Integer.valueOf(Optional.ofNullable(eSeg.getTemporality()).orElse(0)));
      ns = eNode.addNode(eMap);
//      System.out.println(toJson("eMap", eMap));
//      System.out.println(eMap);
    } else {
      //log.info("eMap is empty");
    }
    return ns;
  }

  private NodeOpSummary parse(Transaction tx, HSegment hSeg) {
    NodeOpSummary ns = new NodeOpSummary();
    Map<String, Object> hMap = ns.getProperties();
    if (hSeg != null) {
      ICormelNode hNode = new CormelHNode(tx);
      hMap.put(Field.Node_CormelFile, sourceCormelFile);
      hMap.put(Field.H_ContraHopTimeSec, Float.valueOf(Optional.ofNullable(hSeg.getContraHopTimeSec()).orElse(Float.valueOf(0))));
      hMap.put(Field.H_DuName, String.valueOf(Optional.ofNullable(hSeg.getDuName()).orElse("")));
      hMap.put(Field.H_HopTimeSec, Float.valueOf(Optional.ofNullable(hSeg.getHopTimeSec()).orElse(Float.valueOf(0))));
      hMap.put(Field.H_InboundQuerySizeB, Integer.valueOf(Optional.ofNullable(hSeg.getInboundQuerySizeB()).orElse(0)));
      hMap.put(Field.H_InboundQZippedSizeB, Integer.valueOf(Optional.ofNullable(hSeg.getInboundQZippedSizeB()).orElse(0)));
      hMap.put(Field.H_InboundReplySizeB, Integer.valueOf(Optional.ofNullable(hSeg.getInboundReplySizeB()).orElse(0)));
      hMap.put(Field.H_InboundRZippedSizeB, Integer.valueOf(Optional.ofNullable(hSeg.getInboundRZippedSizeB()).orElse(0)));
      hMap.put(Field.H_InFlightTimeSec, Float.valueOf(Optional.ofNullable(hSeg.getInFlightTimeSec()).orElse(Float.valueOf(0))));
      hMap.put(Field.H_OutboundQuerySizeB, Integer.valueOf(Optional.ofNullable(hSeg.getOutboundQuerySizeB()).orElse(0)));
      hMap.put(Field.H_OutboundQZippedSizeB, Integer.valueOf(Optional.ofNullable(hSeg.getOutboundQZippedSizeB()).orElse(0)));
      hMap.put(Field.H_OutboundReplySizeB, Integer.valueOf(Optional.ofNullable(hSeg.getOutboundReplySizeB()).orElse(0)));
      hMap.put(Field.H_OutboundRZippedSizeB, Integer.valueOf(Optional.ofNullable(hSeg.getOutboundRZippedSizeB()).orElse(0)));
      hMap.put(Field.H_RespTimeSec, Float.valueOf(Optional.ofNullable(hSeg.getRespTimeSec()).orElse(Float.valueOf(0))));
      ns = hNode.addNode(hMap);
//      System.out.println(toJson("hMap", hMap));
//      System.out.println(hMap);
    } else {
      //log.info("hMap is empty");
    }
    return ns;
  }

  private List<NodeOpSummary> parse(Transaction tx, USegment uSeg, TSegment tSeg, Map<String, Object> treeKeyParams, FilterBean filterData) {
    NodeOpSummary ns = new NodeOpSummary();
    Map<String, Object> tMap = ns.getProperties();
    List<NodeOpSummary> nodeSummaries = new ArrayList<>();
    List<RelOpSummary> relSummaries = new ArrayList<>();
    if (tSeg != null) {
      ICormelNode tNode = new CormelTNode(tx);
      String dcxId = String.valueOf(Optional.ofNullable(uSeg.getDcxId()).orElse(""));
      tMap.put(Field.Node_CormelFile, sourceCormelFile);
      tMap.put(Field.T_CausingId, String.valueOf(Optional.ofNullable(tSeg.getCausingId()).orElse("")));
      tMap.put(Field.T_DcxId, dcxId);
      tMap.put(Field.T_Destination, String.valueOf(Optional.ofNullable(tSeg.getDestination()).orElse("")));
      tMap.put(Field.T_Flags, String.valueOf(Optional.ofNullable(tSeg.getFlags()).orElse("")));
      tMap.put(Field.T_Initiator, String.valueOf(Optional.ofNullable(tSeg.getInitiator()).orElse("")));
      tMap.put(Field.T_QueryIPAddressPort, String.valueOf(Optional.ofNullable(tSeg.getQueryIPAddressPort()).orElse("")));
      tMap.put(Field.T_QueryType, String.valueOf(Optional.ofNullable(tSeg.getQueryType()).orElse("")));
      tMap.put(Field.T_ReplyErrorCode, Integer.valueOf(Optional.ofNullable(tSeg.getReplyErrorCode()).orElse(0)));
      tMap.put(Field.T_ReplyType, String.valueOf(Optional.ofNullable(tSeg.getReplyType()).orElse("")));
      tMap.put(Field.T_SAPName, String.valueOf(Optional.ofNullable(tSeg.getSAPName()).orElse("")));
      tMap.put(Field.T_TimeStamp, Long.valueOf(Optional.ofNullable(tSeg.getTimeStamp()).orElse(0L)));
      tMap.put(Field.T_TrxNb, String.valueOf(Optional.ofNullable(tSeg.getTrxNb()).orElse("")));
      if (filterFirst) {
        Map<String,String> transactionFilter = filterData.getTransactionFilterMap();
        String status = lookup(transactionFilter, dcxId, tSeg);
        if (null != status) {
          tMap.put(Field.AppEvent_T_Status, status);
        }
        String parts[] = appEventFilterFile.getName().split("\\.");
        String appEventFilterFilename = parts[0];
        tMap.put(Field.Node_AppEventFile, appEventFilterFilename);
      }
      
      nodeSummaries.add(tNode.addNode(tMap));
      NodeType fromNodeType = NodeType.Tsegment;
      String fromKey = CormelTNode.deriveKey(tMap);
      NodeType toNodeType;
      String toKey;
      if (!excludeAE) {
        NodeOpSummary ans = parse(tx, tSeg.getASegment());
        Map<String, Object> aprops = ans.getProperties();
        if (!aprops.isEmpty()) {
          nodeSummaries.add(ans);
          // System.out.println("Create a T -> A relationship here");
          toNodeType = ans.getNodeType();
          toKey = ans.getKey();
          relSummaries.add(CormelRelationshipShared.createRel(tx, fromNodeType, fromKey, toNodeType, toKey,
              RelType.HAS_A, treeKeyParams));
        }
        NodeOpSummary ens = parse(tx, tSeg.getESegment());
        Map<String, Object> eprops = ens.getProperties();
        if (!eprops.isEmpty()) {
          nodeSummaries.add(ens);
          // System.out.println("Create a T -> E relationship here");
          toNodeType = ens.getNodeType();
          toKey = ens.getKey();
          relSummaries.add(CormelRelationshipShared.createRel(tx, fromNodeType, fromKey, toNodeType, toKey,
              RelType.HAS_E, treeKeyParams));
        }
      }
      for (HSegment hSeg : tSeg.getHSegments()) {
        NodeOpSummary hns = parse(tx, hSeg);
        Map<String, Object> hprops = hns.getProperties();
        if (!hprops.isEmpty()) {
          nodeSummaries.add(hns);
          //System.out.println("Create a T -> H relationship here");
          toNodeType = hns.getNodeType();
          toKey = hns.getKey();
          relSummaries.add(CormelRelationshipShared.createRel(tx, fromNodeType, fromKey, toNodeType, toKey, RelType.HAS_H, treeKeyParams));
        }
      }
      for (TSegment tSegChildren : tSeg.getTSegments()) {
        NodeOpSummary tns = parse(tx, uSeg, tSegChildren, treeKeyParams, filterData).get(0);
        Map<String, Object> tprops = tns.getProperties();
        if (!tprops.isEmpty()) {
          nodeSummaries.add(tns);
          //System.out.println("Create a T -> T (parent,child) relationship here");
          toNodeType = tns.getNodeType();
          toKey = tns.getKey();
          relSummaries.add(CormelRelationshipShared.createRel(tx, fromNodeType, fromKey, toNodeType, toKey, RelType.HAS_T, treeKeyParams));
        }
      }
    } else {
      progressReport(System.err,"tMap is empty");
    }
    return nodeSummaries;
  }

  private NodeOpSummary parse(Transaction tx, USegment uSeg, FilterBean filterData) {
    NodeOpSummary ns = new NodeOpSummary();
    Map<String, Object> uMap = ns.getProperties();
    List<NodeOpSummary> nodeSummaries = new ArrayList<>();
    List<RelOpSummary> relSummaries = new ArrayList<>();
    if (uSeg != null) {
      CormelUNode uNode = new CormelUNode(tx);
      uMap.put(Field.Node_CormelFile, sourceCormelFile);
      uMap.put(Field.U_ATID, String.valueOf(Optional.ofNullable(uSeg.getATID()).orElse("")));
      uMap.put(Field.U_BEType, String.valueOf(Optional.ofNullable(uSeg.getBEType()).orElse("")));
      uMap.put(Field.U_DCD, String.valueOf(Optional.ofNullable(uSeg.getDCD()).orElse("")));
      uMap.put(Field.U_DcxId, String.valueOf(Optional.ofNullable(uSeg.getDcxId()).orElse("")));
      uMap.put(Field.U_Destination, String.valueOf(Optional.ofNullable(uSeg.getDestination()).orElse("")));
      uMap.put(Field.U_Initiator, String.valueOf(Optional.ofNullable(uSeg.getInitiator()).orElse("")));
      uMap.put(Field.U_OfficeID, String.valueOf(Optional.ofNullable(uSeg.getOfficeID()).orElse("")));
      uMap.put(Field.U_ParentDcxId, String.valueOf(Optional.ofNullable(uSeg.getParentDcxId()).orElse("")));
      uMap.put(Field.U_PnrId, String.valueOf(Optional.ofNullable(uSeg.getPnrId()).orElse("")));
      uMap.put(Field.U_QueryIPAddressPort, String.valueOf(Optional.ofNullable(uSeg.getQueryIPAddressPort()).orElse("")));
      uMap.put(Field.U_SAPName, String.valueOf(Optional.ofNullable(uSeg.getSAPName()).orElse("")));
      uMap.put(Field.U_ServiceType, String.valueOf(Optional.ofNullable(uSeg.getServiceType()).orElse("")));
      uMap.put(Field.U_TimeStamp, Long.valueOf(Optional.ofNullable(uSeg.getTimeStamp()).orElse(0L)));
      uMap.put(Field.U_TreeId, Integer.valueOf(Optional.ofNullable(uSeg.getTreeId()).orElse(0)));
      if (filterFirst) {
        String parts[] = appEventFilterFile.getName().split("\\.");
        String appEventFilterFilename = parts[0];
        uMap.put(Field.Node_AppEventFile, appEventFilterFilename);
      }
      //System.out.println(uMap);
      int foundId = CormelRelationshipShared.addOrFindTreeKey(String.valueOf(uMap.get(Field.U_DcxId)),
          String.valueOf(uMap.get(Field.U_TreeId)), maxSurrogateId);
      if (foundId > maxSurrogateId) {
        maxSurrogateId = foundId;
      }
      NodeOpSummary uns = uNode.addNode(uMap);
      nodeSummaries.add(uns);
      NodeType fromNodeType = NodeType.Usegment;
      String fromKey = CormelUNode.deriveKey(uMap);
      Map<String, Object> treeKeyParams = CormelUNode.selectTreeKey(uMap, foundId);
      NodeType toNodeType;
      String toKey;
      NodeOpSummary tns = parse(tx, uSeg, uSeg.getTSegment(), treeKeyParams, filterData).get(0);
      Map<String, Object> tprops = tns.getProperties();
      if (!tprops.isEmpty()) {
        nodeSummaries.add(tns);
        //System.out.println("Create a U -> T relationship here");
        toNodeType = tns.getNodeType();
        toKey = tns.getKey();
        relSummaries.add(CormelRelationshipShared.createRel(tx, fromNodeType, fromKey, toNodeType, toKey, RelType.HAS_T, treeKeyParams));
      }
      // Update uNode with the silhouette based on the Transaction nodes below it
//      String key = CormelUNode.deriveKey(uMap);
//      nodeSummaries.add(uNode.updateSilhouette(key));
      
      //System.out.println("Number of u-related nodes and relationships created is "+nnodes+" and "+nrels+" respectively");
    } else {
      //log.info("uMap is empty");
    }
    return nodeSummaries.get(0);
  }

  private void progressReport(PrintStream outputStream, String s) {
    // See https://stackoverflow.com/a/20677345
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm:ss"));
    outputStream.println(now+" "+s);
  }

  private void applyConstraintsIndexes(Session session) {
    progressReport(System.out, "About to create unique key constraints and indexes on sourceCormelFile");
    for (NodeType nodeType : NodeType.values()) {
      CormelNodeShared.applyConstraint(session, nodeType);
      CormelNodeShared.applyIndex(session, nodeType, Field.Node_CormelFile);
    }
    progressReport(System.out, "Created key indexes");
  }

  private String defineTransactionFilterKey(String dcxId, String trxNb) {
    StringBuilder sb = new StringBuilder();
    sb.append(dcxId);
    sb.append(":");
    sb.append(trxNb);
    String key = sb.toString();
    return key;
  }

  private String defineTreeFilterKey(String dcxId, Integer treeId) {
    StringBuilder sb = new StringBuilder();
    sb.append(dcxId);
    sb.append(":");
    sb.append(treeId);
    String key = sb.toString();
    return key;
  }
  
  private Map<String,Integer> updateTreeFilterMap(Map<String,Integer> treeFilterMap, String key) {
    Integer count = treeFilterMap.get(key);
    if (null == count) {
      count = 0;
    } else {
      count++;
    }
    treeFilterMap.put(key, count);
    return treeFilterMap;
  }
  
  private FilterBean readFilterData(File avroFilterFile) {
    Map<String,String> transactionFilterMap = new HashMap<>();
    Map<String,Integer> treeFilterMap = new HashMap<>();
    int cnt = 0;
    AppEventSummary aes = null;
    DatumReader<AppEventSummary> cormelDatumReader = new SpecificDatumReader<AppEventSummary>(AppEventSummary.class);
    try (DataFileReader<AppEventSummary> filterDataFileReader = new DataFileReader<AppEventSummary>(avroFilterFile, cormelDatumReader)) {
      while (filterDataFileReader.hasNext()) {
        aes = filterDataFileReader.next(aes);
        String dcxId = String.valueOf(aes.getDcxId());
        String trxNb = String.valueOf(aes.getTrxNb());
        int treeId = aes.getTreeId();
        String status = String.valueOf(aes.getStatus());
        String transactionFilterKey = defineTransactionFilterKey(dcxId, trxNb);
        transactionFilterMap.put(transactionFilterKey, status);
        String treeFilterKey = defineTreeFilterKey(dcxId, treeId);
        treeFilterMap = updateTreeFilterMap(treeFilterMap, treeFilterKey);        
        cnt++;
      }  
    } catch (IOException e) {
      progressReport(System.err, "IOException opening/writing/closing "+avroFilterFile);
      e.printStackTrace();
    }
    progressReport(System.out, "Read "+cnt+" records from "+avroFilterFile+" into the filterMap.");
    FilterBean filter = new FilterBean(transactionFilterMap, treeFilterMap);
    return filter;
  }

  private String lookup(Map<String,String> transactionFilter, String dcxId, TSegment tSeg) {
    String trxNb = tSeg.getTrxNb().toString();
    String key = defineTransactionFilterKey(dcxId, trxNb);
//    progressReport(System.out, "transactionFilterKey = "+key);
    String status = transactionFilter.get(key);
//    progressReport(System.out, "status = "+status);
    return status;  
  }

  private boolean checkTreeMatch(FilterBean filterData, USegment uSeg) {
    String dcxId = uSeg.getDcxId().toString();
    Integer treeId = uSeg.getTreeId();
    String key = defineTreeFilterKey(dcxId, treeId);
    boolean found = filterData.treeFilterContains(key);
    return found;  
  }
  
  public void readParseAndSave() {

    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    Path path = Paths.get(System.getProperty("user.home")+"/cormel2neo/output/timings/"+now+"-"+sourceCormelFile+".txt"); // Java 8 - see http://winterbe.com/posts/2015/03/25/java8-examples-string-number-math-files/
    
    // Deserialize records from disk
    int cnt = 0;
    DatumReader<USegment> cormelDatumReader = new SpecificDatumReader<USegment>(USegment.class);

    long groupElapsed = 0L;
    int txCount = 0;
    long start;
    long elapsed;

    progressReport(System.out,"** Uploading CorMel data from "+cormelAvroFile+" **");
    if (excludeAE) {
      progressReport(System.out,"** Uploading CorMel U, T and H segments only. EXCLUDING A and E segments **");
    } else {
      progressReport(System.out,"** Uploading CorMel U, T and H segments. Also INCLUDING A and E segments **");
    }
 
    FilterBean filterData = null;
    if (filterFirst) {
      progressReport(System.out,"** Uploading AppEvent data to be used to filter incoming CorMel records **");
      start = System.nanoTime();
      filterData = readFilterData(appEventFilterFile);
      int numTransactions = filterData.deriveNumTransactions();
      int numTrees = filterData.deriveNumTrees();
      elapsed = System.nanoTime() - start;
      progressReport(System.out,"** Uploaded "+numTransactions+" transaction filter records and "+numTrees+" treeFilter records in "+TimeUnit.NANOSECONDS.toMillis(elapsed)+" milliseconds.**");
    }
    
    try (DataFileReader<USegment> cormelDataFileReader = new DataFileReader<USegment>(cormelAvroFile, cormelDatumReader)){
      USegment uSeg = null;
      
      try (Driver driver = GraphDatabase.driver(defaultConnectionString)) {
        try (Session session = driver.session()) {

          applyConstraintsIndexes(session);
          
          progressReport(System.out, "Create a new transaction for this session");
          Transaction tx = session.beginTransaction();
          
          try (BufferedWriter writer = Files.newBufferedWriter(path)) { // See
                                                                        // http://winterbe.com/posts/2015/03/25/java8-examples-string-number-math-files/

            while (cormelDataFileReader.hasNext()) {
              uSeg = cormelDataFileReader.next(uSeg);

              boolean useThis = true;
              if (filterFirst) {
                useThis = checkTreeMatch(filterData, uSeg);
              }

              if (useThis) {
                start = System.nanoTime();
                parse(tx, uSeg, filterData);
                elapsed = System.nanoTime() - start;
                groupElapsed += elapsed;
                if (cnt % reportFrequency == (reportFrequency - 1)) {
                  progressReport(System.out, "Record " + cnt + ": Parsed previous " + reportFrequency + " records in "
                      + TimeUnit.NANOSECONDS.toMillis(groupElapsed) + " milliseconds.");
                  writer
                      .write(String.valueOf(txCount) + "," + String.valueOf(cnt) + "," + String.valueOf(groupElapsed));
                  writer.newLine();
                  groupElapsed = 0;
                }

                if (cnt % batchSize == (batchSize - 1)) {
                  progressReport(System.out, batchSize
                      + " records added, about to commit and close the current transaction and start a new one");
                  start = System.nanoTime();
                  tx.success(); // Commit before closing!!
                  tx.close();
                  tx = session.beginTransaction();
                  elapsed = System.nanoTime() - start;
                  txCount++;
                  progressReport(System.out,
                      "Committed and closed transaction " + (txCount - 1) + " and started transaction " + txCount
                          + " in " + TimeUnit.NANOSECONDS.toMillis(elapsed) + " milliseconds.");
                }

                cnt++;
                if (cnt >= uRecordsToProcess) {
                  progressReport(System.out, "** Uploaded the last of " + uRecordsToProcess + " transaction trees. **");
                  break;
                }
              }
              
            }
            
            tx.success(); // Commit all transaction tree uploads before closing!!
            tx.close();
            tx = session.beginTransaction();

            progressReport(System.out,"** Update relationships of transaction trees with relTreeList. **");
            int relBatchSize = batchSize*20;

            cnt = 0;
            Map<Long, List<Long>> relSummary = CormelRelationshipShared.deriveRelSummary(tx);
            for (Long relId : relSummary.keySet()) {

              List<Long> relIdList = relSummary.get(relId);
              cnt += CormelRelationshipShared.addRelTreeListToRelationship(tx, relId, relIdList);

              if (cnt % relBatchSize == (relBatchSize - 1)) {
                progressReport(System.out, relBatchSize
                    + " relationships updated, about to commit and close the current transaction and start a new one");
                start = System.nanoTime();
                tx.success(); // Commit before closing!!
                tx.close();
                tx = session.beginTransaction();
                elapsed = System.nanoTime() - start;
                txCount++;
                progressReport(System.out, "Committed and closed transaction "+(txCount-1)+" and started transaction "+txCount+" in " + TimeUnit.NANOSECONDS.toMillis(elapsed)
                    + " milliseconds.");
              }
              
            }
            relSummary = null; // not needed any more, so recover its space
          }

          tx.success(); // Commit before closing!!
          tx.close();
          progressReport(System.out, "Committed and closed final transaction "+txCount+" in this session.");
        }
      }
    } catch (IOException e) {
      log.error("IOException with dataFileReader: {} on {}", e.getMessage(), cormelAvroFile);
      e.printStackTrace();
    }
  }
  
  public static void main(String ... argv) {

    CormelAvroToNeo catn = new CormelAvroToNeo();
    JCommander.newBuilder()
    .addObject(catn)
    .build()
    .parse(argv);
    
    catn.readParseAndSave();
    
  }
}
