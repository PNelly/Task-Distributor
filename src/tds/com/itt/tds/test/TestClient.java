package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.*;

import com.itt.tds.comm.*;
import com.itt.tds.coordinator.CommunicationManager;
import com.itt.tds.cfg.*;
import com.itt.tds.client.*;
import com.itt.tds.core.*;

public class TestClient {
	
	private static CommunicationManager commMgr
		= new CommunicationManager();

	@Before 
	public void initialize() throws IOException {

		commMgr.openListener();
	}

	@After
	public void shutDown() throws IOException {

		commMgr.stopListener();
	}

	@Test
	public void testRequest() throws CommunicationException, TDSStartupException {

		commMgr.setMockResponse(TDSResponse.getMockResponse());

		Client client = new Client();

		TDSResponse compareResponse = TDSResponse.getMockResponse();
		TDSRequest  inRequest 		= TDSRequest.getMockRequest();

		TDSResponse outResponse 	= client.sendRequest(inRequest);

		assertTrue(compareResponse.equals(outResponse));
	}

	@Test
	public void testQueueTask() throws 
		CommunicationException, 
		TDSEnumException, 
		TDSStartupException {

		commMgr.unsetMockResponse();

		Client client = new Client();

		Task task = TestUtil.randomTask(-1);

		int taskId = client.queueTask(task);

		assertTrue(taskId >= 0);
	}

	@Test
	public void testQueryResult() throws 
		CommunicationException, 
		TDSEnumException, 
		TDSStartupException {

		commMgr.unsetMockResponse();

		Client client = new Client();

		Task task = TestUtil.randomTask(-1);

		int taskId = client.queueTask(task);

		assertTrue(taskId >= 0);

		TaskResult result = client.queryResult(taskId);

		/*
			task does not execute, so no result
			expected method return value of null
		*/

		assertNull(result);
	}

	@Test
	public void testQueryStatus() throws 
		CommunicationException, 
		TDSEnumException, 
		TDSStartupException {

		commMgr.unsetMockResponse();

		Client client = new Client();

		Task task = TestUtil.randomTask(-1);

		int taskId = client.queueTask(task);

		assertTrue(taskId >= 0);

		TaskState state = client.queryStatus(taskId);

		assertEquals(TaskState.PENDING, state);
	}
}