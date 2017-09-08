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
from graphmlSupport import defineColors, assignColors, convertToHexTriplet, qualifiedElement # local
from writeYedGraphml import defineNS, addGraphmlNode, lookupYedKey, addGraphNode, addGenericNode, addGenericNSNode, addDataResource # local

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Modify yed graphml after layout was added to highlight specific transaction trees.')
    parser.add_argument("inFile", type=str, help='file containing the yed graphml after a layout has been applied')
    parser.add_argument("outFile", type=str, help='file containing the same yed graphml as the inFile, but with all edges apart from those of the specified transaction tree suppressed visually')
    parser.add_argument("highlightedTreeKey", type=int, help='all other transaction trees, apart from that indicated by this id, are semi-hidden by using light grey edges, instead of black edges for the tree indexed by this id')
    args = parser.parse_args()

    highlightedTreeKey = int(args.highlightedTreeKey)

    # See http://www.faqs.org/docs/diveintopython/regression_path.html
    thisDir = os.path.dirname(sys.argv[0])
    thisScript = os.path.basename(sys.argv[0]).replace('.py','')

    # See https://docs.python.org/2/howto/logging-cookbook.html
    logger = logging.getLogger(thisScript)
    logger.setLevel(logging.WARN)
    ch = logging.StreamHandler()
    ch.setLevel(logging.WARN)
    formatter = logging.Formatter('[%(asctime)s]-%(name)s_%(levelname)s - %(message)s')
    ch.setFormatter(formatter)
    logger.addHandler(ch)

    luColor = defineColors()
    luSegColor = assignColors(luColor)
    
    parser = etree.XMLParser(ns_clean=True, strip_cdata=False)
    tree = etree.parse(args.inFile, parser)
    inGraphml = tree.getroot()
    
    NS = defineNS()
    outGraphml = addGraphmlNode(NS)

    requireFields = {"for":"edge", "yfiles.type":"edgegraphics"}
    edgeDrawId = lookupYedKey(inGraphml, NS["xmlns"], requireFields)
    requireFields = {"for":"edge", "attr.name":"edgeTreeKey", "attr.type":"string"}
    edgeTreeKeyId = lookupYedKey(inGraphml, NS["xmlns"], requireFields)
    requiredFields = {"for":"graphml", "yfiles.type":"resources"}
    dataYResourceKeyId = lookupYedKey(inGraphml, NS["xmlns"], requiredFields)
    requiredFields = {"attr.name":"edgeTreeList", "attr.type":"string", "for":"edge"}
    edgeTreeListId = lookupYedKey(inGraphml, NS["xmlns"], requiredFields)
    logger.info("edgeDrawId is {} edgeTreeKeyId is {} dataYResourceKeyId is {} edgeTreeListId is {}".format(edgeDrawId,edgeTreeKeyId,dataYResourceKeyId,edgeTreeListId))
    logger.info("highlightedTreeKey = {}".format(highlightedTreeKey))

    for inComment in inGraphml.iter(tag=etree.Comment):
        outComment = outGraphml.append(inComment)

    for inKey in inGraphml.iter(qualifiedElement(NS["xmlns"],"key")):
        outKey = outGraphml.append(inKey)

    yPolyLineEdgeElementsToCopy = ["Path", "Arrows", "BendStyle"]
    for inGraph in inGraphml.iter(qualifiedElement(NS["xmlns"],"graph")):
        #logger.info("In inGraph")
        outGraph = addGraphNode(outGraphml)

        for inNode in inGraph.iter(qualifiedElement(NS["xmlns"],"node")):
            outNode = outGraph.append(inNode)
                
        for inEdge in inGraph.iter(qualifiedElement(NS["xmlns"],"edge")):
            edgeId = inEdge.get("id")
            edgeSource = inEdge.get("source")
            edgeTarget = inEdge.get("target")
            attribs = {"id":edgeId, "source":edgeSource, "target":edgeTarget}
            outEdge = addGenericNode(outGraph, "edge", attribs, None)

            for inData in inEdge.iter(qualifiedElement(NS["xmlns"],"data")):
                if (inData.get("key") != edgeDrawId):
                    outEdge.append(inData)
                    if (inData.get("key") == edgeTreeKeyId):
                        treeKey = int(inData.text) # careful: this could be CDATA-wrapped, using type conversion seems to force it to unwrap the CDATA if it is there
                    elif (inData.get("key") == edgeTreeListId):
                        edgeTreeList = inData.text
                        #print "treeKey = {}".format(treeKey)
                else:
                    attribs = {"key":edgeDrawId}
                    outData = addGenericNode(outEdge, "data", attribs, None)
                    outYPolyLineEdge = addGenericNSNode(outData, NS["xmlns_y"], "PolyLineEdge", {}, None)
                    for inYPolyLineEdge in inData.iter(qualifiedElement(NS["xmlns_y"],"PolyLineEdge")):
                        for elementName in yPolyLineEdgeElementsToCopy:
                            for inElement in inYPolyLineEdge.iter(qualifiedElement(NS["xmlns_y"],elementName)):
                                outElement = outYPolyLineEdge.append(inElement)
                        for inYLineStyle in inYPolyLineEdge.iter(qualifiedElement(NS["xmlns_y"],"LineStyle")):
                            if (treeKey == highlightedTreeKey):
                                color = "black"
                                yPolyLineEdgeWidth = inYLineStyle.get("width")
                            else:
                                color = "silver"
                                if (str(highlightedTreeKey) in edgeTreeList):
                                    # Since the highlightedTreeKey is one of the edgeTreeList for this edge, set the width to zero to prevent overwriting the edge for the highlighted tree
                                    yPolyLineEdgeWidth = "0.0"
                                else:
                                    yPolyLineEdgeWidth = inYLineStyle.get("width")
                            yPolyLineEdgeColor = convertToHexTriplet(luColor[color])
                            yPolyLineEdgeType = inYLineStyle.get("type")
                            attribs = {"color":yPolyLineEdgeColor, "type":yPolyLineEdgeType, "width":yPolyLineEdgeWidth}
                            outYLineStyle = addGenericNSNode(outYPolyLineEdge, NS["xmlns_y"], "LineStyle", attribs, None)

    outData = addDataResource(outGraphml, NS["xmlns_y"], dataYResourceKeyId)

    with open(args.outFile, 'w') as f:
        f.write(etree.tostring(outGraphml, xml_declaration=True, encoding="UTF8", standalone=False, pretty_print=True))
