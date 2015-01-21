package com.yahoo.ycsb;

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

import com.yahoo.ycsb.JMXLogger;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.workloads.CoreWorkload;
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
	public static String attr14 = "load";
	public static String attr13 = "streamThroughputMbPerSec";
	public static String attr15 = "avgDelta";
	public static String attr16 = "retransmission";
	public static String attr17 = "packetCount";
	public static String attr18 = "threadCount";
	public static String attr19 = "readProportion";
	public static String attr20 = "opType";
	public static String attrHost = "172.31.32.104";
	public static String attrPort = "172.31.32.104";
	
	
	public static int getPacketCount(){
		int cnt = 0;
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  
        	
		String ofile = "/tmp/tmp-capture-file.cap";  
		
		Pcap pcap = Pcap.openOffline(ofile, errbuf);
		
		PcapHeader hdr = new PcapHeader(JMemory.POINTER);  
		JBuffer buf = new JBuffer(JMemory.POINTER);  
		if(pcap!=null)
		{	
			int id= JRegistry.mapDLTToId(pcap.datalink());  
			while (pcap.nextEx(hdr, buf) == Pcap.NEXT_EX_OK) {  
				  PcapPacket packet = new PcapPacket(hdr, buf);  
				  packet.scan(id);  
				  cnt++;
				  // Do normal processing
			}
		}
		
		return cnt;
		
	}
	
	public static void dumpPackets(){
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  
        
		 /*************************************************************************** 
         * Second we open up the selected device 
         **************************************************************************/  
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 100;           // 10 seconds in millis  
		 Pcap pcap =  
				     Pcap.openLive("eth0", snaplen, flags, timeout, errbuf);

		String ofile = "/tmp/tmp-capture-file.cap";
		File f = new File(ofile);
		if(f.exists())
			f.delete();
		PcapDumper dumper = pcap.dumpOpen(ofile); // output file  
		
		JBufferHandler<PcapDumper> dumpHandler = new JBufferHandler<PcapDumper>() {  
		
		public void nextPacket(PcapHeader header, JBuffer buffer, PcapDumper dumper) {  
		
		dumper.dump(header, buffer);  
		}  
		};  
		
		pcap.loop(100, dumpHandler, dumper);  
		             
		File file1 = new File(ofile);  
		dumper.close(); // Won't be able to delete without explicit close 
		pcap.close(); 
		
	}
	
	public static double getAppThreadCount(){
		Process p;
		BufferedReader reader = null;
		double tCount = 0;
		
		try {
			List<String> commands = new ArrayList<String>();
            commands.add("/bin/sh");
            commands.add("-c");
            commands.add("ps ax -f -L | grep ycsb ");
			Process process = new ProcessBuilder(commands).start();
            
			reader = 
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";		
            
			if ((line = reader.readLine())!= null) {
				
				if(line.toString().split("     ").length>1 && line.toString().split("     ")[1].split("    ").length>1)
				{
					tCount=Double.parseDouble(line.toString().split("     ")[1].split("    ")[1].trim().split(" ")[0]);
					
				}
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
		return tCount;
	}
	
	public static double parseNet(){
		String str = null;
		File file = new File("/proc/net/tcp");
		FileReader reader;
		 double delta = 0;
		try {
			reader = new FileReader(file);
			BufferedReader buffReader = new BufferedReader(reader);
		    String s;
		   
		    int i = 0;
		    
		    while((s = buffReader.readLine()) != null){
		    	
		    	if(i>0 && s.contains("        ") && s.split("        ")[1].contains(" ") && s.split("        ")[1].split(" ").length>7)
		    		delta = Double.parseDouble(s.split("        ")[1].split(" ")[7]);
		    		
		    	i++;
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return delta;
	}
	/**
	 * @param args
	 */
	
	
	public static void cassandraJMX(String opType, String key, HashMap<String,ByteIterator> result,long en, long st, String readProp,int threadcount){
		BufferedWriter br = null,br_syn = null;
		int res = 0;
		JMXServiceURL url=null;
		JMXConnector jmxc = null;
		String arffStr = "";
		MBeanServerConnection mbsc = null;
		try {
			 
			String filename_syn= "/tmp/JmxSyn.log";
			File file_syn = new File(filename_syn);
			while(file_syn.exists() && !tail(file_syn).equalsIgnoreCase("stop"))			{	
					Thread.sleep(1000000);
			}
			br_syn = new BufferedWriter(new FileWriter(filename_syn));
		    	br_syn.append("start\n");
			url = new JMXServiceURL("service:jmx:rmi://ec2-54-191-53-4.us-west-2.compute.amazonaws.com:9999/jndi/rmi://ec2-54-191-53-4.us-west-2.compute.amazonaws.com:9999/jmxrmi");

			jmxc = JMXConnectorFactory.connect(url, null);
			arffStr = arffStr + opType  +",";
			mbsc = 
					jmxc.getMBeanServerConnection();
			ObjectName mbeanName = new ObjectName("org.apache.cassandra.db:type=StorageProxy");
			mbeanName = new ObjectName("org.apache.cassandra.db:type=StorageService");
			StorageServiceMBean mbeanStorageService = JMX.newMBeanProxy(mbsc, mbeanName, 
					StorageServiceMBean.class);
			
			arffStr = arffStr + ((int)(en-st)) +",";
			
			arffStr = arffStr + 
					mbeanStorageService.getStreamThroughputMbPerSec();	
			
			final OperatingSystemMXBean myOsBean=  
		            ManagementFactory.getOperatingSystemMXBean();
			double load = myOsBean.getSystemLoadAverage();
			
			arffStr = arffStr + "," + load + ",";
			String strLog = "";
			String filename= "/tmp/172.31.19.158.log";
			
			File f_log= new File(filename);
			OutputStreamWriter writer = null;
                if(!f_log.exists())
                        f_log.createNewFile(); //the true will append the new data

                writer = new OutputStreamWriter(
                        new FileOutputStream(f_log, true), "UTF-8");
                br = new BufferedWriter(writer);

		    strLog = strLog + key + "\n" + res + "\n";
		    strLog = strLog + st + "\n";
		    strLog = strLog + en + "\n";
		    int op =0;
		    if(opType=="read")
			op = 0;
		    else if(opType=="insert")
			op = 1;
		    else if(opType=="update")
			op = 2;
		    else if(opType=="scan")
			op = 3;
		    else
			op = 4;
		    strLog = strLog + op + "\n"; 
		    synchronized(br){
		    br.append(strLog);
		    }
			br.close();
			
			Process proc = Runtime.getRuntime().exec("/usr/bin/java -jar /home/ubuntu/YCSB/Analysis.jar /tmp/ /tmp/");
			File file = new File("/tmp/plot_gamma_perkey.txt");
			while(!file.exists())
			{
				
			}
			FileReader reader = new FileReader(file);
		    BufferedReader buffReader = new BufferedReader(reader);
		    String s;
		    double delta = 0,totKey = 0,avgDelta = 0;
		    while((s = buffReader.readLine()) != null && !"".equalsIgnoreCase(s) && (s).contains("\t") && (s).split("\t")!=null && (s).split("\t").length>0  && (s).split("\t").length>1){
		    	if(Double.parseDouble(s.split("\t")[1])>0)
		    		delta = delta + Double.parseDouble(s.split("\t")[1]);
		    }
		    file = new File("/tmp/plot_gamma_perkey_proportion.txt");
			while(!file.exists())
			{
				
			}
			reader = new FileReader(file);
		    buffReader = new BufferedReader(reader);
		    if((s = buffReader.readLine()) != null && !"".equalsIgnoreCase(s) && (s).contains("\t") && (s).split("\t")!=null && (s).split("\t").length>0  && (s).split("\t").length>1){
		    	totKey = totKey + Double.parseDouble(s.split("\t")[0]);
		    }
		    
		    if(totKey!=0)
		    	avgDelta = delta / totKey;
		    arffStr = arffStr + avgDelta+ ",";
		   
		    String clevel = null;
		    File f = new File("/tmp/consistencyLevel.txt");
		    if(!f.exists())
		    	getRandomConsistencyLevel(opType);
		    BufferedReader brCon = new BufferedReader(new FileReader(f));
		    String line = brCon.readLine();
		    if(line==null || "".equalsIgnoreCase(line) || "".equalsIgnoreCase(line.trim()) || "".equalsIgnoreCase(line.toString().trim()))
		    {
		    	getRandomConsistencyLevel(opType);
		    	brCon = new BufferedReader(new FileReader(f));
			    line = brCon.readLine();
		    }
		    clevel = line.toString().trim();
		    
		    brCon.close();
	        f.delete();
	        
			arffStr = arffStr + Double.toString(parseNet())+",";
			
			arffStr = arffStr + Double.toString(getPacketCount())+",";
			arffStr = arffStr + Integer.toString(threadcount)+",";
			
			arffStr = arffStr + readProp.trim()+",";
			
			arffStr = arffStr +clevel+" \n";
			JMXLogger.generateLog(arffStr,avgDelta);
		   	br_syn.append("stop\n");
			br_syn.close();
			//}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 	
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
	}
		 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();}
	
}	
	
	 public static void getRandomConsistencyLevel(String opType)
	    {
	    	int type = 1;
	    	if(opType=="read")
	    		type=1;
	    	else if(opType=="insert")
	    		type=3;
	    	else if(opType=="update")
	    		type=3;
	    	else if(opType=="delete")
	    		type=4;
	    	else if(opType=="scan")
	    		type=2;
	    	int Min = 0, Max = 2;
	    	int level = 0;
	    	if(type==3)
	    		level = 3;
	    	else
	    		level = Min + (int)(Math.random() * ((Max - Min) + 1));
	    	
		    	if(level==0)
		    	{
		    		writeConsistencyLevel("ALL");
		    	}
		    	else if(level==1)
		    	{
		    		writeConsistencyLevel("QUORUM");
		    	}
		    	else
		    	{
		    		if(type==3)
		    		{
		    			writeConsistencyLevel("ANY");
		    		}
		    		else
		    		{
		    			writeConsistencyLevel("ONE");
		    		}
		    	}
	    }
	
public static String tail( File file ) {
    RandomAccessFile fileHandler = null;
    try {
        fileHandler = new RandomAccessFile( file, "r" );
        long fileLength = fileHandler.length() - 1;
        StringBuilder sb = new StringBuilder();

        for(long filePointer = fileLength; filePointer != -1; filePointer--){
            fileHandler.seek( filePointer );
            int readByte = fileHandler.readByte();

            if( readByte == 0xA ) {
                if( filePointer == fileLength ) {
                    continue;
                }
                break;

            } else if( readByte == 0xD ) {
                if( filePointer == fileLength - 1 ) {
                    continue;
                }
                break;
            }

            sb.append( ( char ) readByte );
        }

        String lastLine = sb.reverse().toString();
        return lastLine;
    } catch( java.io.FileNotFoundException e ) {
        e.printStackTrace();
        return null;
    } catch( java.io.IOException e ) {
        e.printStackTrace();
        return null;
    } finally {
        if (fileHandler != null )
            try {
                fileHandler.close();
            } catch (IOException e) {
                /* ignore */
            }
    }
}
    
	public static void writeConsistencyLevel(String str){
			
	        File f = new File("/tmp/consistencyLevel.txt");
	        OutputStreamWriter writer = null;
	        BufferedWriter fbw = null;
	        try {
	        	if(f.exists())
	        		f.delete();
	            f.createNewFile();     
	            writer = new OutputStreamWriter(
	                     new FileOutputStream(f, false), "UTF-8");
	            fbw = new BufferedWriter(writer);
	            fbw.append(str);
	            fbw.flush();
	            
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        finally{
	        	try {
	        		fbw.flush();
					fbw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        	
		}
	
	public static void main(String[] args) {
		}

}
