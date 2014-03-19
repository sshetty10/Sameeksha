package com.opower.connectionpool.tests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.hamcrest.core.IsNot;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.opower.connectionpool.parentimpl.ConnectionPoolManager;
import com.opower.connectionpool.service.DbManager;
/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * Test class for ConnectionPool
 */

public class ConnectionPoolTest {
	
	private ConnectionPoolManager connPoolformysql = null;
	private static final Logger logger = Logger.getLogger(ConnectionPoolTest.class);

	@Before
	public void initialSetUp() {
		DbManager dbManager = new DbManager();
		dbManager.createConnectionPools("testConnectionPool.properties");
		connPoolformysql = ConnectionPoolManager.getInstance();
		logger.info("Started Testing ConnPoolMySqlImpl");
	}	
 
	
	@Test 
	public void testCreateConnectionTruePositive() {	
		
		Connection connect = connPoolformysql.createConnection("ds1");
		assertNotNull(connect);
	}
	
	@Test 
	public void testCreateConnectionNegative() {	
		Connection connect = connPoolformysql.createConnection("ds3");
		assertNull(connect);
	}
	
	@Test 
	public void testGetConnectionTruePositive() {
		Connection connect = connPoolformysql.getConnection("ds1");
		assertNotNull(connect);
	}
	
	@Test 
	public void testReleaseConnectionIfNull() throws SQLException {		 
		Connection connect = null;
		assertFalse(connPoolformysql.releaseConnection(connect,"ds1"));
	}
	
	@Test 
	public void testReleaseConnectionInvalidPoolName() throws SQLException {		 
		Connection connect = null;
		assertFalse(connPoolformysql.releaseConnection(connect,"ds3"));
	}
	
	@Test 
	public void testReleaseConnectionTruePositive() throws SQLException {		 
		Connection connect = connPoolformysql.createConnection("ds1");
		assertTrue(connPoolformysql.releaseConnection(connect,"ds1"));
	}
	
	@Test 
	public void purgeConnectionsTruePositive() {	
		assertEquals(connPoolformysql.purgeConnectionPool("ds1"),0);
	}
	
	@Test 
	public void purgeConnectionsNegative() {	
		assertEquals(connPoolformysql.purgeConnectionPool("ds3"),-1);
	}
	
	@Test 
	public void getStatisticsTruePositive() {	
		assertTrue(connPoolformysql.getStatistics("ds1"));
	}
	
	@Test 
	public void getStatisticsNegative() {	
		assertFalse(connPoolformysql.getStatistics("ds3"));
	}
	
	@AfterClass
	public static void tearDown()
	{
		logger.info("Inside ConnPoolMySqlImplTest.tearDown()");
	}
	

}
