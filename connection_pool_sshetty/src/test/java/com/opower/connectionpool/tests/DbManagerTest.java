package com.opower.connectionpool.tests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.opower.connectionpool.parent.ConnectionManager;
import com.opower.connectionpool.parent.ConnectionPool;
import com.opower.connectionpool.parentimpl.ConnectionPoolManager;
import com.opower.connectionpool.service.DbManager;


/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * Test class for the DbManager
 */
public class DbManagerTest {
	
	private ConnectionPool mockCpool; 
	private ConnectionManager mockCMgr;
	private static final Logger logger = Logger.getLogger(DbManagerTest.class);
	private Connection connect;
	private DbManager dbManager;
	@Before
	public void setUp() throws Exception {
		logger.debug("Start DbManager Test");
		dbManager = new DbManager();
		mockCpool = EasyMock.createMock(ConnectionPool.class);	
		mockCMgr = EasyMock.createMock(ConnectionManager.class);	
		connect = ConnectionPoolManager.getInstance().createConnection("ds1");	
	}
	
	@Test
	public void testCreateConnectionPoolsNegative() {
		DbManager dbManager1 = new DbManager();
		assertFalse(dbManager1.createConnectionPools("testConnectionPoolNegative.properties"));
	}
	
	@Test
	public void testCreateConnectionPools() {
		DbManager dbManager1 = new DbManager();
		assertTrue(dbManager1.createConnectionPools("testConnectionPool.properties"));
	}
	



	@Test
	public void testGetConnectionTruePositive() {
		dbManager.setPool(mockCpool);
		Connection mockConn = EasyMock.createMock(Connection.class);
		EasyMock.expect(mockCpool.getConnection("ds1")).andReturn(mockConn);
		EasyMock.replay(mockCpool);
		assertNotNull(dbManager.getConnection("ds1"));
	}
	
	@Test
	public void testGetConnectionNegative() {
		dbManager.setPool(mockCpool);
		dbManager.setNumOfRetries(0);
		EasyMock.expect(mockCpool.getConnection("ds1")).andReturn(null);
		EasyMock.replay(mockCpool);
		assertNull(dbManager.getConnection("ds1"));
	}
	
	@Test
	public void testReleaseConnectionTruePositive() throws SQLException {
		dbManager.setPool(mockCpool);
		Connection mockConn = EasyMock.createMock(Connection.class);
		EasyMock.expect(mockCpool.releaseConnection(mockConn,"ds1")).andReturn(true);
		EasyMock.replay(mockCpool);
		assertTrue(dbManager.releaseConnection(mockConn,"ds1"));
	}
	
	@Test
	public void testReleaseConnectionNegative() throws SQLException {
		dbManager.setPool(mockCpool);
		Connection mockConn = EasyMock.createMock(Connection.class);
		EasyMock.expect(mockCpool.releaseConnection(mockConn,"ds1")).andReturn(false);
		EasyMock.replay(mockCpool);
		assertFalse(dbManager.releaseConnection(mockConn,"ds1"));
	}
	
	@Test
	public void testReleaseConnectionSqlException() throws SQLException {
		dbManager.setPool(mockCpool);
		Connection mockConn = EasyMock.createMock(Connection.class);
		EasyMock.expect(mockCpool.releaseConnection(mockConn,"ds1")).andThrow(new SQLException());
		EasyMock.replay(mockCpool);
		assertFalse(dbManager.releaseConnection(mockConn,"ds1"));
	}
	
	
	@After
	public void tearDown() throws Exception {
		logger.debug("End DbManager Test");
	}
	
	


}
