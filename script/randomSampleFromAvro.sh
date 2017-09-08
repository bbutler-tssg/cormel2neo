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
# See http://wiki.bash-hackers.org/howto/getopts_tutorial

function usage() {
  echo -e "Usage: $0 -i <inputFile.avro> -o <outputFile.avro> -n <num>\nStopping..."
  exit 1
}

numSamples=20 # DEFAULT!
while getopts "i:o:n:h" opt; do
  case $opt in
    i)
      in=$OPTARG
      ;;
    o)
      out=$OPTARG
      ;;
    n)
      numSamples=$OPTARG
      ;;
    h)
      usage
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done
shift $((OPTIND-1))
# now can process script operands... (none in this case)

if [ "$in" == "" ] || [ "$out" == "" ]; then
  echo "Need to specify both file arguments"
  exit 1
# See https://stackoverflow.com/a/806923/1988855 and https://stackoverflow.com/a/4543229/1988855
#elif ! [[ "$numSamples" =~ '^[0-9]+$' ]]; then
#  echo "Need to specify a numeric numSamples parameter"
#  exit 1
else
  ver=0.1.9
  java -jar $HOME/tools/avro/ratatool-${ver}.jar avro --in $in --out $out --numSamples $numSamples
fi
