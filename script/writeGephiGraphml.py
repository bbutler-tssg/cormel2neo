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
import graphmlSupport # local

def defineNS():
    NS = {}
    NS["xmlns"] = "http://graphml.graphdrawing.org/xmlns"
    return NS
    
def addGraphmlNode(NS):
    # See https://stackoverflow.com/a/2850877
    graphml = etree.Element(
    'graphml',
    nsmap={None: NS["xmlns"]
           }
    )
    return graphml

def addGephiKeys(graphml):
    gephiKeys = dict()
    gephiKeys["label"] = {"attr.name":"label", "attr.type":"string", "for":"node", "id":"label"}
    gephiKeys["weight"] = {"attr.name":"weight", "attr.type":"double", "for":"edge", "id":"weight"}
    gephiKeys["r"] = {"attr.name":"r", "attr.type":"int", "for":"node", "id":"r"}
    gephiKeys["g"] = {"attr.name":"g", "attr.type":"int", "for":"node", "id":"g"}
    gephiKeys["b"] = {"attr.name":"b", "attr.type":"int", "for":"node", "id":"b"}
    gephiKeys["x"] = {"attr.name":"x", "attr.type":"float", "for":"node", "id":"x"}
    gephiKeys["y"] = {"attr.name":"y", "attr.type":"float", "for":"node", "id":"y"}
    gephiKeys["size"] = {"attr.name":"size", "attr.type":"float", "for":"node", "id":"size"}
    for key in gephiKeys:
        addGenericNode(graphml, "key", gephiKeys[key], None)
    return graphml

def addExtraKeyFields(parentNode, extraFieldDefs):
    for key in extraFieldDefs:
        addGenericNode(parentNode, "key", extraFieldDefs[key], None)
    return parentNode

def addNeo4jNode(graph, id, rgbDict, extraFields):
    node = addGenericNode(graph, "node", {"id":id}, None)
    
    # Add standard Gephi data nodes    
    baseContent = dict()
    baseContent["label"] = id
    baseContent["size"] = "10.0"
    baseContent["x"] = "0.0"
    baseContent["y"] = "0.0"
    for key in baseContent:
        addGenericNode(node, "data", {"key":key}, baseContent[key])        
    
    # Add RGB data nodes
    for key in rgbDict:
        addGenericNode(node, "data", {"key":key}, str(rgbDict[key]))
        
    # Add remaining (application-specific) data nodes
    for key in extraFields:
        addGenericNode(node, "data", {"key":key}, extraFields[key])
    return node

def addNeo4jEdge(graph, id, source, target, extraFields):
    attribs = {"id":id, "source":source, "target":target}
    edge = addGenericNode(graph, "edge", attribs, None)
    
    # Add standard Gephi data nodes    
    baseContent = dict()
    edgeTreeList = extraFields["edgeTreeList"]
    # print "edgeTreeList is {}".format(edgeTreeList)
    edgeCard = edgeTreeList.count(",")+1 # the number of commas in the csv version of a list is one less than the size of the list
    weight = str(1.0/float(edgeCard))
    baseContent["weight"] = weight
    for key in baseContent:
        addGenericNode(edge, "data", {"key":key}, baseContent[key])        
        
    # Add remaining (application-specific) data nodes
    for key in extraFields:
        addGenericNode(edge, "data", {"key":key}, extraFields[key])
    return edge

def addEdge(graph, id, source, target):
    attribs = {"id":id, "source":source, "target":target}
    edge = addGenericNode(graph, "edge", attribs, None)
    
    # Add standard Gephi data nodes    
    baseContent = dict()
    baseContent["weight"] = "1.0"
    for key in baseContent:
        addGenericNode(edge, "data", {"key":key}, baseContent[key])
    return edge

def addNode(graph, id):
    node = addGenericNode(graph, "node", {"id":id}, None)
    
    # Add standard Gephi data nodes    
    baseContent = dict()
    baseContent["label"] = id
    baseContent["size"] = "10.0"
    baseContent["x"] = "0.0"
    baseContent["y"] = "0.0"
    for key in baseContent:
        addGenericNode(node, "data", {"key":key}, baseContent[key])        
    
    # Add RGB data nodes
    rgbDict = graphmlSupport.convertToRgbDict(luSegColor[extraFields["segment"]])
    for key in rgbDict:
        addGenericNode(node, "data", {"key":key}, str(rgbDict[key]))
        
    # Add remaining (application-specific) data nodes
    for key in extraFields:
        addGenericNode(node, "data", {"key":key}, extraFields[key])
    return node
    
def addGenericNode(parentNode, nodeName, attribs, content):
    genericNode = etree.SubElement(parentNode, nodeName)
    if content is not None:
        genericNode.text = content
    for key in attribs:
        genericNode.set(key, attribs[key])
    return genericNode

def addRootNode(NS, extraFieldDefs):
    graphml = addGraphmlNode(NS)
    graphml = addGephiKeys(graphml)
    graphml = addExtraKeyFields(graphml, extraFieldDefs)
    return graphml

def addGraphNode(graphml):
    graph = addGenericNode(graphml, 'graph', {"edgedefault":"directed"}, None)
    return graph

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate gephi graphml. The main program can be used for testing.')
    parser.add_argument("outFile", type=str, help='files containing the graphml corresponding to the nodes and relationships exported from neo4j')
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
    rgbDict = graphmlSupport.convertToRgbDict(luSegColor[segment])
    addNeo4jNode(graph, id, rgbDict, extraFields)

    id = "n1"
    extraFields = dict()
    segment = "Tsegment"
    extraFields["segment"] = segment
    extraFields["dummyCormel"] = "A cormel field for "+id
    extraFields["dummyCormel2"] = "A second cormel field for "+id
    rgbDict = graphmlSupport.convertToRgbDict(luSegColor[segment])
    addNeo4jNode(graph, id, rgbDict, extraFields)

    extraFields = {"edgelabel":"HAS_T"}
    id = "e0"
    source = "n0"
    target = "n1"
    addNeo4jEdge(graph, id, source, target, extraFields)
    
    with open(args.outFile, 'w') as f:
        f.write(etree.tostring(graphml, xml_declaration=True, encoding="UTF8", standalone=False, pretty_print=True))
