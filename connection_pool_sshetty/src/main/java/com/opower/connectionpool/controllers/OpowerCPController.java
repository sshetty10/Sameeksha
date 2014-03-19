package com.opower.connectionpool.controllers;

import java.sql.Connection;
import org.apache.log4j.Logger;
import com.opower.connectionpool.service.DbManager;


/**
 * @author Sameeksha Shetty
 * Main class
 * Initialises the Connection pools
 * Starts the multithreaded application to query the Database
 * @version:1.0
 * @since: 2014-03-16
 */
public class OpowerCPController {
	private static final Logger logger = Logger.getLogger(OpowerCPController.class);
	
	
	/**
	 * @param args
	 * Initializes connection pools
	 * Multithreaded requests to DB
	 */
	public static void main(String[] args) {
		String propertyFileName = "testConnectionPool.properties";
		DbManager dbmngr = new DbManager();
		if(dbmngr.createConnectionPools(propertyFileName)){
			for(int i=0;i<10;i++){
				RunnableDbCp R1 = new RunnableDbCp( "Thread-"+i);
			     R1.start();
			}			
		}
		else{
			logger.error("Connection pool initializations failed!!Please check configurations");
		}
		
	}

}
