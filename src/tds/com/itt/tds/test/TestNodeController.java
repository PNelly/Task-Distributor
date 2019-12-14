package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.itt.tds.coordinator.*;
import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.comm.*;
import com.itt.tds.cfg.*;
import com.itt.tds.core.*;

public class TestNodeController {
	
	private static NodeController nodeController = new NodeController();
	private static int testClientId = -1;
	private static int testTaskId 	= -1;

	@Before
	public void dependencies() throws RepositoryException {

		AbsClientRepository clientRepository
			= Repositories.getClientRepository();
		AbsTaskRepository taskRepository
			= Repositories.getTaskRepository();

		Client testClient = TestUtil.randomClient();

		testClientId = clientRepository.add(testClient);

		Task task = TestUtil.randomTask(testClientId);

		testTaskId = taskRepository.add(task);
	}

	@Test
	public void testRegisterNode() throws RepositoryException {

		Node node = TestUtil.randomNode();

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			node.getIp(),
			node.getPort(),
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);

		request.addParameter("nodeState",node.getState().toString());
		request.setMethod("node-add");

		TDSResponse response = nodeController.processRequest(request);

		TDSResponse expected = TDSResponse.goodRequest(request);

		assertTrue(response.equals(expected));
	}

	@Test
	public void testSaveResult() {

		TaskResult taskResult = TestUtil.randomResult(testTaskId);

		TDSRequest request = new TDSRequest(
			TDSConfiguration.getProtocolFormat(),
			TDSConfiguration.getProtocolVersion(),
			"127.0.0.1",
			9000,
			TDSConfiguration.getCoordinatorIp(),
			TDSConfiguration.getCoordinatorPort()
		);

		request.addParameter("errorCode", taskResult.errorCode);
		request.addParameter("errorMessage", taskResult.errorMessage);
		request.addParameter("taskId", taskResult.taskId);
		request.addParameter("taskOutcome", taskResult.taskOutcome.toString());
		request.setData(taskResult.resultBuffer);
		request.setMethod("node-result");

		TDSResponse response = nodeController.processRequest(request);

		TDSResponse expected = TDSResponse.goodRequest(request);

		assertTrue(response.equals(expected));
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

		TDSResponse response = nodeController.processRequest(request);

		assertFalse(response.getStatus());
		assertEquals(response.getErrorCode(), 400);
		assertEquals(response.getErrorMessage(), "no such method");
	}
}