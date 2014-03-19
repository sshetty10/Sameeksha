package com.opower.connectionpool.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.opower.connectionpool.parent.ConnectionManager;
import com.opower.connectionpool.parent.ConnectionPool;
import com.opower.connectionpool.parentimpl.ConnectionManagerSqlImpl;
import com.opower.connectionpool.parentimpl.ConnectionPoolManager;

/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * 
 * DbManager is used by the controllers to talk the the data layer components.
 * Initialises the DbComponents using Properties
 * Implements the getting, releasing and purging of connection calls 
 * Implements the retry logic
 * 
 */
public class DbManager {
	private static final Logger logger = Logger.getLogger(DbManager.class);
	private ConnectionPool pool;
	private static Properties dbprop;
	private int numOfRetries=3;


	public DbManager(){
		dbprop= new Properties();
		setPool(ConnectionPoolManager.getInstance());
	}
	public void setPool(ConnectionPool pool){
		this.pool = pool;
	}

	public Connection getConnection(String poolname){
		try{
			Connection connect = pool.getConnection(poolname);			
			if(connect==null){
				AtomicInteger i = new AtomicInteger(0);
				while(i.get()<numOfRetries && connect==null)
				{
					Thread.sleep(10);
					i.incrementAndGet();
					logger.info("Retry attempt : " + i + " for :" + Thread.currentThread().getId());
					connect = pool.getConnection(poolname);
				}
			}
			return connect;
		}catch(Exception e){
			logger.error(e);
		}
		return null;

	}


	public boolean releaseConnection(Connection connect, String poolname)
	{
		boolean releaseConn = false;
		try{
			if(connect!=null){
				releaseConn = pool.releaseConnection(connect,poolname);
				if(!releaseConn){
					logger.error("Error!!Connection could not be released back to the pool.");
				}
			}
		}
		catch(Exception e){
			logger.error(e);
		}
		return releaseConn;
	}

	public int purgeConnectionPool(String poolname)
	{
		int purged = pool.purgeConnectionPool(poolname);
		if(purged == 0)
			pool.getStatistics(poolname);
		else
			logger.debug("Connection Pool for "+poolname + " could not be Purged!!");
		return purged;

	}

	public boolean createConnectionPools(String propertyFileName)
	{
		try{
			List<ConnectionManager> cmgrList = new ArrayList<ConnectionManager>();
			dbprop = new Properties();
			loadDbProperties(propertyFileName);
			String numOfRetries = (String) dbprop.get("numofRetries");
			if(StringUtils.isNotEmpty(numOfRetries) && StringUtils.isNumeric(numOfRetries)){
				setNumOfRetries(Integer.parseInt(numOfRetries));
			}
			String poolnames = (String) dbprop.get("poolnames");
			if(StringUtils.isNotEmpty(poolnames)){
				StringTokenizer pnamesTkns = new StringTokenizer(poolnames, ",");
				while(pnamesTkns.hasMoreTokens()){
					String poolname = pnamesTkns.nextToken();
					if(StringUtils.isNotEmpty(poolname) && StringUtils.isAlphanumeric(poolname)){
						ConnectionManager cmgr = fetchMySqlDbParams(poolname, dbprop);
						if(cmgr!=null){
							cmgr.setPoolName(poolname);
							cmgrList.add(cmgr);
						}
					}
				}
			}
			if(cmgrList.size()>0)
				return pool.createConnectionPools(cmgrList);
			else
				return false;
		}
		catch(Exception e){
			logger.error(e);
			return false;
		}
	}


	private static void loadDbProperties(String propertyName){
		try{
			dbprop.load(ClassLoader.getSystemResourceAsStream(propertyName));
		}
		catch(Exception e){
			logger.error("Error in loading the property file" + propertyName);
			logger.error(e);
		}
	}

	/*
	 * Fetch DBparameters from the Database and create a ConnectionManager object for  each pool
	 */
	private ConnectionManager fetchMySqlDbParams(String poolname, Properties dbprop){
		ConnectionManager cmgr = new ConnectionManagerSqlImpl();

		//Set the jdbcUrl
		StringBuffer sb = new StringBuffer();
		sb.append(poolname);
		sb.append(".jdbc.url");
		String jdbcUrl=(String) dbprop.getProperty(sb.toString());
		if(StringUtils.isNotEmpty(jdbcUrl)){
			cmgr.setJdbcUrl(jdbcUrl);			 
		}
		else{
			return null; // if jdbc url is empty -> invalid input
		}
		sb.setLength(0);

		//set the jdbc username
		sb.append(poolname);
		sb.append(".jdbc.username");
		String jdbcUsername=(String) dbprop.getProperty(sb.toString());
		if(StringUtils.isNotEmpty(jdbcUsername)){
			cmgr.setJdbcusername(jdbcUsername);			 
		}
		else{
			cmgr.setJdbcusername("");
		}
		sb.setLength(0);


		//set the jdbc password
		sb.append(poolname);
		sb.append(".jdbc.password");
		String jdbcPassword=(String) dbprop.getProperty(sb.toString());
		if(StringUtils.isNotEmpty(jdbcPassword)){
			cmgr.setJdbcpassword(jdbcPassword);			 
		}
		else{
			cmgr.setJdbcpassword("");
		}
		sb.setLength(0);

		//Set maximum allowed connections for a poolname
		sb.append(poolname);
		sb.append(".maxConnections");
		String maxConn =(String) dbprop.getProperty(sb.toString());
		if(StringUtils.isNotEmpty(maxConn) && StringUtils.isNumeric(maxConn)){
			cmgr.setMaxSize(Integer.parseInt(maxConn));			 
		}
		//else default is set to 7
		return cmgr;

	}

	public void setNumOfRetries(int numOfRetries) {
		this.numOfRetries = numOfRetries;
	}
}
