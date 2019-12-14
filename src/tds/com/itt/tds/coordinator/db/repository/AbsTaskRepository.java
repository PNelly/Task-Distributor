package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.core.*;
import com.itt.tds.coordinator.Node;

public abstract class AbsTaskRepository {

	/*
		Task Table Column Name Constants
	*/

	protected static final String tableTask 		= "task";
	protected static final String colTaskId 		= "taskId";
	protected static final String colTaskName		= "taskName";
	protected static final String colTaskParameter	= "taskParameter";
	protected static final String colTaskPath 		= "taskPath";
	protected static final String colTaskState 		= "taskState";
	protected static final String colUserId 		= "userID";
    protected static final String colNodeId         = "assignedNodeId";

	/*
		TaskRepository Members
	*/

	protected static String[] taskColumns = 
		{colTaskName, colTaskParameter, colTaskPath, colTaskState, colUserId, colNodeId};

	protected static final int numTaskColumns = taskColumns.length;

	/*
		TaskRepository Methods
	*/

	protected static Task taskFromResultSet(ResultSet resultSet) throws SQLException, TDSEnumException {

		Task task = new Task(
							resultSet.getString(colTaskName),
							resultSet.getString(colTaskParameter),
							resultSet.getString(colTaskPath),
							TaskState.fromString(resultSet.getString(colTaskState)),
							null, // TODO: <-- need to implement task result fetch
							resultSet.getInt(colTaskId),
							null, // TODO: <-- need to implement program bytes fetch
							resultSet.getInt(colUserId),
							resultSet.getInt(colNodeId)
						);
		return task;
	}	

	public abstract int add(Task taskInstance) 
		throws RepositoryException;

	public abstract void modify(Task taskInstance) 
		throws RepositoryException;

	public abstract void delete(Task taskInstance) 
		throws RepositoryException;

	public abstract void setTaskStatus(int taskId, TaskState status)
		throws RepositoryException;

	public abstract List<Task> getTasksByClientId(int clientId)
		throws RepositoryException;

	public abstract Task getTaskById(int taskId) 
		throws RepositoryException;

	public abstract List<Task> getTasksByStatus(TaskState status)
		throws RepositoryException;

	public abstract List<Task> getTasksByNodeId(int nodeId)
		throws RepositoryException;

	public abstract void assignNode(Node node, int taskId)
		throws RepositoryException;

	public abstract int numTasksByClientId(int clientId)
		throws RepositoryException;

	public abstract int numTasksByState(TaskState state)
		throws RepositoryException;

	public abstract int numTasksByNodeId(int nodeId)
		throws RepositoryException;
}