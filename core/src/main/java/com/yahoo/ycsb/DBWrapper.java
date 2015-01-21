/**                                                                                                                                                                                
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.                                                                                                                             
 *                                                                                                                                                                                 
 * Licensed under the Apache License, Version 2.0 (the "License"); you                                                                                                             
 * may not use this file except in compliance with the License. You                                                                                                                
 * may obtain a copy of the License at                                                                                                                                             
 *                                                                                                                                                                                 
 * http://www.apache.org/licenses/LICENSE-2.0                                                                                                                                      
 *                                                                                                                                                                                 
 * Unless required by applicable law or agreed to in writing, software                                                                                                             
 * distributed under the License is distributed on an "AS IS" BASIS,                                                                                                               
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or                                                                                                                 
 * implied. See the License for the specific language governing                                                                                                                    
 * permissions and limitations under the License. See accompanying                                                                                                                 
 * LICENSE file.                                                                                                                                                                   
 */

package com.yahoo.ycsb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.service.StorageServiceMBean;

import com.yahoo.ycsb.JMXClient;
import com.yahoo.ycsb.measurements.Measurements;
import com.yahoo.ycsb.workloads.CoreWorkload;

/**
 * Wrapper around a "real" DB that measures latencies and counts return codes.
 */
public class DBWrapper extends DB
{
	DB _db;
	Measurements _measurements;
	
	public DBWrapper(DB db)
	{
		_db=db;
		_measurements=Measurements.getMeasurements();
	}

	/**
	 * Set the properties for this DB.
	 */
	public void setProperties(Properties p)
	{
		_db.setProperties(p);
	}

	/**
	 * Get the set of properties for this DB.
	 */
	public Properties getProperties()
	{
		return _db.getProperties();
	}

	/**
	 * Initialize any state for this DB.
	 * Called once per DB instance; there is one DB instance per client thread.
	 */
	public void init() throws DBException
	{
		_db.init();
	}

	/**
	 * Cleanup any state for this DB.
	 * Called once per DB instance; there is one DB instance per client thread.
	 */
	public void cleanup() throws DBException
	{
    long st=System.nanoTime();
		_db.cleanup();
    long en=System.nanoTime();
    _measurements.measure("CLEANUP", (int)((en-st)/1000));
	}

	/**
	 * Read a record from the database. Each field/value pair from the result will be stored in a HashMap.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to read.
	 * @param fields The list of fields to read, or null for all of them
	 * @param result A HashMap of field/value pairs for the result
	 * @return Zero on success, a non-zero error code on error
	 */
	public int read(String table, String key, Set<String> fields, HashMap<String,ByteIterator> result)
	{
		
		int res = 0;
		long st=System.currentTimeMillis();
		JMXClient.dumpPackets();
		res=_db.read(table,key,fields,result);
		long en=System.currentTimeMillis();
		_measurements.measure("READ",(int)(en-st));
		_measurements.reportReturnCode("READ",res);
		int threadcount = Integer.parseInt(getProperties().getProperty("threadcount","1"));	
	     JMXClient.cassandraJMX("read",key,result,en,st,this.getProperties().getProperty(CoreWorkload.READ_PROPORTION_PROPERTY),threadcount);
	    return res;
	}

	/**
	 * Perform a range scan for a set of records in the database. Each field/value pair from the result will be stored in a HashMap.
	 *
	 * @param table The name of the table
	 * @param startkey The record key of the first record to read.
	 * @param recordcount The number of records to read
	 * @param fields The list of fields to read, or null for all of them
	 * @param result A Vector of HashMaps, where each HashMap is a set field/value pairs for one record
	 * @return Zero on success, a non-zero error code on error
	 */
	public int scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String,ByteIterator>> result)
	{
		long st=System.nanoTime();
		JMXClient.dumpPackets();
		int res=_db.scan(table,startkey,recordcount,fields,result);
		long en=System.nanoTime();
		_measurements.measure("SCAN",(int)((en-st)/1000));
		_measurements.reportReturnCode("SCAN",res);
		int threadcount = Integer.parseInt(getProperties().getProperty("threadcount","1"));	
		JMXClient.cassandraJMX("scan",startkey,null,en,st,this.getProperties().getProperty(CoreWorkload.READ_PROPORTION_PROPERTY),threadcount);
		return res;
	}
	
	/**
	 * Update a record in the database. Any field/value pairs in the specified values HashMap will be written into the record with the specified
	 * record key, overwriting any existing values with the same field name.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to write.
	 * @param values A HashMap of field/value pairs to update in the record
	 * @return Zero on success, a non-zero error code on error
	 */
	public int update(String table, String key, HashMap<String,ByteIterator> values)
	{
		long st=System.nanoTime();
		JMXClient.dumpPackets();
		int res=_db.update(table,key,values);
		long en=System.nanoTime();
		_measurements.measure("UPDATE",(int)((en-st)/1000));
		_measurements.reportReturnCode("UPDATE",res);
		
		int threadcount = Integer.parseInt(getProperties().getProperty("threadcount","1"));	
		JMXClient.cassandraJMX("update",key,values,en,st,this.getProperties().getProperty(CoreWorkload.READ_PROPORTION_PROPERTY),threadcount);
		return res;
	}

	/**
	 * Insert a record in the database. Any field/value pairs in the specified values HashMap will be written into the record with the specified
	 * record key.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to insert.
	 * @param values A HashMap of field/value pairs to insert in the record
	 * @return Zero on success, a non-zero error code on error
	 */
	public int insert(String table, String key, HashMap<String,ByteIterator> values)
	{
		long st=System.nanoTime();
		JMXClient.dumpPackets();
		int res=_db.insert(table,key,values);
		long en=System.nanoTime();
		_measurements.measure("INSERT",(int)((en-st)/1000));
		_measurements.reportReturnCode("INSERT",res);
		
		int threadcount = Integer.parseInt(getProperties().getProperty("threadcount","1"));	
		JMXClient.cassandraJMX("insert",key,values,en,st,this.getProperties().getProperty(CoreWorkload.READ_PROPORTION_PROPERTY),threadcount);
		return res;
	}

	/**
	 * Delete a record from the database. 
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to delete.
	 * @return Zero on success, a non-zero error code on error
	 */
	public int delete(String table, String key)
	{
		long st=System.nanoTime();
		JMXClient.dumpPackets();
		int res=_db.delete(table,key);
		long en=System.nanoTime();
		_measurements.measure("DELETE",(int)((en-st)/1000));
		_measurements.reportReturnCode("DELETE",res);
		
		int threadcount = Integer.parseInt(getProperties().getProperty("threadcount","1"));	
		JMXClient.cassandraJMX("delete",key,null,en,st,this.getProperties().getProperty(CoreWorkload.READ_PROPORTION_PROPERTY),threadcount);
		return res;
	}
}
