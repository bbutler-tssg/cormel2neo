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

function log() {
  now=`date +'%Y%m%d_%H:%M:%S'`
  echo -e "$now $1"
}

function upload() {
  cormelDataFile=$1
  classPath=$2
  mainClass=$3
  rowInsertReportFrequency=$4
  rowInsertTransactionSize=$5
  ext=$6
  filterFirst=$7
  appEventFilterFile=$8
  dataSource=`basename ${cormelDataFile} ${ext}`
  uRecordsToProcess=`script/avroRecordCount.sh $cormelDataFile 2> /dev/null`
  log "There are $uRecordsToProcess records to process from $cormelDataFile..."
  javaArgs="-i $cormelDataFile -n $uRecordsToProcess -p $rowInsertReportFrequency -b $rowInsertTransactionSize -s $dataSource"
  if [ "$filterFirst" = "y" ]; then
    javaArgs="$javaArgs -f -a $appEventFilterFile"
  fi
  java -Dfile.encoding=UTF-8 -classpath $classPath $mainClass $javaArgs
  log "Run ended for $cormelDataFile"
}

function usage() {
  log "Usage: $0 -t|type <uploadType> -r|refresh -a|appEventFilterFile <appEventFilterFile.avro> <infile.avro>, where <infile.avro> is *mandatory*, <uploadType> defaults to full, <refresh> indicator defaults to n and takes the value y otherwise, <appEventFilterFile> is optional but if supplied, is used to filter the Cormel data on upload.\nStopping..."
}

# The option handling below was based on https://stackoverflow.com/a/29754866/1988855
getopt --test > /dev/null
if [[ $? -ne 4 ]]; then
  log "Enhanced getopt is not installed\nStopping..."
  exit 1
fi

OPTIONS=hrt:a:
LONGOPTIONS=help,refresh,type:,appEventFilterFile:

# -temporarily store output to be able to check for errors
# -activate advanced mode getopt quoting e.g. via “--options”
# -pass arguments only via   -- "$@"   to separate them correctly
PARSED=$(getopt --options=$OPTIONS --longoptions=$LONGOPTIONS --name "$0" -- "$@")
if [[ $? -ne 0 ]]; then
  # e.g. $? == 1
  #  then getopt has already complained about wrong arguments to stdout
  exit 2
fi
# use eval with "$PARSED" to properly handle the quoting
eval set -- "$PARSED"

# Default values for the options...
refresh=n
uploadType=full
filterFirst=n

# now enjoy the options in order and nicely split until we see --
while true; do
  case "$1" in
    -h|--help)
      usage
      exit 0
      ;;
    -r|--refresh)
      refresh=y
      shift
      ;;
    -t|--type)
      uploadType="$2"
      shift 2
      ;;
    -a|--appEventFilterFile)
      appEventFilterFile=`readlink -f "$2"`
      filterFirst=y # implied, because appEventFilterFile is given
      shift 2
      ;;
    --)
      shift
      break
      ;;
    *)
      echo "Unexpected argument error"
      usage
      exit 3
      ;;
  esac
done

# handle non-option arguments
if [[ $# -ne 1 ]]; then
  echo "$0: A single input path (either a directory of Cormel Avro files, or a single Cormel Avro file) is required."
  usage
  exit 4
else
  cormelDataPath=`readlink -f $1` # See https://stackoverflow.com/a/284671/1988855
fi

echo "refresh = $refresh uploadType = $uploadType path = $cormelDataPath appEventFilterFile = $appEventFilterFile filterFirst = $filterFirst"

mainClass=org.tssg.solas.bb.cormel2neo.CormelAvroToNeo
workDir=$HOME/cormel2neo/target/classes

M2_REPO=$HOME/.m2/repository
avro=$M2_REPO/org/apache/avro/avro/1.8.2/avro-1.8.2.jar
jackson_core_asl=$M2_REPO/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar
jackson_mapper_asl=$M2_REPO/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar
paranamer=$M2_REPO/com/thoughtworks/paranamer/paranamer/2.7/paranamer-2.7.jar
snappy=$M2_REPO/org/xerial/snappy/snappy-java/1.1.1.3/snappy-java-1.1.1.3.jar
commons_compress=$M2_REPO/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar
xz=$M2_REPO/org/tukaani/xz/1.5/xz-1.5.jar
slf4j_api=$M2_REPO/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar
logback_classic=$M2_REPO/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar
logback_core=$M2_REPO/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar
neo4j=$M2_REPO/org/neo4j/neo4j/3.2.1/neo4j-3.2.1.jar
neo4j_kernel=$M2_REPO/org/neo4j/neo4j-kernel/3.2.1/neo4j-kernel-3.2.1.jar
neo4j_graphdb_api=$M2_REPO/org/neo4j/neo4j-graphdb-api/3.2.1/neo4j-graphdb-api-3.2.1.jar
neo4j_resource=$M2_REPO/org/neo4j/neo4j-resource/3.2.1/neo4j-resource-3.2.1.jar
neo4j_common=$M2_REPO/org/neo4j/neo4j-common/3.2.1/neo4j-common-3.2.1.jar
neo4j_collections=$M2_REPO/org/neo4j/neo4j-collections/3.2.1/neo4j-collections-3.2.1.jar
neo4j_primitive_collections=$M2_REPO/org/neo4j/neo4j-primitive-collections/3.2.1/neo4j-primitive-collections-3.2.1.jar
neo4j_unsafe=$M2_REPO/org/neo4j/neo4j-unsafe/3.2.1/neo4j-unsafe-3.2.1.jar
neo4j_io=$M2_REPO/org/neo4j/neo4j-io/3.2.1/neo4j-io-3.2.1.jar
commons_lang3=$M2_REPO/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar
neo4j_csv=$M2_REPO/org/neo4j/neo4j-csv/3.2.1/neo4j-csv-3.2.1.jar
neo4j_logging=$M2_REPO/org/neo4j/neo4j-logging/3.2.1/neo4j-logging-3.2.1.jar
neo4j_lucene_upgrade=$M2_REPO/org/neo4j/neo4j-lucene-upgrade/3.2.1/neo4j-lucene-upgrade-3.2.1.jar
lucene_backward_codecs=$M2_REPO/org/apache/lucene/lucene-backward-codecs/5.5.0/lucene-backward-codecs-5.5.0.jar
neo4j_configuration=$M2_REPO/org/neo4j/neo4j-configuration/3.2.1/neo4j-configuration-3.2.1.jar
neo4j_index=$M2_REPO/org/neo4j/neo4j-index/3.2.1/neo4j-index-3.2.1.jar
neo4j_lucene_index=$M2_REPO/org/neo4j/neo4j-lucene-index/3.2.1/neo4j-lucene-index-3.2.1.jar
lucene_analyzers_common=$M2_REPO/org/apache/lucene/lucene-analyzers-common/5.5.0/lucene-analyzers-common-5.5.0.jar
lucene_core=$M2_REPO/org/apache/lucene/lucene-core/5.5.0/lucene-core-5.5.0.jar
lucene_queryparser=$M2_REPO/org/apache/lucene/lucene-queryparser/5.5.0/lucene-queryparser-5.5.0.jar
lucene_codecs=$M2_REPO/org/apache/lucene/lucene-codecs/5.5.0/lucene-codecs-5.5.0.jar
neo4j_graph_algo=$M2_REPO/org/neo4j/neo4j-graph-algo/3.2.1/neo4j-graph-algo-3.2.1.jar
neo4j_udc=$M2_REPO/org/neo4j/neo4j-udc/3.2.1/neo4j-udc-3.2.1.jar
neo4j_cypher=$M2_REPO/org/neo4j/neo4j-cypher/3.2.1/neo4j-cypher-3.2.1.jar
scala_library=$M2_REPO/org/scala-lang/scala-library/2.11.8/scala-library-2.11.8.jar
scala_reflect=$M2_REPO/org/scala-lang/scala-reflect/2.11.8/scala-reflect-2.11.8.jar
neo4j_graph_matching=$M2_REPO/org/neo4j/neo4j-graph-matching/3.1.3/neo4j-graph-matching-3.1.3.jar
neo4j_codegen=$M2_REPO/org/neo4j/neo4j-codegen/3.2.1/neo4j-codegen-3.2.1.jar
asm=$M2_REPO/org/ow2/asm/asm/5.2/asm-5.2.jar
concurrentlinkedhashmap=$M2_REPO/com/googlecode/concurrentlinkedhashmap/concurrentlinkedhashmap-lru/1.4.2/concurrentlinkedhashmap-lru-1.4.2.jar
caffeine=$M2_REPO/com/github/ben-manes/caffeine/caffeine/2.3.3/caffeine-2.3.3.jar
neo4j_cypher_compiler=$M2_REPO/org/neo4j/neo4j-cypher-compiler-3.2/3.2.1/neo4j-cypher-compiler-3.2-3.2.1.jar
neo4j_cypher_frontend=$M2_REPO/org/neo4j/neo4j-cypher-frontend-3.2/3.2.1/neo4j-cypher-frontend-3.2-3.2.1.jar
neo4j_cypher_ir=$M2_REPO/org/neo4j/neo4j-cypher-ir-3.2/3.2.1/neo4j-cypher-ir-3.2-3.2.1.jar
parboiled_scala=$M2_REPO/org/parboiled/parboiled-scala_2.11/1.1.7/parboiled-scala_2.11-1.1.7.jar
parboiled_core=$M2_REPO/org/parboiled/parboiled-core/1.1.7/parboiled-core-1.1.7.jar
opencsv=$M2_REPO/net/sf/opencsv/opencsv/2.3/opencsv-2.3.jar
neo4j_jmx=$M2_REPO/org/neo4j/neo4j-jmx/3.2.1/neo4j-jmx-3.2.1.jar
neo4j_consistency_check=$M2_REPO/org/neo4j/neo4j-consistency-check/3.2.1/neo4j-consistency-check-3.2.1.jar
neo4j_command_line=$M2_REPO/org/neo4j/neo4j-command-line/3.2.1/neo4j-command-line-3.2.1.jar
neo4j_dbms=$M2_REPO/org/neo4j/neo4j-dbms/3.2.1/neo4j-dbms-3.2.1.jar
neo4j_import_tool=$M2_REPO/org/neo4j/neo4j-import-tool/3.2.1/neo4j-import-tool-3.2.1.jar
neo4j_java_driver=$M2_REPO/org/neo4j/driver/neo4j-java-driver/1.2.1/neo4j-java-driver-1.2.1.jar
jcommander=$M2_REPO/com/beust/jcommander/1.72/jcommander-1.72.jar 
guava=$M2_REPO/com/google/guava/guava/23.0/guava-23.0.jar
findbugs=$M2_REPO/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar
error_prone_annotations=$M2_REPO/com/google/errorprone/error_prone_annotations/2.0.18/error_prone_annotations-2.0.18.jar
j2objc_annotations=$M2_REPO/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar
animal_sniffer_annotations=$M2_REPO/org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar

classPath=$workDir:$avro:$jackson_core_asl:$jackson_mapper_asl:$paranamer:$snappy:$commons_compress:$xz:$slf4j_api:$logback_classic:$logback_core:$neo4j:$neo4j_kernel:$neo4j_graphdb_api:$neo4j_resource:$neo4j_common:$neo4j_collections:$neo4j_primitive_collections:$neo4j_unsafe:$neo4j_io:$commons_lang3:$neo4j_csv:$neo4j_logging:$neo4j_lucene_upgrade:$lucene_backward_codecs:$neo4j_configuration:$neo4j_index:$neo4j_lucene_index:$lucene_analyzers_common:$lucene_core:$lucene_queryparser:$lucene_codecs:$neo4j_graph_algo:$neo4j_udc:$neo4j_cypher:$scala_library:$scala_reflect:$neo4j_graph_matching:$neo4j_codegen:$asm:$neo4j_cypher_compiler:$neo4j_cypher_frontend:$concurrentlinkedhashmap:$caffeine:$neo4j_cypher_compiler:$neo4j_cypher_frontend:$neo4j_cypher_ir:$parboiled_scala:$parboiled_core:$opencsv:$neo4j_jmx:$neo4j_consistency_check:$neo4j_command_line:$neo4j_dbms:$neo4j_import_tool:$neo4j_java_driver:$jcommander:$guava:$findbugs:$error_prone_annotations:$j2objc_annotations:$animal_sniffer_annotations

log "Starting run..."

if [ $uploadType == partial ]; then
  rowInsertReportFrequency=1
  rowInsertTransactionSize=$uRecordsToProcess
else
  rowInsertReportFrequency=500
  rowInsertTransactionSize=2500
fi
# uRecordsToProcess=$((2**31-1)) # largest unsigned 32-bit integer, used as int in Java

if [ "$refresh" = "y" ]; then
  log "Reset the neo4j instance"
  resetNeo4j
fi

localNeo4j start
waitSeconds=3
log "** Wait $waitSeconds seconds until the neo4j server is ready **"
sleep $waitSeconds
ext=.gz.avro
if [ -d "$cormelDataPath" ]; then
  if [ $filterFirst = y ]; then
    waitSeconds=5
  else
    waitSeconds=60
  fi
  for cormelDataFile in ${cormelDataPath}/*${ext}; do 
    args="$cormelDataFile $classPath $mainClass $rowInsertReportFrequency $rowInsertTransactionSize $ext"
    if [ "$filterFirst" = y ]; then
      args="$args $filterFirst $appEventFilterFile"
    fi
    #log "** $args"
    upload $args
    log "Now sleep $waitSeconds seconds to allow the laptop to cool down!"
    sleep $waitSeconds
  done
else
  cormelDataFile=$cormelDataPath
  args="$cormelDataFile $classPath $mainClass $rowInsertReportFrequency $rowInsertTransactionSize $ext"
  if [ "$filterFirst" = y ]; then
    args="$args $filterFirst $appEventFilterFile"
  fi
  #log "** $args"
  upload $args
fi
