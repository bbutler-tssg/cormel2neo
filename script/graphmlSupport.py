#!/usr/bin/python
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
#Copyright (c) 2017, Bernard Butler, TSSG, Waterford Institute of Technology (WIT), Ireland
#The work was funded by SOLAS grant FP7-PEOPLE-2013-IAPP 612480.
#All rights reserved.
#
#Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
#
#1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
#
#2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
#
#3. Neither the name of WIT nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
#
#THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

def defineColors():
  luColor = {}
  luColor["aliceblue"] = "240,248,255"
  luColor["antiquewhite"] = "250,235,215"
  luColor["aqua"] = "0,255,255"
  luColor["aquamarine"] = "127,255,212"
  luColor["azure"] = "240,255,255"
  luColor["beige"] = "245,245,220"
  luColor["bisque"] = "255,228,196"
  luColor["black"] = "0,0,0"
  luColor["blanchedalmond"] = "255,235,205"
  luColor["blue"] = "0,0,255"
  luColor["blueviolet"] = "138,43,226"
  luColor["brown"] = "165,42,42"
  luColor["burlywood"] = "222,184,135"
  luColor["cadetblue"] = "95,158,160"
  luColor["chartreuse"] = "127,255,0"
  luColor["chocolate"] = "210,105,30"
  luColor["coral"] = "255,127,80"
  luColor["cornflowerblue"] = "100,149,237"
  luColor["cornsilk"] = "255,248,220"
  luColor["crimson"] = "220,20,60"
  luColor["cyan"] = "0,255,255"
  luColor["darkblue"] = "0,0,139"
  luColor["darkcyan"] = "0,139,139"
  luColor["darkgoldenrod"] = "184,134,11"
  luColor["darkgray"] = "169,169,169"
  luColor["darkgreen"] = "0,100,0"
  luColor["darkgrey"] = "169,169,169"
  luColor["darkkhaki"] = "189,183,107"
  luColor["darkmagenta"] = "139,0,139"
  luColor["darkolivegreen"] = "85,107,47"
  luColor["darkorange"] = "255,140,0"
  luColor["darkorchid"] = "153,50,204"
  luColor["darkred"] = "139,0,0"
  luColor["darksalmon"] = "233,150,122"
  luColor["darkseagreen"] = "143,188,143"
  luColor["darkslateblue"] = "72,61,139"
  luColor["darkslategray"] = "47,79,79"
  luColor["darkslategrey"] = "47,79,79"
  luColor["darkturquoise"] = "0,206,209"
  luColor["darkviolet"] = "148,0,211"
  luColor["deeppink"] = "255,20,147"
  luColor["deepskyblue"] = "0,191,255"
  luColor["dimgray"] = "105,105,105"
  luColor["dimgrey"] = "105,105,105"
  luColor["dodgerblue"] = "30,144,255"
  luColor["firebrick"] = "178,34,34"
  luColor["floralwhite"] = "255,250,240"
  luColor["forestgreen"] = "34,139,34"
  luColor["fuchsia"] = "255,0,255"
  luColor["gainsboro"] = "220,220,220"
  luColor["ghostwhite"] = "248,248,255"
  luColor["gold"] = "255,215,0"
  luColor["goldenrod"] = "218,165,32"
  luColor["gray"] = "128,128,128"
  luColor["grey"] = "128,128,128"
  luColor["green"] = "0,128,0"
  luColor["greenyellow"] = "173,255,47"
  luColor["honeydew"] = "240,255,240"
  luColor["hotpink"] = "255,105,180"
  luColor["indianred"] = "205,92,92"
  luColor["indigo"] = "75,0,130"
  luColor["ivory"] = "255,255,240"
  luColor["khaki"] = "240,230,140"
  luColor["lavender"] = "230,230,250"
  luColor["lavenderblush"] = "255,240,245"
  luColor["lawngreen"] = "124,252,0"
  luColor["lemonchiffon"] = "255,250,205"
  luColor["lightblue"] = "173,216,230"
  luColor["lightcoral"] = "240,128,128"
  luColor["lightcyan"] = "224,255,255"
  luColor["lightgoldenrodyellow"] = "250,250,210"
  luColor["lightgray"] = "211,211,211"
  luColor["lightgreen"] = "144,238,144"
  luColor["lightgrey"] = "211,211,211"
  luColor["lightpink"] = "255,182,193"
  luColor["lightsalmon"] = "255,160,122"
  luColor["lightseagreen"] = "32,178,170"
  luColor["lightskyblue"] = "135,206,250"
  luColor["lightslategray"] = "119,136,153"
  luColor["lightslategrey"] = "119,136,153"
  luColor["lightsteelblue"] = "176,196,222"
  luColor["lightyellow"] = "255,255,224"
  luColor["lime"] = "0,255,0"
  luColor["limegreen"] = "50,205,50"
  luColor["linen"] = "250,240,230"
  luColor["magenta"] = "255,0,255"
  luColor["maroon"] = "128,0,0"
  luColor["mediumaquamarine"] = "102,205,170"
  luColor["mediumblue"] = "0,0,205"
  luColor["mediumorchid"] = "186,85,211"
  luColor["mediumpurple"] = "147,112,219"
  luColor["mediumseagreen"] = "60,179,113"
  luColor["mediumslateblue"] = "123,104,238"
  luColor["mediumspringgreen"] = "0,250,154"
  luColor["mediumturquoise"] = "72,209,204"
  luColor["mediumvioletred"] = "199,21,133"
  luColor["midnightblue"] = "25,25,112"
  luColor["mintcream"] = "245,255,250"
  luColor["mistyrose"] = "255,228,225"
  luColor["moccasin"] = "255,228,181"
  luColor["navajowhite"] = "255,222,173"
  luColor["navy"] = "0,0,128"
  luColor["oldlace"] = "253,245,230"
  luColor["olive"] = "128,128,0"
  luColor["olivedrab"] = "107,142,35"
  luColor["orange"] = "255,165,0"
  luColor["orangered"] = "255,69,0"
  luColor["orchid"] = "218,112,214"
  luColor["palegoldenrod"] = "238,232,170"
  luColor["palegreen"] = "152,251,152"
  luColor["paleturquoise"] = "175,238,238"
  luColor["palevioletred"] = "219,112,147"
  luColor["papayawhip"] = "255,239,213"
  luColor["peachpuff"] = "255,218,185"
  luColor["peru"] = "205,133,63"
  luColor["pink"] = "255,192,203"
  luColor["plum"] = "221,160,221"
  luColor["powderblue"] = "176,224,230"
  luColor["purple"] = "128,0,128"
  luColor["red"] = "255,0,0"
  luColor["rosybrown"] = "188,143,143"
  luColor["royalblue"] = "65,105,225"
  luColor["saddlebrown"] = "139,69,19"
  luColor["salmon"] = "250,128,114"
  luColor["sandybrown"] = "244,164,96"
  luColor["seagreen"] = "46,139,87"
  luColor["seashell"] = "255,245,238"
  luColor["sienna"] = "160,82,45"
  luColor["silver"] = "192,192,192"
  luColor["skyblue"] = "135,206,235"
  luColor["slateblue"] = "106,90,205"
  luColor["slategray"] = "112,128,144"
  luColor["slategrey"] = "112,128,144"
  luColor["snow"] = "255,250,250"
  luColor["springgreen"] = "0,255,127"
  luColor["steelblue"] = "70,130,180"
  luColor["tan"] = "210,180,140"
  luColor["teal"] = "0,128,128"
  luColor["thistle"] = "216,191,216"
  luColor["tomato"] = "255,99,71"
  luColor["turquoise"] = "64,224,208"
  luColor["violet"] = "238,130,238"
  luColor["wheat"] = "245,222,179"
  luColor["white"] = "255,255,255"
  luColor["whitesmoke"] = "245,245,245"
  luColor["yellow"] = "255,255,0"
  luColor["yellowgreen"] = "154,205,50"
  return luColor

def defineSegments():
  segments = [
    "Usegment"
   ,"Tsegment"
   ,"TsegmentOK"
   ,"TsegmentKO"
   ,"Hsegment"
   ,"Error"
   ,"Warning"
  ]
  return segments
    
def assignColors(luColor):
  # See https://brightside.me/article/the-ultimate-color-combinations-cheat-sheet-92405/
  luSegColor = {}
  luSegColor["Usegment"] = luColor["orchid"]
  luSegColor["Tsegment"] = luColor["cyan"]
  luSegColor["TsegmentOK"] = luColor["royalblue"]
  luSegColor["TsegmentKO"] = luColor["yellowgreen"]
  luSegColor["Hsegment"] = luColor["orange"]
  luSegColor["Error"] = luColor["gray"]
  luSegColor["Warning"] = luColor["lightgray"]
  return luSegColor

def convertToHexTriplet(rgbStr):
  rgbList = rgbStr.split(',')
  return "#{0:02X}{1:02X}{2:02X}".format(int(rgbList[0]), int(rgbList[1]), int(rgbList[2]))

def convertToRgbDict(rgbStr):
    # See https://stackoverflow.com/a/4289557 and https://stackoverflow.com/a/4071407
    rgbTriple = [int(s) for s in rgbStr.split(",") if s.isdigit()]
    rgbDict = {}
    rgbDict["r"] = rgbTriple[0]
    rgbDict["g"] = rgbTriple[1]
    rgbDict["b"] = rgbTriple[2]
    return rgbDict

def qualifiedElement(ns, name):
    qE = "{{{}}}{}".format(ns, name) # Note the extra wrapping of {{}} because of the fact that {} is both the format placeholder and the XML qualifier wrapper
    return qE

