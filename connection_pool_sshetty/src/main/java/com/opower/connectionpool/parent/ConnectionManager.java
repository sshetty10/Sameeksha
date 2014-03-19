package com.opower.connectionpool.parent;

import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 *  
 * ConnectionManager is an abstract class . 
 * Createconnection() is declared as abstract to faciliate multiple DB implementations
 * 
 * Each poolname will have its own object which encompasses all the DB specific parameters and the Connection pool 
 * for that poolname
 */

public abstract class ConnectionManager {

	private String jdbcUrl;
	private String jdbcusername;
	private String jdbcpassword;
	//queue of Connections(Connection Pool) for each poolname
	public LinkedBlockingQueue<Connection> m_Connections = new LinkedBlockingQueue<Connection>();
	//Total number of new connections created for each pool
	public AtomicInteger m_NewConnectionsSql = new AtomicInteger(0);
	public String poolName;
	private int maxSize = 7;
	
	  public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	  
	  public String getJdbcUrl() {
			return jdbcUrl;
		}

		public void setJdbcUrl(String jdbcUrl) {
			this.jdbcUrl = jdbcUrl;
		}

		public String getJdbcusername() {
			return jdbcusername;
		}

		public void setJdbcusername(String jdbcusername) {
			this.jdbcusername = jdbcusername;
		}

		public String getJdbcpassword() {
			return jdbcpassword;
		}

		public void setJdbcpassword(String jdbcpassword) {
			this.jdbcpassword = jdbcpassword;
		}

		public int getMaxSize() {
			return maxSize;
		}

		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}
		
		/**
		 *@return java.sql.Connection object
		 * Each DB implementation will have its own implementation of createConnection
		 */
		public abstract Connection createConnection();	
		

}
