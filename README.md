------------------------------------
We add code in the Cassandra Client for YCSB to call OptCon Client for Predicting the 
Consistency Level to apply to an YCSB operation.

We add code for collecting parameters latency, throughput using Cassandra's JMX Interface. We also include code for measuring packet count using libpcap, and client thread count from operating system Java management bean. We measure Staleeness using Gamma metric (described in the paper). We log the parameters into a txt file for usage in training phase using the OptCon framework, for predicting the appropriate client side consistency setting. 
  

Please refer to the technical paper Consistency.pdf in the folder.
-----------------------------------------------------------------------------------------
Yahoo! Cloud System Benchmark (YCSB)
====================================
[![Build Status](https://travis-ci.org/brianfrankcooper/YCSB.png?branch=master)](https://travis-ci.org/brianfrankcooper/YCSB)

Links
-----
http://wiki.github.com/brianfrankcooper/YCSB/  
http://research.yahoo.com/Web_Information_Management/YCSB/  
ycsb-users@yahoogroups.com  

Getting Started
---------------

1. Download the latest release of YCSB:

    ```sh
    wget https://github.com/downloads/brianfrankcooper/YCSB/ycsb-0.1.4.tar.gz
    tar xfvz ycsb-0.1.4
    cd ycsb-0.1.4
    ```
    
2. Set up a database to benchmark. There is a README file under each binding 
   directory.

3. Run YCSB command. 
    
    ```sh
    bin/ycsb load basic -P workloads/workloada
    bin/ycsb run basic -P workloads/workloada
    ```

  Running the `ycsb` command without any argument will print the usage. 
   
  See https://github.com/brianfrankcooper/YCSB/wiki/Running-a-Workload
  for a detailed documentation on how to run a workload.

  See https://github.com/brianfrankcooper/YCSB/wiki/Core-Properties for 
  the list of available workload properties.
