package com.itt.tds.coordinator.db;

import java.util.*;
import java.sql.*;
import com.itt.tds.cfg.TDSConfiguration;
import com.mysql.jdbc.Driver;

public class DBManager {

	/*
		Setup DBManager as a Singleton Class
	*/

	private static DBManager singleton = new DBManager();

	public static synchronized DBManager getInstance(){
		return singleton;
	}

	private DBManager() {
		
	}


	/*
		SQL String Constants
	*/

	public static final String sqlInsert 	= " INSERT INTO ";
	public static final String sqlValues 	= " VALUES ";
	public static final String sqlDelete 	= " DELETE FROM ";
	public static final String sqlWhere 	= " WHERE ";
	public static final String sqlAnd 		= " AND ";
	public static final String sqlNot 		= " NOT ";
	public static final String sqlExists 	= " EXISTS ";
	public static final String sqlUpdate 	= " UPDATE ";
	public static final String sqlSet 		= " SET ";
	public static final String sqlSelect 	= " SELECT ";
	public static final String sqlFrom 		= " FROM ";
	public static final String sqlSelectAll = " SELECT * FROM ";
	public static final String sqlCountRows = " SELECT COUNT(*) FROM ";
	public static final String sqlLimit 	= " LIMIT ";
	public static final String sqlMax 		= " MAX";

	/*
		Query Building Helper Methods
	*/

	public static String sqlColumnList(String[] columns){

		String str = "( ";

		for(int idx = 0; idx < columns.length; ++idx)
			str += (idx < columns.length-1)
				?  (columns[idx] + ", ")
				:  (columns[idx] + " )");

		return str;
	}

	public static String sqlValuesList(int numValues){

		String str = "( ";

		for(int idx = 0; idx < numValues; ++idx)
			str += (idx < numValues -1) 
				? "?, " 
				: "? )";

		return str;
	}

	public static String sqlSetList(String[] columns){

		String str = "";

		for(int idx = 0; idx < columns.length; ++idx)
			str += (idx < columns.length-1)
				?  (columns[idx] + " = ?, ")
				:  (columns[idx] + " = ? ");

		return str;
	}

	/*
		DBManager Methods
	*/

	public static void closeResource(AutoCloseable autoCloseable){

		if(autoCloseable == null)
			return;

		try {

			autoCloseable.close();

		} catch (Exception exception) {

			System.out.println("Resource could not be closed");
		}

		return;
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
								TDSConfiguration.getDBConnectionString(), 
								TDSConfiguration.getDBConnectionUser(),
								TDSConfiguration.getDBConnectionPassword()
							);
	}

	public static void closeConnection(Connection connection) {
		closeResource(connection);
	}
}