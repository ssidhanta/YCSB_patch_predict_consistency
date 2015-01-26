package com.yahoo.ycsb.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

//import net.sourceforge.jpcap.capture.PacketCapture;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.registry.LocateRegistry;
import java.lang.Thread;


//import net.sourceforge.jpcap.capture.PacketCapture;
import org.apache.cassandra.concurrent.JMXEnabledThreadPoolExecutorMBean;
import org.apache.cassandra.db.compaction.CompactionManagerMBean;
import org.apache.cassandra.service.StorageProxyMBean;
import org.apache.cassandra.service.StorageServiceMBean;


import java.io.RandomAccessFile;
import java.lang.InterruptedException;

import org.jnetpcap.JBufferHandler;
import org.jnetpcap.JCaptureHeader;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PeeringException;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;

import com.yahoo.ycsb.DB;
public class JMXClient {

	public static String attr1 = "recentReadLatencyMicros";
	/*public static String attr2 = "recentWriteLatencyMicros";
	public static String attr3 = "totalReadLatencyMicros";
	public static String attr4 = "totalWriteLatencyMicros";
	public static String attr5 = "readOperations";
	public static String attr6 = "writeOperations";
	public static String attr7 = "readRepairRepairedBackground";
	public static String attr8 = "getReadRepairAttempted";
	public static String attr9 = "readRpcTimeout";
	public static String attr10 = "writeRpcTimeout";
	public static String attr11 = "rpcTimeout";
	public static String attr12 = "totalBytesCompacted";*/
	public static String attr14 = "load";
	public static String attr13 = "streamThroughputMbPerSec";
	public static String attr15 = "avgDelta";
	public static String attr16 = "retransmission";
	public static String attr17 = "packetCount";
	public static String attr18 = "threadCount";
	public static String attr19 = "readProportion";
	public static String attr20 = "opType";
	
	
	
	
	public static void main(String[] args) {
		}

}
