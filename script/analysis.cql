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
# By DataSource
MATCH (n:Usegment) RETURN n.dataSource AS DataSource, COUNT(*) AS COUNT ORDER BY n.dataSource;
MATCH (n:Tsegment) WHERE n.TransactionStatus IS NOT NULL RETURN n.dataSource AS DataSource, COUNT(*) AS COUNT ORDER BY n.dataSource;
MATCH (u:Usegment), (t:Tsegment) WHERE u.DcxId = t.DcxId AND t.TransactionStatus IS NULL RETURN DISTINCT u;

# By DcxId, TreeId
MATCH (n:Usegment) RETURN n.DcxId AS DcxId, n.TreeId AS TreeId ORDER BY n.DcxId, n.TreeId;
MATCH (n:Tsegment) WHERE n.TransactionStatus IS NOT NULL RETURN n.DcxId AS DcxId, split(n.TrxNb,"-")[0] AS TreeId ORDER BY n.DcxId, TreeId;

# By TransactionStatus
MATCH (n:Tsegment) WHERE n.TransactionStatus IS NOT NULL RETURN n.TransactionStatus AS TransactionStatus, COUNT(*) AS Count;

