package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.itt.tds.coordinator.db.DBManager;
import java.sql.Connection;
import java.sql.SQLException;

public class TestDBManager {

	@Test
	public void testGetConnection() throws SQLException {

		/*
			Test to pass if no exception thrown
		*/

		assertNotNull(
			"connection failed, ensure db server is running", 
			DBManager.getConnection()
		);
	}

	@Test
	public void testCloseConnection() throws SQLException {

		/*
			Test to pass if no exception thrown
		*/

		DBManager.closeConnection(DBManager.getConnection());
	}

	@Test
	public void testSqlColumnList(){

		String[] columns  = {"col1", "col2", "col3"};
		String   expected = "( col1, col2, col3 )";

		assertEquals(expected, DBManager.sqlColumnList(columns));
	}

	@Test
	public void testSqlValuesList(){

		String 	expected 	= "( ?, ?, ? )";
		int 	values 		= 3;

		assertEquals(expected, DBManager.sqlValuesList(values));
	}

	@Test
	public void testSqlSetList(){

		String[] 	columns  = {"col1","col2","col3"};
		String 		expected = "col1 = ?, col2 = ?, col3 = ? ";

		assertEquals(expected, DBManager.sqlSetList(columns));
	}
}