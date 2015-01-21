package com.yahoo.ycsb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import com.yahoo.ycsb.JMXClient;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.*;
import weka.core.converters.ConverterUtils;

public class JMXLogger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void writeToTrainingFile(BufferedWriter fw,String str){
		try {
			fw.append("@relation consistency");
			fw.append("\n@attribute "+JMXClient.attr20+ " STRING" );
			fw.append("\n@attribute "+JMXClient.attr1+ " NUMERIC" );
			
			fw.write("\n@attribute "+JMXClient.attr13+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr14+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr15+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr16+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr17+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr18+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr19+ " NUMERIC" );
			fw.append("\n@attribute class {ANY,ONE,TWO,THREE,LOCAL_QUORUM,EACH_QUORUM,QUORUM,ALL}");
			
			fw.append("\n@data\n");
			fw.append(str+"\n");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//appends the string to the file
		
	}
	
	public static void generateLog(String str,double avgDelta){
		
        File f = new File("/tmp/training_data.arff");
        OutputStreamWriter writer = null;
        BufferedWriter fbw = null;
        try {
        	if(!f.exists()) {
        		f.createNewFile(); //the true will append the new data
                
                writer = new OutputStreamWriter(
                        new FileOutputStream(f, true), "UTF-8");
                fbw = new BufferedWriter(writer);
                
        		writeToTrainingFile(fbw,str);
                fbw.flush();
                
            }
        	else{
        		 writer = new OutputStreamWriter(
                         new FileOutputStream(f, true), "UTF-8");
                 fbw = new BufferedWriter(writer);
                 fbw.append(str);
                 fbw.flush();
        	
        	}
       
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
        	try {
        		fbw.flush();
				fbw.flush();
				fbw.close();
				fbw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
	}

}
