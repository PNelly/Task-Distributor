package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.io.*;
import java.net.Socket;

import com.itt.tds.comm.*;
import com.itt.tds.coordinator.*;
import com.itt.tds.cfg.*;

public class TestCommunicationManager {

	@Test
	public void testStartup() {

		CommunicationManager commMgr = null;

		try {

			commMgr = new CommunicationManager();

			assertTrue(commMgr.openListener());

		} catch(IOException e) {

			fail("IOException");

		} finally {

			commMgr.stopListener();
		}
	}

	@Test
	public void testShutdown() {

		CommunicationManager commMgr = null;

		try {

			commMgr = new CommunicationManager();
			commMgr.openListener();

			assertTrue(commMgr.stopListener());

		} catch(IOException e) {

			fail("IOException");

		} finally {

			commMgr.stopListener();
		}
	}

	@Test
	public void testConnection() {

		CommunicationManager commMgr = null;

		Socket socket = null;

		try {

			commMgr = new CommunicationManager();
			commMgr.openListener();

			socket = new Socket(
				"127.0.0.1",
				TDSConfiguration.getCoordinatorPort()
			);

			assertTrue(socket.isConnected());

			socket.close();

		} catch(IOException e) {

			fail("IOException");

		} finally {

			commMgr.stopListener();
		}		
	}

	@Test
	public void testRequestToResponse() {

		TDSRequest  mockRequest  = TDSRequest.getMockRequest();
		TDSResponse mockResponse = TDSResponse.getMockResponse();

		String mockRequestJson  = TDSRequest.getMockRequestJson();
		String mockResponseJson = TDSResponse.getMockResponseJson();

		CommunicationManager 	commMgr = null;
		Socket 					socket 	= null;
		PrintWriter 			out 	= null;
		BufferedReader 			in 		= null;

		try {

			commMgr = new CommunicationManager(mockResponse);
			commMgr.openListener();

			socket = new Socket(
				"127.0.0.1",
				TDSConfiguration.getCoordinatorPort()
			);

			out = new PrintWriter(
					socket.getOutputStream(),
					true 
			);

			in 	= new BufferedReader(
					new InputStreamReader(
						socket.getInputStream()
					)
			);

			out.println(mockRequestJson);

			String commMgrResponse = in.readLine();

			assertTrue(commMgrResponse.equals(mockResponseJson));

			in.close();
			out.close();
			socket.close();

		} catch(IOException e) {

			fail("IOException");

		} finally {

			commMgr.stopListener();
		}
	}	
}	