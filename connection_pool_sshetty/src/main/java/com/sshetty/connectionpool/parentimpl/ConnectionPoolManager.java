package com.sshetty.connectionpool.parentimpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sshetty.connectionpool.parent.ConnectionManager;
import com.sshetty.connectionpool.parent.ConnectionPool;


/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * The Connection Pool Manager provides a Connection Pooling implementation logic for multiple pools of different DBs.
 * This class implements a singleton design pattern to promote multithreaded usage
 */
public class ConnectionPoolManager implements ConnectionPool{


	private static final Logger logger = Logger.getLogger(ConnectionPoolManager.class);
	private static ConnectionPoolManager instance = null;
	private static ConcurrentHashMap<String,ConnectionManager> connectionMap= new ConcurrentHashMap<String,ConnectionManager>();
	private static final String defaultPoolName = "ds1";

	public static ConnectionPoolManager getInstance(){
		if(instance == null) {
			instance = new ConnectionPoolManager();
		}
		return instance;
	}

	//add a new connection to the pool if pool maximum size requirements permit	 
	private int addConnection(String poolname) throws ClassNotFoundException, SQLException, InterruptedException {
		//if number of floating connections + 1(new connection we are bout to add) < max size add it
		synchronized(connectionMap.get(poolname).m_NewConnectionsSql){				  
			if(connectionMap.get(poolname).m_NewConnectionsSql.get() < connectionMap.get(poolname).getMaxSize()){
				Connection connect = connectionMap.get(poolname).createConnection();
				connectionMap.get(poolname).m_Connections.put(connect);		  		
				addNewConnectionCnt(poolname);
				logger.debug("Added a New connection for " + poolname + " :" + connectionMap.get(poolname).m_NewConnectionsSql.get() + " pool size:" + connectionMap.get(poolname).m_Connections.size());
			}
		}		  
		return connectionMap.get(poolname).m_Connections.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#getConnection(java.lang.String)
	 * Get a connection from the pool, if not available and poolsize is lesser than the requirements add a new connection to the pool.
	 * Poll the connection from the pool
	 */
	public Connection getConnection(String poolname)
	{
		Connection connect = null;
		try{
			if(connectionMap.get(poolname)==null)
				return null;
			if (isEmpty(poolname)) {
				int flag = addConnection(poolname);
				if(flag == -1)
				{
					return null;
				}
			}
			connect = poll(poolname);			
			if (connect != null) {
				return connect;
			}
			else{
				logger.info("ThreadId:" + Thread.currentThread().getId() +" Sorry all connections are busy for " + poolname);
			}

			return null;
		}
		catch(Exception e)
		{
			logger.error(e);			
			return null;
		}
		finally{
			getStatistics(poolname);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#releaseConnection(java.sql.Connection, java.lang.String)
	 * Release The given connection "connect" back to the connection pool for "poolname"
	 */

	public boolean releaseConnection(Connection connect, String poolname) throws SQLException
	{ 
		try{
			if(connectionMap.get(poolname)==null)
				return false;
			if(connect!=null && connect.isValid(1) ){
				connectionMap.get(poolname).m_Connections.put(connect);
				logger.debug("ThreadId:" + Thread.currentThread().getId() +" !!!!!!!!!connection pool size after releasing !!!!!!! for " + poolname + " :" + connectionMap.get(poolname).m_Connections.size());
				return true;
			}
			else{
				return false;
			}
		}
		catch(InterruptedException ex){
			logger.error(ex);
			return false;
		}
		finally{
			getStatistics(poolname);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#purgeConnectionPool(java.lang.String)
	 * Call this function only after releasing all the connections back to the pool(releaseConnection)
	 */

	public int purgeConnectionPool(String poolname)
	{
		try{
			if(connectionMap.get(poolname)==null)
				return -1;
			int poolsize = connectionMap.get(poolname).m_Connections.size();
			while(poolsize>0)
			{
				Connection connect = connectionMap.get(poolname).m_Connections.poll();
				if(connect!=null)
					connect.close();
				poolsize--;
			}
			connectionMap.get(poolname).m_Connections.clear();
			connectionMap.get(poolname).m_NewConnectionsSql.set(0);
			return connectionMap.get(poolname).m_Connections.size();
		}
		catch(Exception ex){
			logger.error(ex);
			return -1;
		}
	}

	/*
	 * Poll for a connection from the pool
	 * 
	 */

	private Connection poll(String poolname) throws ClassNotFoundException, SQLException
	{
		Connection connect = connectionMap.get(poolname).m_Connections.poll();
		logger.debug("ThreadId:" + Thread.currentThread().getId() +" **********connection pool size after polling ******* for " + poolname + " :" + connectionMap.get(poolname).m_Connections.size());
		return connect;
	}



	private boolean isEmpty(String poolname) {
		logger.debug("Check for available connections in the pool : " + connectionMap.get(poolname).m_Connections.isEmpty());
		return connectionMap.get(poolname).m_Connections.isEmpty();
	}

	private void addNewConnectionCnt(String poolname) {
		connectionMap.get(poolname).m_NewConnectionsSql.addAndGet(1);
	}



	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#getStatistics(java.lang.String)
	 * logs the statistics for monitioring
	 * This is synchronized for this assignment to facilitate log view , not needed in live environments.
	 */
	public synchronized boolean getStatistics(String poolname) 
	{
		try{
			if(connectionMap.get(poolname)==null)
				return false;
			logger.info("ThreadId:" + Thread.currentThread().getId() +" Connection pool size for " + poolname + " is :" + connectionMap.get(poolname).m_Connections.size() );
			logger.info("ThreadId:" + Thread.currentThread().getId() +" Connection Created New for " + poolname + " is :" + connectionMap.get(poolname).m_NewConnectionsSql.get());	
			logger.info("ThreadId:" + Thread.currentThread().getId() +" Connection Used for " + poolname + " is :" + (connectionMap.get(poolname).m_NewConnectionsSql.get() -connectionMap.get(poolname).m_Connections.size()) );
			return true;
		}
		catch(Exception e){
			logger.error(e);
			return false;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#createConnection(java.lang.String)
	 * Creates a new connection
	 */
	public Connection createConnection(String poolname)
	{
		if(connectionMap.get(poolname)!=null){
			return connectionMap.get(poolname).createConnection();
		}
		else 
			return null;
	}



	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#createConnectionPools(java.util.List)
	 * Initializing all the pools during startup
	 */
	public boolean createConnectionPools(List<ConnectionManager> cmgrList)
	{
		try
		{
			connectionMap.clear();
			Iterator<ConnectionManager> it = cmgrList.iterator();
			while(it.hasNext()){
				ConnectionManager cm = (ConnectionManager) it.next();
				connectionMap.put(cm.getPoolName(), cm);
			}
			logger.debug("Pools created :" + connectionMap.size());
			return true;
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#getConnection()
	 * get a connection from the pool
	 * @param: defaultPoolName = ds1
	 */
	public Connection getConnection() {
		return getConnection(defaultPoolName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.sshetty.connectionpool.ConnectionPool#releaseConnection(java.sql.Connection)
	 * 
	 * release connection back into the pool 
	 * @param : Connection
	 * defaultpoolname = ds1
	 */
	public boolean releaseConnection(Connection connect) throws SQLException{
		return releaseConnection(connect,defaultPoolName);
	}

}