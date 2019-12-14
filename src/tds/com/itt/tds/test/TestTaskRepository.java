package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.List;

import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.Client;
import com.itt.tds.coordinator.Node;
import com.itt.tds.core.*;

public class TestTaskRepository {

	private static AbsClientRepository clientRepository 
		= Repositories.getClientRepository();

	private static AbsTaskRepository taskRepository
		= Repositories.getTaskRepository();

	private static int addClientId = -1;
	private static int modClientId = -1;

	private static void checkClientDependencies() throws RepositoryException {

		if(addClientId >= 0 && modClientId >= 0)
			return;

		String addUserName = "testTaskClient";
		String addHostName = "testTaskHost";
		String modUserName = "testTaskModClient";
		String modHostName = "testTaskModHost";

		Client addClient   = new Client(addHostName, addUserName);
		Client modClient   = new Client(modHostName, modUserName);

		addClientId = clientRepository.add(addClient);
		modClientId = clientRepository.add(modClient);

		int failValue = -1;

		assertTrue(failValue != addClientId);
		assertTrue(failValue != modClientId);
	}

	private static Task taskAddHelper() throws RepositoryException {

		checkClientDependencies();

		int failValue = -1;

		int addId = failValue;

		String 		taskName  	= "testName";
		String 		taskParam 	= "{}";
		String 		taskPath  	= "/dev/null";
		TaskState 	taskState 	= TaskState.PENDING;
		int 		taskUsrId 	= addClientId;
		int 		taskNodeId  = 1;

		Task task = new Task(failValue, taskName, taskParam,
			taskPath,taskState, taskUsrId, taskNodeId);

		addId = taskRepository.add(task);

		assertTrue(failValue != addId);

		task.setId(addId);

		assertTrue(failValue != task.getId());

		return task;
	}

	@Test
	public void testAdd() throws RepositoryException {

		checkClientDependencies();

		int failValue = -1;

		Task addTask = taskAddHelper();

		assertTrue(failValue != addTask.getId());
	}

	@Test
	public void testModify() throws RepositoryException {

		checkClientDependencies();

		/* Setup by adding a new task */

		Task task = taskAddHelper();

		int taskId = task.getId();

		/* attempt modify using other task instance */

		String 		modName 	= "modName";
		String 		modParam 	= "{\"key\": \"val\"}";
		String 		modPath 	= "/null/null";
		TaskState 	modState 	= TaskState.IN_PROGRESS;
		int 		modUsrId 	= modClientId;
		int 		modNodeId   = 2;
		
		Task modTask = new Task(taskId, modName, modParam,
			modPath, modState, modUsrId, modNodeId);

		taskRepository.modify(modTask);

		Task retrieveTask = taskRepository.getTaskById(taskId);

		assertEquals(modName,  retrieveTask.getTaskName());
		assertEquals(modParam, retrieveTask.getTaskParameters());
		assertEquals(modPath,  retrieveTask.getTaskExePath());
		assertEquals(modState, retrieveTask.getTaskState());
		assertEquals(modUsrId, retrieveTask.getUserId());
	}

	@Test
	public void testDelete() throws RepositoryException {

		checkClientDependencies();

		/* Setup by adding a new task */

		Task task = taskAddHelper();

		int taskId = task.getId();

		/* attempt delete */

		taskRepository.delete(task);

		assertNull(taskRepository.getTaskById(taskId));
	}

	@Test
	public void testSetTaskStatus() throws RepositoryException {

		checkClientDependencies();

		/* Setup by adding a new task */

		Task task = taskAddHelper();

		int taskId = task.getId();

		/* attempt status set */

		TaskState modState = TaskState.COMPLETED;

		taskRepository.setTaskStatus(taskId, modState);

		Task retrieveTask = taskRepository.getTaskById(taskId);

		assertEquals(modState, retrieveTask.getTaskState());
	}

	@Test
	public void testGetTasksByClientId() throws RepositoryException {

		checkClientDependencies();

		int numTasks = taskRepository.numTasksByClientId(addClientId);

		List<Task> tasks = taskRepository.getTasksByClientId(addClientId);

		assertEquals(numTasks, tasks.size());
	}

	@Test
	public void testGetTaskById() throws RepositoryException {

		checkClientDependencies();

		Task task = taskAddHelper();

		int taskId = task.getId();

		Task retrieveTask = taskRepository.getTaskById(taskId);

		assertNotNull(retrieveTask);
	}

	@Test
	public void testGetTasksByStatus() throws RepositoryException {

		checkClientDependencies();

		int numPending  = taskRepository.numTasksByState(TaskState.PENDING);
		int numProgress = taskRepository.numTasksByState(TaskState.IN_PROGRESS);
		int numComplete = taskRepository.numTasksByState(TaskState.COMPLETED);

		List<Task> tasksPending  = taskRepository.getTasksByStatus(TaskState.PENDING);
		List<Task> tasksProgress = taskRepository.getTasksByStatus(TaskState.IN_PROGRESS);
		List<Task> tasksComplete = taskRepository.getTasksByStatus(TaskState.COMPLETED);

		assertEquals(numPending,  tasksPending.size());
		assertEquals(numProgress, tasksProgress.size());
		assertEquals(numComplete, tasksComplete.size());
	}

	@Test
	public void testGetTasksByNodeId() throws RepositoryException {

		checkClientDependencies();

		int numTasks = taskRepository.numTasksByNodeId(1);

		List<Task> tasks = taskRepository.getTasksByNodeId(1);

		assertEquals(numTasks, tasks.size());
	}

	@Test
	public void testAssignNode() throws RepositoryException {

		checkClientDependencies();

		Task task = taskAddHelper();

		int 		nodeId 	  = 5;
		String 		nodeIp 	  = "127.0.0.1";
		int 		nodePort  = 8080;
		NodeState 	nodeState = NodeState.BUSY;

		Node node = new Node(nodeId, nodeIp, nodePort, nodeState);

		taskRepository.assignNode(node, task.getId());

		Task retrieveTask = taskRepository.getTaskById(task.getId());

		assertEquals(nodeId, retrieveTask.getAssignedNodeId());
	}
}