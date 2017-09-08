#!/bin/bash
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
inputDir=${1:-$HOME/data/CorMel/extracts_text/incident}
outputDir=${2:-$HOME/data/CorMel/extracts_avro/incident}

M2_REPO=$HOME/.m2/repository
cormelTreeParser=$HOME/data/CorMel/cormel-tree-parser/target/classes
avro=$M2_REPO/org/apache/avro/avro/1.8.2/avro-1.8.2.jar
jackson=$M2_REPO/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar:$M2_REPO/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar
paranamer=$M2_REPO/com/thoughtworks/paranamer/paranamer/2.7/paranamer-2.7.jar
compress=$M2_REPO/org/xerial/snappy/snappy-java/1.1.1.3/snappy-java-1.1.1.3.jar:$M2_REPO/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar:$M2_REPO/org/tukaani/xz/1.5/xz-1.5.jar
logging=$M2_REPO/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:$M2_REPO/org/slf4j/slf4j-log4j12/1.7.25/slf4j-log4j12-1.7.25.jar:$M2_REPO/log4j/log4j/1.2.17/log4j-1.2.17.jar
guava=$M2_REPO/com/google/guava/guava/23.0/guava-23.0.jar

classPath=$cormelTreeParser:$avro:$jackson:$paranamer:$compress:$logging:$guava
mainClass=com.amadeus.prdana.CormelTextToAvro

for inF in ${inputDir}/*.gz; do
  outF=`basename $inF`.avro
  params="$inF $outputDir/$outF"
  java -Dfile.encoding=UTF-8 -classpath $classPath $mainClass $params
done
