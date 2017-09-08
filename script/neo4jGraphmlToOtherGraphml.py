#!/usr/bin/env python
# Copyright (c) 2017, Bernard Butler (Waterford Institute of Technology, Ireland), Project: SOLAS placement in Amadeus SA, where SOLAS (Project ID: 612480) is funded by the European Commision FP7 MC-IAPP-Industry-Academia Partnerships and Pathways scheme.
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 
#  -  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
#  -  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
#  -  Neither the name of WATERFORD INSTITUTE OF TECHNOLOGY nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
from lxml import etree
import argparse
import os
import sys
import logging
from io import StringIO, BytesIO
import graphmlSupport # local
import writeGephiGraphml # local
import writeYedGraphml # local

def keysForUsegment():
    cormelFieldDefs = {}
    cormelFieldDefs["U_CormelFile"] = {"attr.type":"string", "for":"node", "attr.name":"CormelFile", "id":"U_CormelFile"}
    cormelFieldDefs["U_AppEventFile"] = {"attr.type":"string", "for":"node", "attr.name":"AppEventFile", "id":"U_AppEventFile"}
    cormelFieldDefs["U_DcxId"] = {"attr.type":"string", "for":"node", "attr.name":"U_DcxId", "id":"U_DcxId"}
    cormelFieldDefs["U_TreeId"] = {"attr.type":"string", "for":"node", "attr.name":"U_TreeId", "id":"U_TreeId"}
    cormelFieldDefs["U_TimeStamp"] = {"attr.type":"string", "for":"node", "attr.name":"U_TimeStamp", "id":"U_TimeStamp"}
    cormelFieldDefs["U_Initiator"] = {"attr.type":"string", "for":"node", "attr.name":"U_Initiator", "id":"U_Initiator"}
    cormelFieldDefs["U_Destination"] = {"attr.type":"string", "for":"node", "attr.name":"U_Destination", "id":"U_Destination"}
    cormelFieldDefs["U_SAPName"] = {"attr.type":"string", "for":"node", "attr.name":"U_SAPName", "id":"U_SAPName"}
    cormelFieldDefs["U_OfficeID"] = {"attr.type":"string", "for":"node", "attr.name":"U_OfficeID", "id":"U_OfficeID"}
    cormelFieldDefs["U_ATID"] = {"attr.type":"string", "for":"node", "attr.name":"U_ATID", "id":"U_ATID"}
    cormelFieldDefs["U_ServiceType"] = {"attr.type":"string", "for":"node", "attr.name":"U_ServiceType", "id":"U_ServiceType"}
    cormelFieldDefs["U_BEType"] = {"attr.type":"string", "for":"node", "attr.name":"U_BEType", "id":"U_BEType"}
    cormelFieldDefs["U_DCD"] = {"attr.type":"string", "for":"node", "attr.name":"U_DCD", "id":"U_DCD"}
    cormelFieldDefs["U_PnrId"] = {"attr.type":"string", "for":"node", "attr.name":"U_PnrId", "id":"U_PnrId"}
    cormelFieldDefs["U_QueryIPAddressPort"] = {"attr.type":"string", "for":"node", "attr.name":"U_QueryIPAddressPort", "id":"U_QueryIPAddressPort"}
    cormelFieldDefs["U_ParentDcxId"] = {"attr.type":"string", "for":"node", "attr.name":"U_ParentDcxId", "id":"U_ParentDcxId"}
    return cormelFieldDefs

def keysForTsegment():
    cormelFieldDefs = {}
    cormelFieldDefs["T_CormelFile"] = {"attr.type":"string", "for":"node", "attr.name":"CormelFile", "id":"T_CormelFile"}
    cormelFieldDefs["T_AppEventFile"] = {"attr.type":"string", "for":"node", "attr.name":"AppEventFile", "id":"T_AppEventFile"}
    cormelFieldDefs["T_DcxId"] = {"attr.type":"string", "for":"node", "attr.name":"T_DcxId", "id":"T_DcxId"}
    cormelFieldDefs["T_TrxNb"] = {"attr.type":"string", "for":"node", "attr.name":"T_TrxNb", "id":"T_TrxNb"}
    cormelFieldDefs["T_CausingId"] = {"attr.type":"string", "for":"node", "attr.name":"T_CausingId", "id":"T_CausingId"}
    cormelFieldDefs["T_TimeStamp"] = {"attr.type":"string", "for":"node", "attr.name":"T_TimeStamp", "id":"T_TimeStamp"}
    cormelFieldDefs["T_Initiator"] = {"attr.type":"string", "for":"node", "attr.name":"T_Initiator", "id":"T_Initiator"}
    cormelFieldDefs["T_Destination"] = {"attr.type":"string", "for":"node", "attr.name":"T_Destination", "id":"T_Destination"}
    cormelFieldDefs["T_SAPName"] = {"attr.type":"string", "for":"node", "attr.name":"T_SAPName", "id":"T_SAPName"}
    cormelFieldDefs["T_QueryType"] = {"attr.type":"string", "for":"node", "attr.name":"T_QueryType", "id":"T_QueryType"}
    cormelFieldDefs["T_QueryIPAddressPort"] = {"attr.type":"string", "for":"node", "attr.name":"T_QueryIPAddressPort", "id":"T_QueryIPAddressPort"}
    cormelFieldDefs["T_ReplyType"] = {"attr.type":"string", "for":"node", "attr.name":"T_ReplyType", "id":"T_ReplyType"}
    cormelFieldDefs["T_ReplyErrorCode"] = {"attr.type":"string", "for":"node", "attr.name":"T_ReplyErrorCode", "id":"T_ReplyErrorCode"}
    cormelFieldDefs["T_Flags"] = {"attr.type":"string", "for":"node", "attr.name":"T_Flags", "id":"T_Flags"}
    cormelFieldDefs["T_TransactionStatus"] = {"attr.type":"string", "for":"node", "attr.name":"TransactionStatus", "id":"T_TransactionStatus"}
    return cormelFieldDefs

def keysForHsegment():
    cormelFieldDefs = {}
    cormelFieldDefs["H_CormelFile"] = {"attr.type":"string", "for":"node", "attr.name":"CormelFile", "id":"H_CormelFile"}
    cormelFieldDefs["H_DuName"] = {"attr.type":"string", "for":"node", "attr.name":"H_DuName", "id":"H_DuName"}
    cormelFieldDefs["H_HopTimeSec"] = {"attr.type":"string", "for":"node", "attr.name":"H_HopTimeSec", "id":"H_HopTimeSec"}
    cormelFieldDefs["H_RespTimeSec"] = {"attr.type":"string", "for":"node", "attr.name":"H_RespTimeSec", "id":"H_RespTimeSec"}
    cormelFieldDefs["H_ContraHopTimeSec"] = {"attr.type":"string", "for":"node", "attr.name":"H_ContraHopTimeSec", "id":"H_ContraHopTimeSec"}
    cormelFieldDefs["H_InFlightTimeSec"] = {"attr.type":"string", "for":"node", "attr.name":"H_InFlightTimeSec", "id":"H_InFlightTimeSec"}
    cormelFieldDefs["H_InboundQuerySizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_InboundQuerySizeB", "id":"H_InboundQuerySizeB"}
    cormelFieldDefs["H_OutboundQuerySizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_OutboundQuerySizeB", "id":"H_OutboundQuerySizeB"}
    cormelFieldDefs["H_InboundReplySizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_InboundReplySizeB", "id":"H_InboundReplySizeB"}
    cormelFieldDefs["H_OutboundReplySizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_OutboundReplySizeB", "id":"H_OutboundReplySizeB"}
    cormelFieldDefs["H_InboundQZippedSizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_InboundQZippedSizeB", "id":"H_InboundQZippedSizeB"}
    cormelFieldDefs["H_OutboundQZippedSizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_OutboundQZippedSizeB", "id":"H_OutboundQZippedSizeB"}
    cormelFieldDefs["H_InboundRZippedSizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_InboundRZippedSizeB", "id":"H_InboundRZippedSizeB"}
    cormelFieldDefs["H_OutboundRZippedSizeB"] = {"attr.type":"string", "for":"node", "attr.name":"H_OutboundRZippedSizeB", "id":"H_OutboundRZippedSizeB"}
    return cormelFieldDefs

def keysForMultiDescriptor():
    cormelFieldDefs = {}
    cormelFieldDefs["M_AppEventFile"] = {"attr.type":"string", "for":"node", "attr.name":"AppEventFile", "id":"M_AppEventFile"}
    cormelFieldDefs["M_Code"] = {"attr.type":"string", "for":"node", "attr.name":"M_Code", "id":"M_Code"}
    cormelFieldDefs["M_Descriptor"] = {"attr.type":"string", "for":"node", "attr.name":"M_Descriptor", "id":"M_Descriptor"}
    return cormelFieldDefs

def extraKeysForEdge():
    cormelFieldDefs = {}
    cormelFieldDefs["Rel_DcxId"] = {"attr.type":"string", "for":"edge", "attr.name":"edgeDcxId", "id":"edgeDcxId"}
    cormelFieldDefs["Rel_TreeId"] = {"attr.type":"string", "for":"edge", "attr.name":"edgeTreeId", "id":"edgeTreeId"}
    cormelFieldDefs["Rel_TreeKey"] = {"attr.type":"string", "for":"edge", "attr.name":"edgeTreeKey", "id":"edgeTreeKey"}
    cormelFieldDefs["Rel_TreeList"] = {"attr.type":"string", "for":"edge", "attr.name":"edgeTreeList", "id":"edgeTreeList"}
    return cormelFieldDefs

def parseNeo4jNode(neo4jNode, segment, dataFields):
    #logger.info(dataFields)
    kvMap = dict()
    for neo4jData in neo4jNode.iter("{http://graphml.graphdrawing.org/xmlns}data"):
        key = "_".join((segment[0],neo4jData.get("key")))
        #logger.info(key)
        if (key in dataFields):
            value = neo4jData.text
            kvMap[key] = value
    return kvMap

def merge_dicts(*dict_args):
    # See https://stackoverflow.com/a/26853961
    """
    Given any number of dicts, shallow copy and merge into a new dict,
    precedence goes to key value pairs in latter dicts.
    """
    result = {}
    for dictionary in dict_args:
        result.update(dictionary)
    return result
        
if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate gephi/yed graphml. The main program can be used for testing.')
    parser.add_argument("inFile", type=str, help='file containing the graphml containing the nodes and relationships exported from neo4j')
    parser.add_argument("outFile", type=str, help='file containing the graphml corresponding to the nodes and relationships exported from neo4j')
    parser.add_argument("outType", type=str, choices=["gephi", "yed"], default="gephi", help='type of output: gephi or yed. Defaults to gephi.')
    args = parser.parse_args()

    # See http://www.faqs.org/docs/diveintopython/regression_path.html
    thisDir = os.path.dirname(sys.argv[0])
    thisScript = os.path.basename(sys.argv[0]).replace('.py','')

    # See https://docs.python.org/2/howto/logging-cookbook.html
    logger = logging.getLogger(thisScript)
    logger.setLevel(logging.INFO)
    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    formatter = logging.Formatter('[%(asctime)s]-%(name)s_%(levelname)s - %(message)s')
    ch.setFormatter(formatter)
    logger.addHandler(ch)

    luColor = graphmlSupport.defineColors()
    luSegColor = graphmlSupport.assignColors(luColor)
    
    cormelFieldDefs = dict()
    cormelFieldDefs["Usegment"] = keysForUsegment()
    cormelFieldDefs["Tsegment"] = keysForTsegment()
    cormelFieldDefs["Hsegment"] = keysForHsegment()
    cormelFieldDefs["Error"] = keysForMultiDescriptor()
    cormelFieldDefs["Warning"] = keysForMultiDescriptor()
    cormelMergedFieldDefs = merge_dicts(cormelFieldDefs["Usegment"], cormelFieldDefs["Tsegment"], cormelFieldDefs["Hsegment"], cormelFieldDefs["Error"], cormelFieldDefs["Warning"], extraKeysForEdge())
    cormelMergedFieldDefs["segment"] = {"attr.type":"string", "for":"node", "attr.name":"segment", "id":"segment"}
    
    parser = etree.XMLParser(ns_clean=True)
    tree = etree.parse(args.inFile, parser)
    neo4jGraphml = tree.getroot()
    
    if (args.outType == "gephi"):
        NS = writeGephiGraphml.defineNS()
        outGraphml = writeGephiGraphml.addRootNode(NS, cormelMergedFieldDefs)
    else:
        NS = writeYedGraphml.defineNS()
        outGraphml = writeYedGraphml.addRootNode(NS, cormelMergedFieldDefs)

    for neo4jGraph in neo4jGraphml.iter("{http://graphml.graphdrawing.org/xmlns}graph"):
        #logger.info("In neo4jGraph")
        if (args.outType == "gephi"):
            outGraph = writeGephiGraphml.addGraphNode(outGraphml)
        else:
            outGraph = writeYedGraphml.addGraphNode(outGraphml)

        for neo4jNode in neo4jGraph.iter("{http://graphml.graphdrawing.org/xmlns}node"):
            #logger.info("In neo4jNode")
            id = neo4jNode.get("id")
            labels = neo4jNode.get("labels")
            segment = labels[1:]
            extraFields = parseNeo4jNode(neo4jNode, segment, list(cormelFieldDefs[segment]))
            extraFields["segment"] = segment
            if "T_TransactionStatus" in extraFields:
                generalisedSegment = segment + extraFields["T_TransactionStatus"]
            else:
                generalisedSegment = segment
                
            #logger.info(extraFields)
            if (args.outType == "gephi"):
                rgbDict = graphmlSupport.convertToRgbDict(luSegColor[generalisedSegment])
                writeGephiGraphml.addNeo4jNode(outGraph, id, rgbDict, extraFields)
            else:
                fillColorHexTriplet = graphmlSupport.convertToHexTriplet(luSegColor[generalisedSegment])
                writeYedGraphml.addNeo4jNode(outGraph, id, NS["xmlns_y"], fillColorHexTriplet, extraFields)
                
        for neo4jEdge in neo4jGraph.iter("{http://graphml.graphdrawing.org/xmlns}edge"):
            #logger.info("In neo4jEdge")
            id = neo4jEdge.get("id")
            source = neo4jEdge.get("source")
            target = neo4jEdge.get("target")
            edgelabel = etree.CDATA(neo4jEdge.get("label"))
            for neo4jEdgeData in neo4jEdge.iter("{http://graphml.graphdrawing.org/xmlns}data"):
                if (neo4jEdgeData.get("key") == "Rel_DcxId"):
                    edgeDcxID = etree.CDATA(neo4jEdgeData.text)
                elif (neo4jEdgeData.get("key") == "Rel_TreeId"):
                    edgeTreeId = neo4jEdgeData.text
                elif (neo4jEdgeData.get("key") == "Rel_TreeKey"):
                    edgeTreeKey = neo4jEdgeData.text
                elif (neo4jEdgeData.get("key") == "Rel_TreeList"):
                    edgeTreeList = neo4jEdgeData.text
            extraFields = {
              "edgelabel":edgelabel,
              "edgeDcxID":edgeDcxID,
              "edgeTreeId":edgeTreeId,
              "edgeTreeKey":edgeTreeKey,
              "edgeTreeList":edgeTreeList
              }
            if (args.outType == "gephi"):
                writeGephiGraphml.addNeo4jEdge(outGraph, id, source, target, extraFields)
            else:
                writeYedGraphml.addNeo4jEdge(outGraph, id, source, target, NS["xmlns_y"], extraFields)

    with open(args.outFile, 'w') as f:
        f.write(etree.tostring(outGraphml, xml_declaration=True, encoding="UTF8", standalone=False, pretty_print=True))
