package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.core.*;

public abstract class AbsTaskResultRepository {
	
	/*
		Task Result Table Column Name Constants
	*/

	protected static final String tableTaskResult 	= "taskresult";
	protected static final String colTaskId 		= "taskId";
	protected static final String colTaskOutcome 	= "taskOutcome";
	protected static final String colTaskErrCode 	= "taskErrorCode";
	protected static final String colTaskErrMsg 	= "taskErrorMsg";
	protected static final String colTaskResultBuff = "taskResultBuffer";

	/*
		Column Management
	*/

	protected static String[] taskResultColumns = 
		{colTaskId, colTaskOutcome, colTaskErrCode, colTaskErrMsg, colTaskResultBuff};

	protected static final int numTaskResultColumns = taskResultColumns.length;

	/*
		Repository Methods
	*/

	protected static TaskResult taskResultFromSQL(ResultSet resultSet) 
		throws SQLException, TDSEnumException {

		Blob blob = resultSet.getBlob(colTaskResultBuff);
		byte[] resultBytes = blob.getBytes(1, (int) blob.length());

		TaskResult taskResult 	= new TaskResult(
									resultSet.getInt(colTaskErrCode),
									resultSet.getString(colTaskErrMsg),
									resultBytes,
									resultSet.getInt(colTaskId),
									TaskOutcome.fromString(resultSet.getString(colTaskOutcome))
								);

		return taskResult;
	}

	public abstract void add(TaskResult instance)
		throws RepositoryException;

	public abstract void delete(TaskResult instance)
		throws RepositoryException;

	public abstract TaskResult getResultByTaskId(int id)
		throws RepositoryException;

	public abstract int getMaxTaskId()
		throws RepositoryException;
}