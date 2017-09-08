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

function toPng() {
  f=$1
  scaleTo=$2
  if [ ! -f ${f}.png ]; then
    if [ "$scaleTo" != "" ]; then
      pdftoppm -scale-to $scaleTo -f 1 -singlefile -png ${f}.pdf $f
    else
      pdftoppm -f 1 -singlefile -png ${f}.pdf $f
    fi
  fi
}

scaleTo=$1
for f in \
graphics/20170802_153002-CROPPED \
analysis/yed/sample20yed_treeBalloon_nodeBetweennessCentrality \
analysis/yed/sample20yed_treeBalloon_numberConnectedEdges \
analysis/yed/annotated/sample20yed_circular_hl01 \
analysis/yed/annotated/sample20yed_treeBalloon_hl01 \
; do
  log "Converting ${f}.pdf to ${f}.png..."
  toPng $f $scaleTo
done
