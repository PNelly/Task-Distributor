package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.db.DBManager;
import com.itt.tds.coordinator.Client;
import com.itt.tds.core.*;

public class TestTaskResultRepository {

	private static AbsTaskResultRepository resultRepository 
		= Repositories.getTaskResultRepository();

	private static AbsClientRepository clientRepository 
		= Repositories.getClientRepository();

	private static AbsTaskRepository taskRepository 
		= Repositories.getTaskRepository();

	private static int testClientId = -1;
	private static int testTaskId 	= -1;

	@Before
	public void dependencies() throws RepositoryException {

		// create a new reference test client

		String hostName = TestUtil.randomString(16);
		String userName = TestUtil.randomString(16);

		while(clientRepository.resolveClientId(
			hostName,
			userName
			) > 0
		){

			hostName = TestUtil.randomString(16);
			userName = TestUtil.randomString(16);
		}

		Client testClient = new Client(hostName, userName);

		testClientId = clientRepository.add(testClient);

		// create a new reference test task

		String 		taskName 	= "resultTestName";
		String 		taskParam 	= "{}";
		String 		taskPath 	= "/dev/null";
		TaskState 	taskState 	= TaskState.COMPLETED;
		int 		taskUsrId 	= testClientId;
		int 		taskNodeId 	= 1;

		Task task = new Task(
			-1,
			taskName,
			taskParam,
			taskPath,
			taskState,
			taskUsrId,
			taskNodeId
		);

		testTaskId = taskRepository.add(task);
	}

	private static TaskResult mockResult() throws RepositoryException {

		int 		mockTaskId 	= testTaskId;
		int 		mockErrCode	= 0;
		String 		mockErrMsg 	= "testMsg";
		byte[] 		mockBuff 	= {1, 0 , 4, 3, 2};
		TaskOutcome mockOutcome = TaskOutcome.SUCCESS;

		TaskResult mockResult = new TaskResult(
			mockErrCode,
			mockErrMsg,
			mockBuff,
			mockTaskId,
			mockOutcome
		);

		return mockResult;
	}

	@Test
	public void testAdd() throws RepositoryException {

		resultRepository.add(mockResult());
	}

	@Test
	public void testDelete() throws RepositoryException {

		TaskResult instance = mockResult();

		resultRepository.add(instance);
		resultRepository.delete(instance);
	}

	@Test 
	public void testGetByTaskId() throws RepositoryException {

		TaskResult instanceIn = mockResult();

		resultRepository.add(instanceIn);

		TaskResult instanceOut = resultRepository.getResultByTaskId(instanceIn.taskId);

		assertTrue(instanceIn.equals(instanceOut));
	}
}