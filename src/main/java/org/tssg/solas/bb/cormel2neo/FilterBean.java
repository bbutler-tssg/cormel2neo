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

import java.io.Serializable;
import java.util.Map;

/**
 * @author bbutler
 *
 */
public class FilterBean  implements Serializable, Comparable<FilterBean> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Map<String,String> transactionFilterMap;
  private Map<String,Integer> treeFilterMap;
  
 
  public FilterBean(Map<String, String> transactionFilterMap, Map<String, Integer> treeFilterMap) {
    super();
    this.transactionFilterMap = transactionFilterMap;
    this.treeFilterMap = treeFilterMap;
  }

  /**
   * @return the transactionFilterMap
   */
  public Map<String, String> getTransactionFilterMap() {
    return transactionFilterMap;
  }

  /**
   * @return the treeFilterMap
   */
  public Map<String, Integer> getTreeFilterMap() {
    return treeFilterMap;
  }

  public int deriveNumTransactions() {
    int num = transactionFilterMap.size();
    return num;
  }

  public int deriveNumTrees() {
    int num = treeFilterMap.size();
    return num;
  }
  
  public boolean transactionFilterContains(String key) {
    boolean found = transactionFilterMap.keySet().contains(key);
    return found;
  }
  
  public boolean treeFilterContains(String key) {
    boolean found = treeFilterMap.keySet().contains(key);
    return found;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((transactionFilterMap == null) ? 0 : transactionFilterMap.hashCode());
    result = prime * result + ((treeFilterMap == null) ? 0 : treeFilterMap.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FilterBean other = (FilterBean) obj;
    if (transactionFilterMap == null) {
      if (other.transactionFilterMap != null)
        return false;
    } else if (!transactionFilterMap.equals(other.transactionFilterMap))
      return false;
    if (treeFilterMap == null) {
      if (other.treeFilterMap != null)
        return false;
    } else if (!treeFilterMap.equals(other.treeFilterMap))
      return false;
    return true;
  }

  @Override
  public int compareTo(FilterBean arg0) {
    return this.toString().compareTo(arg0.toString());
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
  }

}
