package com.opower.connectionpool.parent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 *  
 *  ConnectionPool interface 
 */
public interface ConnectionPool {

    /**
     * Gets a connection from the connection pool.
     * 
     * @return a valid connection from the pool.
     */
 	public Connection getConnection();
    /**
     * Gets a connection from the connection pool.
     * 
     * @return a valid connection from the pool, pass the poolname to handle connection pooling for multiple pools
     */
	public Connection getConnection(String poolname);
    /**
     * Releases a connection back into the connection pool.
     * 
     * @param connection the connection to return to the pool
     * @param poolname to handle connection pooling for multiple pools
     * 
     */
	public boolean releaseConnection(Connection connect, String poolname) throws SQLException ;
    /**
     * Releases a connection back into the connection pool.
     * 
     * @param connection the connection to return to the pool
     * 
     */	
	public boolean releaseConnection(Connection connect) throws SQLException;
	
	/**
	 * Logs pool statistics for the given pool
	 * @param poolname
	 * @return true on successful logging
	 */
	public boolean getStatistics(String poolname);
	
	/**
	 * Creates a new connection for the given pool
	 * @param poolname
	 * @return Connection
	 */
	public Connection createConnection(String poolname);
	
	/**
	 * Resets the connection pool
	 * @param poolname
	 * 
	 */
	public int purgeConnectionPool(String poolname);
	
	/**
	 * Instantiates pools for all poolnames
	 * @param poolname
	 * @return
	 */
	public boolean createConnectionPools(List<ConnectionManager> cmgrList);
    
}
