package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.coordinator.db.DBManager;
import com.itt.tds.coordinator.Node;
import com.itt.tds.core.*;

public class TaskRepository extends AbsTaskRepository {

    @Override
    public int add(Task taskInstance) throws RepositoryException {

        final String addQueryString = DBManager.sqlInsert + tableTask
        	+ DBManager.sqlColumnList(taskColumns) + DBManager.sqlValues
        	+ DBManager.sqlValuesList(numTaskColumns) + " ;";

        int insertedId;

        Connection connection = null;

        PreparedStatement addStatement = null;

        try {

	    	connection = DBManager.getConnection();

	    	connection.setAutoCommit(false);

	    	addStatement = connection.prepareStatement(
	    		addQueryString,
	    		Statement.RETURN_GENERATED_KEYS
    		);

	    	addStatement.setString(1, 	taskInstance.getTaskName());
	    	addStatement.setString(2, 	taskInstance.getTaskParameters());
	    	addStatement.setString(3, 	taskInstance.getTaskExePath());
	    	addStatement.setString(4, 	taskInstance.getTaskState().toString());
	    	addStatement.setInt(   5, 	taskInstance.getUserId());
	    	addStatement.setInt(   6,	taskInstance.getAssignedNodeId());

	    	addStatement.executeUpdate();

	    	connection.commit();

	    	ResultSet keySet = addStatement.getGeneratedKeys();

	    	keySet.first();

	    	insertedId = keySet.getInt(1);

    	} catch (SQLException sqlException) {

    		System.out.println("add task sql exception: "+sqlException.getMessage());

    		try {

    			connection.rollback();

    		} catch (SQLException rollbackException) {

    			System.out.println(rollbackException.getMessage());
    			System.out.println("rollback on task add failed");

    		} finally {

    			throw new RepositoryException("could not add Task");
    		}

    	} finally {

	        DBManager.closeResource(addStatement);
	    	DBManager.closeConnection(connection);
    	}

    	return insertedId;
    }

    @Override
    public void modify(Task taskInstance) throws RepositoryException {

        final String modifyQueryString = DBManager.sqlUpdate + tableTask 
            + DBManager.sqlSet + DBManager.sqlSetList(taskColumns)
            + DBManager.sqlWhere + colTaskId + " = ? ;";

        Connection connection = null;

        PreparedStatement modifyStatement = null;

        try {

	    	connection = DBManager.getConnection();

	    	connection.setAutoCommit(false);

	    	modifyStatement = connection.prepareStatement(modifyQueryString);

	    	modifyStatement.setString(1, taskInstance.getTaskName());
	    	modifyStatement.setString(2, taskInstance.getTaskParameters());
	    	modifyStatement.setString(3, taskInstance.getTaskExePath());
	    	modifyStatement.setString(4, taskInstance.getTaskState().toString());
	    	modifyStatement.setInt(   5, taskInstance.getUserId());
	    	modifyStatement.setInt(   6, taskInstance.getAssignedNodeId());
            modifyStatement.setInt(   7, taskInstance.getId());

	    	modifyStatement.executeUpdate();

	    	connection.commit();

    	} catch (SQLException sqlException) {

            System.out.println("modify task sql exception: "+sqlException.getMessage());

    		try {

    			connection.rollback();

    		} catch (SQLException rollbackException){

    			System.out.println(rollbackException.getMessage());
    			System.out.println("rollback on task modify failed");

    		} finally {

    			throw new RepositoryException("could not modify task");
    		}

    	} finally {

	        DBManager.closeResource(modifyStatement);
	    	DBManager.closeConnection(connection);
    	}

        return;
    }

    @Override
    public void delete(Task taskInstance) throws RepositoryException {

        final String deleteQueryString = DBManager.sqlDelete + tableTask 
            + DBManager.sqlWhere + colTaskId + " = ? ;";

        Connection connection = null;

        PreparedStatement deleteStatement = null;

        try {

	    	connection = DBManager.getConnection();

	    	connection.setAutoCommit(false);

	    	deleteStatement = connection.prepareStatement(deleteQueryString);

	    	deleteStatement.setInt(1, taskInstance.getId());

	    	deleteStatement.executeUpdate();

	    	connection.commit();

    	} catch (SQLException sqlException) {

    		try {

    			connection.rollback();

    		} catch (SQLException rollbackException) {

    			System.out.println(rollbackException.getMessage());
    			System.out.println("rollback on task delete failed");

    		} finally {

    			throw new RepositoryException("could not delete task");
    		}

    	} finally {

	        DBManager.closeResource(deleteStatement);
	    	DBManager.closeConnection(connection);
    	}

        return;
    }

    @Override
    public void setTaskStatus(int taskId, TaskState status) throws RepositoryException {

        final String setStatusQueryString = DBManager.sqlUpdate + tableTask 
            + DBManager.sqlSet + colTaskState + " = ? " + DBManager.sqlWhere 
            + colTaskId + " = ? ;";

        Connection connection = null;

        PreparedStatement setStatusStatement = null;

        try {

	    	connection = DBManager.getConnection();

	    	connection.setAutoCommit(false);

	    	setStatusStatement = connection.prepareStatement(setStatusQueryString);

	    	setStatusStatement.setString(1, status.toString());
	    	setStatusStatement.setInt(   2, taskId);

	    	setStatusStatement.executeUpdate();

	    	connection.commit();

	    } catch (SQLException sqlException) {

	    	try {

	    		connection.rollback();

	    	} catch (SQLException rollbackException){

	    		System.out.println(rollbackException.getMessage());
	    		System.out.println("rollback on set task status failed");

	    	} finally {

	    		throw new RepositoryException("could not set task status");
	    	}

	    } finally {

	        DBManager.closeResource(setStatusStatement);
	    	DBManager.closeConnection(connection);
    	}

        return;
    }

    @Override
    public List<Task> getTasksByClientId(int clientId) throws RepositoryException {

        final String tasksByClientQueryString = DBManager.sqlSelectAll + tableTask 
            + DBManager.sqlWhere + colUserId + " = ? ;";

        Connection connection = null;

        PreparedStatement getTasksStatement = null;

        ArrayList<Task> tasks = null;

        try {

	    	connection = DBManager.getConnection();

	    	getTasksStatement = connection.prepareStatement(tasksByClientQueryString);

	    	getTasksStatement.setInt(1, clientId);

	    	ResultSet resultSet = getTasksStatement.executeQuery();

	    	tasks = new ArrayList<Task>();

	    	while(resultSet.next())
				tasks.add(taskFromResultSet(resultSet));

		} catch (SQLException | TDSEnumException ex) {

			throw new RepositoryException("could not get tasks by client id");

		} finally {

	        DBManager.closeResource(getTasksStatement);
	        DBManager.closeConnection(connection);
        }

		return tasks;
    }

    @Override
    public Task getTaskById(int taskId) throws RepositoryException {


        final String taskByIdQueryString = DBManager.sqlSelectAll + tableTask 
            + DBManager.sqlWhere + colTaskId + " = ? ;";

        Connection connection = null;

        PreparedStatement getTaskStatement = null;

        Task task = null;

        try {

	    	connection = DBManager.getConnection();

	    	getTaskStatement = connection.prepareStatement(taskByIdQueryString);

	    	getTaskStatement.setInt(1, taskId);

	    	ResultSet resultSet = getTaskStatement.executeQuery();

	    	task = resultSet.next()
	    		 ? taskFromResultSet(resultSet) 
	    		 : null;

    	} catch (SQLException | TDSEnumException ex) {

    		throw new RepositoryException("could not get task by id");

    	} finally {

	        DBManager.closeResource(getTaskStatement);
	        DBManager.closeConnection(connection);
        }

    	return task;
    }

    @Override
    public List<Task> getTasksByStatus(TaskState status) throws RepositoryException {

        final String tasksByStatusQueryString = DBManager.sqlSelectAll + tableTask 
            + DBManager.sqlWhere + colTaskState + " = ? ;";

        Connection connection = null;

        PreparedStatement getTasksStatement = null;

        ArrayList<Task> tasks = null;

        try {

	    	connection = DBManager.getConnection();

	    	getTasksStatement = connection.prepareStatement(tasksByStatusQueryString);

	    	getTasksStatement.setString(1, status.toString());

	    	ResultSet resultSet = getTasksStatement.executeQuery();

	    	tasks = new ArrayList<Task>();

	    	while(resultSet.next())
	    		tasks.add(taskFromResultSet(resultSet));

    	} catch (SQLException | TDSEnumException ex) {

    		throw new RepositoryException("could not get tasks by status");

    	} finally {

	        DBManager.closeResource(getTasksStatement);
	        DBManager.closeConnection(connection);
	    }

    	return tasks;
    }

    @Override
    public List<Task> getTasksByNodeId(int nodeId) throws RepositoryException {

        final String tasksByNodeQueryString = DBManager.sqlSelectAll + tableTask 
            + DBManager.sqlWhere + colNodeId + " = ? ;";

        Connection connection = null;

        PreparedStatement getTasksStatement = null;

        ArrayList<Task> tasks = null;

        try {

	        connection = DBManager.getConnection();

	        getTasksStatement = connection.prepareStatement(tasksByNodeQueryString);

	        getTasksStatement.setInt(1, nodeId);

	        ResultSet resultSet = getTasksStatement.executeQuery();

	        tasks = new ArrayList<Task>();

	        while(resultSet.next())
	            tasks.add(taskFromResultSet(resultSet));
	        
	    } catch (SQLException | TDSEnumException ex) {

	    	throw new RepositoryException("could not get tasks by node id");

	    } finally {

	        DBManager.closeResource(getTasksStatement);
	        DBManager.closeConnection(connection);
        }

        return tasks;
    }

    @Override
    public void assignNode(Node node, int taskId) throws RepositoryException {

        final String assignNodeQueryString = DBManager.sqlUpdate + tableTask 
            + DBManager.sqlSet + colNodeId + " = ? " + DBManager.sqlWhere 
            + colTaskId + " = ? ;";

        Connection connection = null;

        PreparedStatement assignNodeStatement = null;

        try {

	        connection = DBManager.getConnection();

	        connection.setAutoCommit(false);

	        assignNodeStatement = connection.prepareStatement(assignNodeQueryString);

	        assignNodeStatement.setInt(1, node.getId());
	        assignNodeStatement.setInt(2, taskId);

	        assignNodeStatement.executeUpdate();

	        connection.commit();

        } catch (SQLException sqlException) {

        	try {

        		connection.rollback();

        	} catch(SQLException rollbackException){

        		System.out.println(rollbackException.getMessage());
        		System.out.println("rollback on assign node failed");

        	} finally {

        		throw new RepositoryException("could not assign task to node");
        	}

        } finally {

	        DBManager.closeResource(assignNodeStatement);
	        DBManager.closeConnection(connection);
        }

        return;
    }

    @Override
    public int numTasksByClientId(int clientId) throws RepositoryException {

    	final String numTasksQueryString = DBManager.sqlCountRows + tableTask
    		+ DBManager.sqlWhere + colUserId + " = ? ;";

    	Connection connection = null;

    	PreparedStatement numTasksStatement = null;

    	int numTasks = -1;

    	try {

    		connection = DBManager.getConnection();

    		numTasksStatement = connection.prepareStatement(numTasksQueryString);

    		numTasksStatement.setInt(1, clientId);

    		ResultSet resultSet = numTasksStatement.executeQuery();

    		if(resultSet.next())
    			numTasks = resultSet.getInt(1);

    	} catch (SQLException sqlException) {

    		throw new RepositoryException("could not get num tasks by client id");

    	} finally {

			DBManager.closeResource(numTasksStatement);
			DBManager.closeConnection(connection);
    	}

    	return numTasks;
    }

    @Override
    public int numTasksByState(TaskState state) throws RepositoryException {

    	final String numTasksQueryString = DBManager.sqlCountRows + tableTask
    		+ DBManager.sqlWhere + colTaskState + " = ? ;";

    	Connection connection = null;

    	PreparedStatement numTasksStatement = null;

    	int numTasks = -1;

    	try {

    		connection = DBManager.getConnection();

    		numTasksStatement = connection.prepareStatement(numTasksQueryString);

    		numTasksStatement.setString(1, state.toString());

    		ResultSet resultSet = numTasksStatement.executeQuery();

    		if(resultSet.next())
    			numTasks = resultSet.getInt(1);

    	} catch (SQLException sqlException) {

    		throw new RepositoryException("could not get num tasks by client id");

    	} finally {

			DBManager.closeResource(numTasksStatement);
			DBManager.closeConnection(connection);
    	}

    	return numTasks;
    }

    @Override
    public int numTasksByNodeId(int nodeId) throws RepositoryException {

    	final String numTasksQueryString = DBManager.sqlCountRows + tableTask
    		+ DBManager.sqlWhere + colNodeId + " = ? ;";

    	Connection connection = null;

    	PreparedStatement numTasksStatement = null;

    	int numTasks = -1;

    	try {

    		connection = DBManager.getConnection();

    		numTasksStatement = connection.prepareStatement(numTasksQueryString);

    		numTasksStatement.setInt(1, nodeId);

    		ResultSet resultSet = numTasksStatement.executeQuery();

    		if(resultSet.next())
    			numTasks = resultSet.getInt(1);

    	} catch (SQLException sqlException) {

    		throw new RepositoryException("could not get num tasks by client id");

    	} finally {

			DBManager.closeResource(numTasksStatement);
			DBManager.closeConnection(connection);
    	}

    	return numTasks;
    }
}