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
from graphmlSupport import defineColors, defineSegments, assignColors, convertToHexTriplet, qualifiedElement # Local

def  defineNS():
    NS = {}
    NS["xmlns"] = "http://graphml.graphdrawing.org/xmlns"
    NS["xmlns_java"] = "http://www.yworks.com/xml/yfiles-common/1.0/java"
    NS["xmlns_sys"] = "http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0"
    NS["xmlns_x"] = "http://www.yworks.com/xml/yfiles-common/markup/2.0"
    NS["xmlns_xsi"] = "http://www.w3.org/2001/XMLSchema-instance"
    NS["xmlns_y"] = "http://www.yworks.com/xml/graphml"
    NS["xmlns_yed"] = "http://www.yworks.com/xml/yed/3"
    NS["xsi_schemaLocation"] = "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd"
    return NS
    
def addGraphmlNode(NS):
    # See https://stackoverflow.com/a/2850877
    graphml = etree.Element(
    'graphml',
    attrib={"{" + NS["xmlns_xsi"] + "}schemaLocation": NS["xsi_schemaLocation"]},
    nsmap={None: NS["xmlns"],
           'java': NS["xmlns_java"],
           'sys': NS["xmlns_sys"],
           'x': NS["xmlns_x"],
           'xsi': NS["xmlns_xsi"],
           'y': NS["xmlns_y"],
           'yed': NS["xmlns_yed"]
           }
    )
    return graphml

def addYedKeys(graphml):
    yedKeys = dict()
    yedKeys["d0"] = {"for":"port", "id":"d0", "yfiles.type":"portgraphics"}
    yedKeys["d1"] = {"for":"port", "id":"d1", "yfiles.type":"portgeometry"}
    yedKeys["d2"] = {"for":"port", "id":"d2", "yfiles.type":"portuserdata"}
    yedKeys["d3"] = {"attr.name":"url", "attr.type":"string", "for":"node", "id":"d3"}
    yedKeys["d4"] = {"attr.name":"description", "attr.type":"string", "for":"node", "id":"d4"}
    yedKeys["d5"] = {"for":"node", "id":"d5", "yfiles.type":"nodegraphics"}
    yedKeys["d6"] = {"for":"graphml", "id":"d6", "yfiles.type":"resources"}
    yedKeys["d7"] = {"attr.name":"url", "attr.type":"string", "for":"edge", "id":"d7"}
    yedKeys["d8"] = {"attr.name":"description", "attr.type":"string", "for":"edge", "id":"d8"}
    yedKeys["d9"] = {"for":"edge", "id":"d9", "yfiles.type":"edgegraphics"}
    for key in yedKeys:
        addGenericNode(graphml, "key", yedKeys[key], None)
    return graphml

def lookupYedKey(graphml, ns, requiredFields):
    # See https://stackoverflow.com/a/32815843/1988855
    requiredFieldItemSet = set(requiredFields.items())
    foundId = ""
    for key in graphml.iter(qualifiedElement(ns,"key")):
        availableFieldItemSet = set(key.items())
        # See https://stackoverflow.com/a/21191323/1988855
        if ((requiredFieldItemSet - availableFieldItemSet) == set()):
            foundId = key.get("id")
            break
    return foundId

def addExtraKeyFields(parentNode, extraFieldDefs):
    for key in extraFieldDefs:
        addGenericNode(parentNode, "key", extraFieldDefs[key], None)
    return parentNode


def addNeo4jNode(graph, id, xmlns_y, fillColorHexTriplet, extraFields):
    node = addGenericNode(graph, "node", {"id":id}, None)
    
    # Add standard Yed data nodes  
    baseContent = dict()
    baseContent["d4"] = None
    baseContent["d5"] = None
    addGenericNode(node, "data", {"key":"d4"}, baseContent["d4"])        
    dataNode = addGenericNode(node, "data", {"key":"d5"}, baseContent["d5"])        
    
    # Add yed shape node - note the fillColor
    addY_ShapeNode(dataNode, xmlns_y, fillColorHexTriplet)
        
    # Add remaining (application-specific) data nodes
    for key in extraFields:
        addGenericNode(node, "data", {"key":key}, extraFields[key])
    return node

def addNeo4jEdge(graph, id, source, target, xmlns_y, extraFields):
    attribs = {"id":id, "source":source, "target":target}
    edge = addGenericNode(graph, "edge", attribs, None)
    
    # Add standard Yed data nodes    
    addGenericNode(edge, "data", {"key":"weight"}, "1.0")        
    addGenericNode(edge, "data", {"key":"d8"}, None)
    dataNode = addGenericNode(edge, "data", {"key":"d9"}, None)
    addY_PolyLineEdge(dataNode, xmlns_y)
        
    # Add remaining (application-specific) data nodes
    for key in extraFields:
        addGenericNode(edge, "data", {"key":key}, extraFields[key])
    return edge

def addY_Fill(parentNode, xmlns_y, fillColor):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Fill", {"color":fillColor, "transparent":"false"}, None)
    return elmt

def addY_Geometry(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Geometry", {"height":"30.0", "width":"30.0", "x":"-15.0", "y":"-15.0"}, None)
    return elmt

def addY_BorderStyle(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "BorderStyle", {"color":"#000000", "raised":"false", "type":"line", "width":"1.0"}, None)
    return elmt

def addY_SmartNodeLabelModel(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "SmartNodeLabelModel", {"distance":"4.0"}, None)
    return elmt

def addY_SmartNodeLabel(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "SmartNodeLabel", {}, None)
    addY_SmartNodeLabelModel(elmt, xmlns_y)
    return elmt

def addY_SmartNodeLabelModelParameter(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "SmartNodeLabelModelParameter", {"labelRatioX":"0.0", "labelRatioY":"0.0", "nodeRatioX":"0.0", "nodeRatioY":"0.0", "offsetX":"0.0", "offsetY":"0.0", "upX":"0.0", "upY":"-1.0"}, None)
    return elmt

def addY_ModelParameter(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "ModelParameter", {}, None)
    addY_SmartNodeLabelModelParameter(elmt, xmlns_y)
    return elmt

def addY_NodeLabel(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "NodeLabel", {"alignment":"center", "autoSizePolicy":"content", "fontFamily":"Dialog", "fontSize":"12", "fontStyle":"plain", "hasBackgroundColor":"false", "hasLineColor":"false", "hasText":"false", "height":"4.0", "horizontalTextPosition":"center", "iconTextGap":"4", "modelName":"custom", "textColor":"#000000", "verticalTextPosition":"bottom", "visible":"true", "width":"4.0", "x":"13.0", "y":"13.0"}, None)
    addY_SmartNodeLabel(elmt, xmlns_y)
    addY_ModelParameter(elmt, xmlns_y)
    return elmt

def addY_Shape(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Shape", {"type":"rectangle"}, None)
    return elmt

def addY_ShapeNode(parentNode, xmlns_y, fillColor):
    y_ShapeNode = addGenericNSNode(parentNode, xmlns_y, "ShapeNode", {}, None)
    addY_Geometry(y_ShapeNode, xmlns_y)
    addY_Fill(y_ShapeNode, xmlns_y, fillColor)
    addY_BorderStyle(y_ShapeNode, xmlns_y)
    addY_NodeLabel(y_ShapeNode, xmlns_y)
    addY_Shape(y_ShapeNode, xmlns_y)
    return y_ShapeNode

def addY_Path(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Path", {"sx":"0.0", "sy":"0.0", "tx":"0.0", "ty":"0.0"}, None)
    return elmt

def addY_LineStyle(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "LineStyle", {"color":"#000000", "type":"line", "width":"1.0"}, None)
    return elmt

def addY_Arrows(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Arrows", {"source":"none", "target":"standard"}, None)
    return elmt

def addY_BendStyle(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "BendStyle", {"smoothed":"false"}, None)
    return elmt

def addY_PolyLineEdge(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "PolyLineEdge", {}, None)
    addY_Path(elmt, xmlns_y)
    addY_LineStyle(elmt, xmlns_y)
    addY_Arrows(elmt, xmlns_y)
    addY_BendStyle(elmt, xmlns_y)
    return elmt

def addY_Resources(parentNode, xmlns_y):
    elmt = addGenericNSNode(parentNode, xmlns_y, "Resources", {}, None)
    return elmt

def addEdge(graph, id, source, target, xmlns_y):
    attribs = {"id":id, "source":source, "target":target}
    edge = addGenericNode(graph, "edge", attribs, None)
    attribs = {"key":"d8"}
    addGenericNode(edge, "data", attribs, None)
    attribs = {"key":"d9"}
    dataNode = addGenericNode(edge, "data", attribs, None)
    addY_PolyLineEdge(dataNode, xmlns_y)
    return edge

def addDataResource(graphml, xmlns_y, keyId):
    attribs = {"key":keyId}
    data = addGenericNode(graphml, "data", attribs, None)
    addY_Resources(data, xmlns_y)
    return data

def addGenericNSNode(parentNode, ns, nodeName, attribs, content):
    genericNode = etree.SubElement(parentNode, "{%s}%s" % (ns,nodeName))
    if content is not None:
        genericNode.text = content
    for key in attribs:
        genericNode.set(key, attribs[key])
    return genericNode
        
def addGenericNode(parentNode, nodeName, attribs, content):
    genericNode = etree.SubElement(parentNode, nodeName)
    if content is not None:
        genericNode.text = content
    for key in attribs:
        genericNode.set(key, attribs[key])
    return genericNode

def addRootNode(NS, extraFieldDefs):
    graphml = addGraphmlNode(NS)
    graphml = addYedKeys(graphml)
    graphml = addExtraKeyFields(graphml, extraFieldDefs)
    return graphml

def addGraphNode(graphml):
    graph = addGenericNode(graphml, 'graph', {"edgedefault":"directed", "id":"G"}, None)
    return graph

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate yed graphml. The main program can be used for testing.')
    parser.add_argument("outFile", type=str, help='files containing the graphml corresponding to the nodes and relationships exported as text from neo4j')
    args = parser.parse_args()

    luColor = graphmlSupport.defineColors()
    segments = graphmlSupport.defineSegments()
    luSegColor = graphmlSupport.assignColors(luColor)
    
    NS = defineNS()
    extraFieldDefs = dict()
    extraFieldDefs["segment"] = {"attr.type":"string", "attr.name":"segment", "for":"node", "id":"segment"}
    extraFieldDefs["dummyCormel"] = {"attr.type":"string", "attr.name":"dummyCormel", "for":"node", "id":"dummyCormel"}
    extraFieldDefs["dummyCormel2"] = {"attr.type":"string", "attr.name":"dummyCormel2", "for":"node", "id":"dummyCormel2"}
    extraFieldDefs["edgelabel"] = {"attr.name":"Edge Label", "attr.type":"string", "for":"edge", "id":"edgelabel"}

    graphml = addRootNode(NS, extraFieldDefs)
    
    graph = addGraphNode(graphml)

    id = "n0"
    extraFields = dict()
    segment = "Usegment"
    extraFields["segment"] = segment
    extraFields["dummyCormel"] = "A cormel field for "+id
    extraFields["dummyCormel2"] = "A second cormel field for "+id
    fillColorHexTriplet = graphmlSupport.convertToHexTriplet(luSegColor[segment])
    addNeo4jNode(graph, id, NS["xmlns_y"], fillColorHexTriplet, extraFields)

    id = "n1"
    extraFields = dict()
    segment = "Tsegment"
    extraFields["segment"] = segment
    extraFields["dummyCormel"] = "A cormel field for "+id
    extraFields["dummyCormel2"] = "A second cormel field for "+id
    fillColorHexTriplet = graphmlSupport.convertToHexTriplet(luSegColor[segment])
    addNeo4jNode(graph, id, NS["xmlns_y"], fillColorHexTriplet, extraFields)
    
    id = "e0"
    source = "n0"
    target = "n1"
    extraFields = {"edgelabel":"HAS_T"}
    addNeo4jEdge(graph, id, source, target, NS["xmlns_y"], extraFields)
    
    addDataResource(graphml, NS["xmlns_y"])
    
    with open(args.outFile, 'w') as f:
        f.write(etree.tostring(graphml, xml_declaration=True, encoding="UTF8", standalone=False, pretty_print=True))
