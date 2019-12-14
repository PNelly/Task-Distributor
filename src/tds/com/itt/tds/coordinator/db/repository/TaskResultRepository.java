package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;
import javax.sql.rowset.serial.SerialBlob;

import com.itt.tds.coordinator.db.DBManager;
import com.itt.tds.core.*;

public class TaskResultRepository extends AbsTaskResultRepository {

	@Override
	public void add(TaskResult instance) throws RepositoryException {

		final String addString = DBManager.sqlInsert + tableTaskResult
			+ DBManager.sqlColumnList(taskResultColumns) + DBManager.sqlValues
			+ DBManager.sqlValuesList(numTaskResultColumns) + " ;";

		Connection 			connection   = null;
		PreparedStatement 	addStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			addStatement = connection.prepareStatement(addString);

			addStatement.setInt(   1, instance.taskId);
			addStatement.setString(2, TaskOutcome.toString(instance.taskOutcome));
			addStatement.setInt(   3, instance.errorCode);
			addStatement.setString(4, instance.errorMessage);
			addStatement.setBlob(  5, new SerialBlob(instance.resultBuffer));

			addStatement.executeUpdate();

			connection.commit();

		} catch (SQLException e) {

			System.out.println("add taskresult excep: "+e.getMessage());

			try {

				connection.rollback();

			} catch (SQLException rbe){

				System.out.println(rbe.getMessage());
				System.out.println("rollback on task result add failed");

			} finally {

				throw new RepositoryException("could not add TaskResult");
			}

		} finally {

			DBManager.closeResource(addStatement);
			DBManager.closeConnection(connection);
		}
	}

	@Override
	public void delete(TaskResult instance) throws RepositoryException {

		final String deleteString = DBManager.sqlDelete + tableTaskResult
			+ DBManager.sqlWhere + colTaskId + " = ? ;";

		Connection connection = null;
		PreparedStatement deleteStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			deleteStatement = connection.prepareStatement(deleteString);
			deleteStatement.setInt(1, instance.taskId);
			deleteStatement.executeUpdate();

			connection.commit();

		} catch (SQLException e){

			try {
				connection.rollback();
			} catch (SQLException rbe){
				System.out.println(rbe.getMessage());
				System.out.println("rollback on task result delete failed");
			} finally {
				throw new RepositoryException("could not delete taskresult");
			}

		} finally {

			DBManager.closeResource(deleteStatement);
			DBManager.closeConnection(connection);
		}
	}

	@Override
	public TaskResult getResultByTaskId(int id) throws RepositoryException {

		final String getResultString = DBManager.sqlSelectAll + tableTaskResult
			+ DBManager.sqlWhere + colTaskId + " = ? ";

		Connection connection = null;
		PreparedStatement getResultStatement = null;
		TaskResult taskResult = null;

		try {

			connection = DBManager.getConnection();

			getResultStatement = connection.prepareStatement(getResultString);
			getResultStatement.setInt(1, id);

			ResultSet resultSet = getResultStatement.executeQuery();

			taskResult  = resultSet.next()
						? taskResultFromSQL(resultSet)
						: null;

		} catch(SQLException | TDSEnumException e){

			throw new RepositoryException(getResultString+"\n"+e.getMessage());

		} finally {

			DBManager.closeResource(getResultStatement);
			DBManager.closeConnection(connection);
		}

		return taskResult;
	}

	@Override 
	public int getMaxTaskId() throws RepositoryException {

		final String maxString = DBManager.sqlSelect + DBManager.sqlMax
			+ "(" + colTaskId + ")" + DBManager.sqlFrom 
			+ tableTaskResult + " ;";

		Connection connection = null;
		PreparedStatement getMaxStatement = null;

		int maxId = -1;

		try {

			connection = DBManager.getConnection();

			getMaxStatement = connection.prepareStatement(maxString);

			ResultSet set = getMaxStatement.executeQuery();

			if(set.next())
				maxId = set.getInt(1);

		} catch (SQLException e){

			throw new RepositoryException(maxString+"\n"+e.getMessage());

		} finally {

			DBManager.closeResource(getMaxStatement);
			DBManager.closeConnection(connection);
		}

		return maxId;
	}
}