package com.opower.connectionpool.parentimpl;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.opower.connectionpool.parent.ConnectionManager;
/**
 * @author Sameeksha Shetty

 * @version:1.0
 * @since: 2014-03-16
 * This implementation is for MySql database. Similar implemenations may be done for other DBs
 */

public class ConnectionManagerSqlImpl extends ConnectionManager {
	private static final Logger logger = Logger.getLogger(ConnectionManagerSqlImpl.class);
	/*Create Database Connection for Each Pool*/
	@Override
	public Connection createConnection()
	  {
		  try{
			  Class.forName("com.mysql.jdbc.Driver");		      
			  Connection connect = DriverManager.getConnection(getJdbcUrl(),getJdbcusername(),getJdbcpassword());
		      return connect;
		  }
		  catch(ClassNotFoundException ce){
			  logger.error(ce);
		  }
		  catch(SQLException sq){
			  logger.error(sq);
		  }
		  catch(Exception e){
			  e.printStackTrace();
			  logger.error(e);
		  }
		  return null;
	  }

}
