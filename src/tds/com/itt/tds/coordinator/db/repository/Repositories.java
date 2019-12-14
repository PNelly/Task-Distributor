package com.itt.tds.coordinator.db.repository;

public class Repositories {
	
	private static AbsClientRepository 		clientRepository 	 = new ClientRepository();
	private static AbsTaskRepository 		taskRepository 		 = new TaskRepository();
	private static AbsNodeRepository 		nodeRepository 		 = new NodeRepository();
	private static AbsTaskResultRepository	taskResultRepository = new TaskResultRepository();

	public static AbsClientRepository getClientRepository(){
		return clientRepository;
	}

	public static AbsTaskRepository getTaskRepository(){
		return taskRepository;
	}

	public static AbsNodeRepository getNodeRepository(){
		return nodeRepository;
	}

	public static AbsTaskResultRepository getTaskResultRepository(){
		return taskResultRepository;
	}
}