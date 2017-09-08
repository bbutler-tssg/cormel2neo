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

/**
 * @author bbutler
 *
 */
public class Field {
  public static final String A_ActionCode = "ActionCode";
  
  public static final String E_Temporality = "Temporality";
  
  public static final String H_DuName = "DuName";
  public static final String H_HopTimeSec = "HopTimeSec";
  public static final String H_RespTimeSec = "RespTimeSec";
  public static final String H_ContraHopTimeSec = "ContraHopTimeSec";
  public static final String H_InFlightTimeSec = "InFlightTimeSec";
  public static final String H_InboundQuerySizeB = "InboundQuerySizeB";
  public static final String H_OutboundQuerySizeB = "OutboundQuerySizeB";
  public static final String H_InboundReplySizeB = "InboundReplySizeB";
  public static final String H_OutboundReplySizeB = "OutboundReplySizeB";
  public static final String H_InboundQZippedSizeB = "InboundQZippedSizeB";
  public static final String H_OutboundQZippedSizeB = "OutboundQZippedSizeB";
  public static final String H_InboundRZippedSizeB = "InboundRZippedSizeB";
  public static final String H_OutboundRZippedSizeB = "OutboundRZippedSizeB";

  public static final String T_DcxId = "DcxId";
  public static final String T_TrxNb = "TrxNb";
  public static final String T_CausingId = "CausingId";
  public static final String T_TimeStamp = "TimeStamp";
  public static final String T_Initiator = "Initiator";
  public static final String T_Destination = "Destination";
  public static final String T_SAPName = "SAPName";
  public static final String T_QueryType = "QueryType";
  public static final String T_QueryIPAddressPort = "QueryIPAddressPort";
  public static final String T_ReplyType = "ReplyType";
  public static final String T_ReplyErrorCode = "ReplyErrorCode";
  public static final String T_Flags = "Flags";
  
  // The following come from AppEvent updates - they are not in CorMel TSegments!
  public static final String AppEvent_T_Prefix = "Prefix";
  public static final String AppEvent_T_Status = "TransactionStatus";
  public static final String AppEvent_T_Code = "TransactionCode";
  public static final String AppEvent_T_SubCode = "TransactionSubCode";

  public static final String U_DcxId = "DcxId";
  public static final String U_TreeId = "TreeId";
  public static final String U_TimeStamp = "TimeStamp";
  public static final String U_Initiator = "Initiator";
  public static final String U_Destination = "Destination";
  public static final String U_SAPName = "SAPName";
  public static final String U_OfficeID = "OfficeID";
  public static final String U_ATID = "ATID";
  public static final String U_ServiceType = "ServiceType";
  public static final String U_BEType = "BEType";
  public static final String U_DCD = "DCD";
  public static final String U_PnrId = "PnrId";
  public static final String U_QueryIPAddressPort = "QueryIPAddressPort";
  public static final String U_ParentDcxId = "ParentDcxId";
  
  public static final String Rel_DcxId = "Rel_DcxId";
  public static final String Rel_TreeId = "Rel_TreeId";  
  public static final String Rel_TreeKey = "Rel_TreeKey";  
  public static final String Rel_TreeList = "Rel_TreeList";  
  
  public static final String AppEvent_M_Code = "Code";
  public static final String AppEvent_M_Description = "Description";

  // The following field is used to 'label' all nodes with the source of the data
  public static final String Node_CormelFile = "CormelFile";

  // The following field is used to 'label' all *matched* nodes with the source of the matching data
  public static final String Node_AppEventFile = "AppEventFile";

  // The following field is used to 'label' all *matched* U nodes with their silhouette string
  public static final String Silhouette = "Silhouette";
  
}
