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

import java.util.HashMap;
import java.util.Map;

public class NodeOpSummary {

  private NodeType nodeType;
  
  private String key;
  
  private Long id;
  
  private Map<String,Object> properties;
  
  private EntityOperation nodeOp;
  
  private int opCount;
  
  public NodeOpSummary() {
    properties = new HashMap<>();
  }
  
  public NodeOpSummary(NodeType nodeType, Map<String,Object> properties, String key, Long id, EntityOperation nodeOp, int opCount) {
    this.nodeType = nodeType;
    this.properties = properties;
    this.key = key;
    this.id = id;
    this.nodeOp = nodeOp;
    this.opCount = opCount;
  }

  /**
   * @return the nodeType
   */
  public NodeType getNodeType() {
    return nodeType;
  }

  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the properties
   */
  public Map<String, Object> getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  /**
   * @return the nodeOp
   */
  public EntityOperation getNodeOp() {
    return nodeOp;
  }

  /**
   * @param nodeOp the nodeOp to set
   */
  public void setNodeOp(EntityOperation nodeOp) {
    this.nodeOp = nodeOp;
  }

  /**
   * @return the opCount
   */
  public int getOpCount() {
    return opCount;
  }

  /**
   * @param opCount the opCount to set
   */
  public void setOpCount(int opCount) {
    this.opCount = opCount;
  }
  
  @Override
  public String toString() {
    return "Node type "+nodeType+" with properties "+properties+" and id "+id+" subject to NodeOp "+nodeOp+", rows affected: "+opCount;
  }
}
