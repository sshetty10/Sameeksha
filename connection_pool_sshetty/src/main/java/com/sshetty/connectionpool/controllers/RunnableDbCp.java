package com.sshetty.connectionpool.controllers;


/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * 
 * Implements Multithreading request generation to the DB. 
 * Calls DbconnectPool1() and DbconnectPool2() 
 * 
 */

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.sshetty.connectionpool.service.DbManager;

public class RunnableDbCp implements Runnable {
	Connection connect = null;
	DbManager dbManager = new DbManager();
	   private static final Logger logger = Logger.getLogger(RunnableDbCp.class);
	   private Thread t;
	   private String threadName;
	   
	 public RunnableDbCp(String name)
	 {
	       threadName = name;
	 }

	 public void run() 
	 {
	      try {
		         for(int i = 3; i > 0; i--) {
				    try {
				    	if(Thread.currentThread().getName().equals("Thread-3") ){
				    		DbconnectPool1();
				    	}
				    	else{
				    		DbconnectPool1();
				    	}
					} catch (Exception e) {
						logger.error(e);
					}
		            // Let the thread sleep for a while.
		            Thread.sleep(10);
	         }
	     } catch (InterruptedException e) {
	         logger.error(e);
	     } 
	}
	
	 public void start ()
	 {
	      if (t == null)
	      {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	 }
	 
	 /**
		 * Method to run queries for ds1
		 */
		public void DbconnectPool1(){
			try
			{
				connect = dbManager.getConnection("ds1");
				if(connect!=null){
						//Queries for ds1
				}
		    } catch (Exception e) {
		      logger.error(e);
		    } finally {
		      dbManager.releaseConnection(connect, "ds1");	   
		    }
		}
		
		/**
		 * Method to run queries for ds2
		 */
		
		public void DbconnectPool2(){
			try
			{
				connect = dbManager.getConnection("ds2");
				if(connect!=null){
					//Queries for ds2
				}
		    } catch (Exception e) {
		      logger.error(e);
		    } finally {
		      dbManager.releaseConnection(connect, "ds2");
		    }
		}

}
