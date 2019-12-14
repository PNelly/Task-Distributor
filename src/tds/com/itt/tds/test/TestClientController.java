package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.itt.tds.coordinator.*;
import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.comm.*;
import com.itt.tds.cfg.*;
import com.itt.tds.core.*;

public class TestClientController {
	
	private static ClientController clientController = new ClientController();

	@Test
	public void testQueueTask() throws RepositoryException {

		Client client = TestUtil.randomClient();

		Task task = TestUtil.randomTask(client.getId());

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			"127.0.0.1",
			9000,
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);

		request.addParameter("hostName", client.getHostName());
		request.addParameter("userName", client.getUserName());
		request.addParameter("taskName", task.getTaskName());
		request.addParameter("taskParameters", task.getTaskParameters());
		request.setData(task.getProgramBytes());

		request.setMethod("task-queue");

		TDSResponse response = clientController.processRequest(request);

		TDSResponse expected = TDSResponse.goodRequest(request);
		expected.setValue("taskId", response.getValue("taskId"));

		assertTrue(response.equals(expected));
	}

	@Test
	public void testQueryResult() throws RepositoryException {

		int clientId = Repositories.getClientRepository().add(
			TestUtil.randomClient()
		);

		int taskId = Repositories.getTaskRepository().add(
			TestUtil.randomTask(clientId)
		);

		Repositories.getTaskResultRepository().add(
			TestUtil.randomResult(taskId)
		);

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			"127.0.0.1",
			9000,
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);

		request.addParameter("taskId", taskId);

		request.setMethod("task-result");

		TDSResponse response = clientController.processRequest(request);

		assertEquals(response.getErrorCode(), 200);
		assertTrue(response.getStatus());
	}

	@Test
	public void testQueryStatus() throws RepositoryException {

		int clientId = Repositories.getClientRepository().add(
			TestUtil.randomClient()
		);

		int taskId = Repositories.getTaskRepository().add(
			TestUtil.randomTask(clientId)
		);		

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			"127.0.0.1",
			9000,
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);		

		request.addParameter("taskId", taskId);

		request.setMethod("task-status");

		TDSResponse response = clientController.processRequest(request);

		assertEquals(response.getErrorCode(), 200);
		assertTrue(response.getStatus());
	}

	@Test
	public void testNoMethod() {

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			"127.0.0.1",
			9000,
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);

		request.setMethod("NULL");

		TDSResponse response = clientController.processRequest(request);

		assertFalse(response.getStatus());
		assertEquals(400, response.getErrorCode());
		assertEquals("no such method", response.getErrorMessage());
	}
}