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
package org.tssg.solas.bb.cormel2neo;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tssg.solas.bb.cormel2neo.CormelAvroToNeo;
import org.tssg.solas.bb.cormel2neo.learn.GraphManager;

/**
 * @author bbutler
 *
 */
public class TestGraphManager {

  private Logger log = LoggerFactory.getLogger(CormelAvroToNeo.class);
  private static GraphManager gm;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setupBeforeClass, about to get graphManager instance");
    gm = new GraphManager();
    System.out.println("In setupBeforeClass, after getting graphManager instance");
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tearDownAfterClass, about to get graphDb instance");
    GraphDatabaseService graphDb = gm.getGraphDb();
    graphDb.shutdown();
    System.out.println("In tearDownAfterClass, have shutdown graphDb instance");
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    GraphDatabaseService graphDb = gm.getGraphDb();
    log.debug("In setup, picked up a graphDb instance");
    Node firstNode;
    Node secondNode;
    Relationship relationship;

    try (Transaction tx = graphDb.beginTx()) {
      log.debug("In Setup: about to create firstNode");
      firstNode = graphDb.createNode();
      firstNode.setProperty("message", "Hello, ");
      log.debug("In Setup: about to create secondNode");
      secondNode = graphDb.createNode();
      secondNode.setProperty("message", "World!");

      log.debug("In Setup: about to create relationship");
      relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
      relationship.setProperty("message", "brave Neo4j ");
      tx.success();
      log.debug("In Setup: successfully created two nodes and a relationship between them");
    }
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    GraphDatabaseService graphDb = gm.getGraphDb();
    log.debug("In teardown, picked up a graphDb instance");
    try (Transaction tx = graphDb.beginTx()) {
      log.debug("In teardown: about to delete relationship");
      for (Relationship rel : graphDb.getAllRelationships()) {
        rel.delete();
      }
      log.debug("In teardown: deleted relationship, about to delete 2 nodes");
      for (Node node : graphDb.getAllNodes()) {
        node.delete();
      }
      tx.success();
      log.debug("In teardown: deleted 2 nodes");
    }
  }

  @Test
  public void testRelationshipExists() {
    GraphDatabaseService graphDb = gm.getGraphDb();
    log.debug("testRelationshipExists: Picked up a graphDb instance");
    boolean isNotEmpty = false;
    try (Transaction tx = graphDb.beginTx()) {
      isNotEmpty = graphDb.getAllRelationships().iterator().hasNext();
      tx.success();
      log.debug("testRelationshipExists: Successfully searched for all relationships");
    }
    assert (isNotEmpty);
  }

  @Test
  public void testExactlyOneRelationship() {
    GraphDatabaseService graphDb = gm.getGraphDb();
    log.debug("testExactlyOneRelationship: Picked up a graphDb instance");
    int cnt = 0;
    try (Transaction tx = graphDb.beginTx()) {
      while (graphDb.getAllRelationships().iterator().hasNext()) {
        cnt++;
      }
      tx.success();
      log.debug("testExactlyOneRelationship: Successfully searched for all relationships");
    }
    assertEquals(cnt, 1);
  }

//  @Test
//  public void testExactlyTwoNodes() {
//    GraphDatabaseService graphDb = gm.getGraphDb();
//    int cnt = 0;
//    try (Transaction tx = graphDb.beginTx()) {
//      while (graphDb.getAllNodes().iterator().hasNext()) {
//        cnt++;
//      }
//      tx.success();
//    }
//    assertEquals(cnt, 2);
//  }
//
//  @Test
//  public void testHelloNode() {
//    GraphDatabaseService graphDb = gm.getGraphDb();
//    boolean foundHelloNode = false;
//    int helloCnt = 0;
//    try (Transaction tx = graphDb.beginTx()) {
//      while (graphDb.findNodes(null, "message", "Hello, ").hasNext()) {
//        foundHelloNode = true;
//        helloCnt++;
//      }
//      tx.success();
//    }
//    assert (foundHelloNode && (helloCnt <= 1));
//  }

  private enum RelTypes implements RelationshipType {
    KNOWS
  }

}
