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

public class RelOpSummary {

  private RelType relType;

  private NodeType fromNodeType;
  
  private NodeType toNodeType;
  
  private EntityOperation relOp;
  
  private int opCount;
  
  public RelOpSummary() {
  }
  
  public RelOpSummary(RelType relType, NodeType fromNodeType, NodeType toNodeType, EntityOperation relOp, int opCount) {
    this.relType = relType;
    this.fromNodeType = fromNodeType;
    this.toNodeType = toNodeType;
    this.relOp = relOp;
    this.opCount = opCount;
  }

  /**
   * @return the relType
   */
  public RelType getRelType() {
    return relType;
  }

  /**
   * @param relType the relType to set
   */
  public void setRelType(RelType relType) {
    this.relType = relType;
  }

  /**
   * @return the fromNodeType
   */
  public NodeType getFromNodeType() {
    return fromNodeType;
  }

  /**
   * @param fromNodeType the fromNodeType to set
   */
  public void setFromNodeType(NodeType fromNodeType) {
    this.fromNodeType = fromNodeType;
  }

  /**
   * @return the toNodeType
   */
  public NodeType getToNodeType() {
    return toNodeType;
  }

  /**
   * @param toNodeType the toNodeType to set
   */
  public void setToNodeType(NodeType toNodeType) {
    this.toNodeType = toNodeType;
  }

  /**
   * @return the relOp
   */
  public EntityOperation getRelOp() {
    return relOp;
  }

  /**
   * @param relOp the relOp to set
   */
  public void setRelOp(EntityOperation relOp) {
    this.relOp = relOp;
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
    return "Rel type "+relType+" subject to RelOp "+relOp+", rows affected: "+opCount;
  }
}
