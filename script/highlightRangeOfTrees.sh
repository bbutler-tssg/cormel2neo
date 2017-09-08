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
  echo $now $1
}

if [ $# -lt 2 ]; then
  echo -e "Usage: $0 <masterFile>.graphml <numTrees>\nStopping..."
  exit 1
fi
masterFile=$1
numTrees=$2
nDigit=`echo -n $numTrees | wc -c`
# See https://stackoverflow.com/a/169517/1988855
for i in $(seq 1 $numTrees); do
  # See https://stackoverflow.com/a/8789815/1988855
  printf -v j "%0${nDigit}d" $i
  log "Highlight transaction tree $i"
  script/highlightTree.py ${masterFile}.graphml ${masterFile}_hl${j}.graphml $i
  log "Standardise the format of ${masterFile}_hl${j}.graphml"
  [ -f ${masterFile}_hl${j}.graphml ] && xmllint --format ${masterFile}_hl${j}.graphml > ${masterFile}_hl${j}_pp.graphml && mv ${masterFile}_hl${j}_pp.graphml ${masterFile}_hl${j}.graphml
done
