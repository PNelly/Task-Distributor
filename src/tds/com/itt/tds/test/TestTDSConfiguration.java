package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.itt.tds.cfg.TDSConfiguration;

public class TestTDSConfiguration {

	@Test
	public void testReadConfiguration(){

		assertEquals(
			"jdbc:mysql://localhost:3306/tds?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT",
			TDSConfiguration.getDBConnectionString());
		assertEquals(
			"root",
			TDSConfiguration.getDBConnectionUser());
		assertEquals("",
			TDSConfiguration.getDBConnectionPassword());
	}
}