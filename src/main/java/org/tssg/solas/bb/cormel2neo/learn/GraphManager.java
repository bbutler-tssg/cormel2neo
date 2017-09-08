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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphManager {

  // private final String defaultGraphDir = System.getProperty("user.home") +
  // "/neo4j/data";
  private static final String defaultGraphDir = System.getProperty("user.dir") + "/target/neo4j/data";
  private GraphDatabaseService graphDb;

  public GraphManager(String graphDir) {
    // graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new
    // File(graphDir))
    // .setConfig(ShellSettings, "true")
    // .setConfig(ShellSettings.remote_shell_port, "5555")
    // .newGraphDatabase();
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(graphDir));
    registerShutdownHook(graphDb);
  }

  public GraphManager() {
    this(defaultGraphDir);
  }

  public GraphDatabaseService getGraphDb() {
    return graphDb;
  }

  private static void registerShutdownHook(final GraphDatabaseService graphDb) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        graphDb.shutdown();
      }
    });
  }

  
  private static enum RelTypes implements RelationshipType {
    KNOWS
  }

  public static void main(String[] args) {
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    GraphManager gm = new GraphManager();
    GraphDatabaseService graphDb = gm.getGraphDb();

    try (Transaction tx = graphDb.beginTx()) {
      firstNode = graphDb.createNode();
      firstNode.setProperty("message", "Hello, ");
      secondNode = graphDb.createNode();
      secondNode.setProperty("message", "World!");

      relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
      relationship.setProperty("message", "brave Neo4j ");

      System.out.print(firstNode.getProperty("message"));
      System.out.print(relationship.getProperty("message"));
      System.out.println(secondNode.getProperty("message"));

    // let's remove the data
      firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
      firstNode.delete();
      secondNode.delete();
      tx.success();
    }

    graphDb.shutdown();

  }

}