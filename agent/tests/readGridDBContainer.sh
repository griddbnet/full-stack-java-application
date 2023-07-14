#!/bin/bash

cat <<EOF | docker exec --interactive griddb-server gs_sh
setnode node0 127.0.0.1 10040 22
setcluster cluster0 myCluster FIXED_LIST 127.0.0.1:10001 \$node0
setclustersql cluster0 myCluster FIXED_LIST 127.0.0.1:20001
setuser admin admin
connect \$cluster0
select * from RAWLOG_host1_tests;
get
dropcontainer RAWLOG_host1_tests
EOF