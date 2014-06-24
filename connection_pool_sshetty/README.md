#Connection Pooling
The Connection pooling application archive "connection_pool_sshetty.zip" has my Java implementations of a thread safe connection pooling real world solution.
The project architecture,features and working are present in this document.

## Instructions
1. Project Architecture
	a. src/main/java
		i. com.opower.connectionpool.controllers
			-OpowerCpController.java (Main class)
			-RunnableDbCp.java (Multithreaded Implementation of the main class)
		ii.com.opower.connectionpool.parent
			-ConnectionPool.java (Interface) - provides connection pooling methods interface
			-ConnectionManager.java (Abstract class) - provides DB paramaters and DB connection functionality
		iii.com.opower.connectionpool.parentImpl
			-ConnectionPoolManager.java (Implementation class for ConnectionPool.java)
			-ConnectionManagerSqlImpl.java (Child class of ConnectionManager, implements the abstract method createConnection )
		iv.com.opower.connectionpool.service
			-DbManager.java (Implements all the dao functionalities, called from the main method.)
	b. 	src/main/resources
		i.  log4j.properties-configurable paramters for application logging
		ii. testConnectionPool.properties-configurable DB/connection pooling 
			-poolnames(comma separated names of dbpools) : ds1,ds2
			-${poolname}.jdbc.url : ds1.jdbc.url - (jdbcurl)
			-${poolname}.jdbc.url : ds1.jdbc.username - (jdbcusername)
			-${poolname}.jdbc.url : ds1.jdbc.password - (jdbcpassword)
			-${poolname}.maxConnections(maximum pool size , default is 7)
			-numofRetries(number of retries to fetch connection, default is 3)
	c.src/test/java
		i.ConnectionPoolTest.java(ConnectionPool test class)
		ii.DbManagerTest.java(DbManager Test class)
	d. src/test/resources
		i.  log4j.properties-configurable paramters for application logging
		ii. testConnectionPool.properties-configurable DB/connection pooling 
		iii. testConnectionPoolNegative.properties-configurable DB/connection pooling for negative test cases of db params
2. Features:
	a. Supports multiple pools
	b. Supports different DB types- changes in the pom to get the DB driver and a simple Implementation class like ConnectionManagerSqlImpl
	c. log4j capabilities
	d. Prints the statistics of poolsize, connections used after every poll and release
	e. Purge the connection pool for any give poolName	
	f. Number of retries, Maximum size of the connection pools are configurable
	e. Easymock and Junit based test cases
	f. pom upgraded and Junits configured to be a part of the mvn install. Surefire reports generated. Facilitates CI. 	
3. Working
	a. Main method triggers initializing of all the dbpools
	b. Main method then spawns multiple threads
	c. Each thread makes dbconnection requests 3 times in RunnableDbcp.java
	d. Each call tries to get a DB connection from the connectionPool
	e. If not available , and the total number of connections already floating is less than the maximum number of connections that can be made , then create a new connection. Else wait for a connection to be available by retrying the getconnection method "numofretries" times.
	f. After connection is available use it and then release it back to the pool
	g. If the connection is valid only then it will be released to the pool.
	h. print pool statistics after every f and every g.
Maven goals:
mvn clean compile
mvn test
mvn install
