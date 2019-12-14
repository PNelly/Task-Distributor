package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.coordinator.db.DBManager;
import com.itt.tds.coordinator.Node;
import com.itt.tds.core.NodeState;
import com.itt.tds.core.TDSEnumException;

public class NodeRepository extends AbsNodeRepository {

	@Override
	public int add(Node node) throws RepositoryException {

		int existingId 	= resolveNodeId(
							node.getIp(),
							node.getPort()
						);

		if(existingId > 0)
			return existingId;

		final String addQueryString = DBManager.sqlInsert + tableNode
			+ DBManager.sqlColumnList(nodeColumns) + DBManager.sqlValues 
			+ DBManager.sqlValuesList(numNodeColumns) + " ;";

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

			addStatement.setString(1, node.getIp());
			addStatement.setInt(   2, node.getPort());
			addStatement.setString(3, node.getState().toString());

			addStatement.executeUpdate();

			connection.commit();

			ResultSet keySet = addStatement.getGeneratedKeys();

			keySet.first();

			insertedId = keySet.getInt(1);

		} catch (SQLException sqlException) {

			System.out.println("add node encountered sql exception: "+sqlException.getMessage());

			try {

				connection.rollback();

			} catch (SQLException rollbackException) {

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on add node failed");

			} finally {

				throw new RepositoryException("could not add node");
			}

		} finally {

			DBManager.closeResource(addStatement);
			DBManager.closeConnection(connection);
		}

		return insertedId;
	}

	@Override
	public void modify(Node node) throws RepositoryException {

		int existingId 	= resolveNodeId(
							node.getIp(),
							node.getPort()
						);

		if(existingId > 0)
			return;

		final String modifyQueryString = DBManager.sqlUpdate + tableNode
			+ DBManager.sqlSet + DBManager.sqlSetList(nodeColumns)
			+ DBManager.sqlWhere + colNodeId + " = ? ;";

		Connection connection = null;

		PreparedStatement modifyStatement = null;

		try {

			connection = DBManager.getConnection();

			connection.setAutoCommit(false);

			modifyStatement = connection.prepareStatement(modifyQueryString);

			modifyStatement.setString(1, node.getIp());
			modifyStatement.setInt(   2, node.getPort());
			modifyStatement.setString(3, node.getState().toString());
			modifyStatement.setInt(   4, node.getId());

			modifyStatement.executeUpdate();

			connection.commit();

		} catch (SQLException sqlException) {

			System.out.println("node modify encountered sql exception: "+sqlException.getMessage());

			try {

				connection.rollback();

			} catch (SQLException rollbackException){

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on node modify failed");

			} finally {

				throw new RepositoryException("could not modify node");
			}

		} finally {

			DBManager.closeResource(modifyStatement);
			DBManager.closeConnection(connection);
		}

		return;
	}

	@Override
	public void delete(Node node) throws RepositoryException {

		final String deleteQueryString = DBManager.sqlDelete + tableNode
			+ DBManager.sqlWhere + colNodeId + " = ? ;";

		Connection connection = null;

		PreparedStatement deleteStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			deleteStatement = connection.prepareStatement(deleteQueryString);
			deleteStatement.setInt(1, node.getId());

			deleteStatement.executeUpdate();

			connection.commit();

		} catch (SQLException sqlException){

			try {

				connection.rollback();

			} catch (SQLException rollbackException) {

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on node delete failed");

			} finally {

				throw new RepositoryException("could not delete node");
			}

		} finally {

			DBManager.closeResource(deleteStatement);
			DBManager.closeConnection(connection);
		}

		return;
	}

	@Override
	public Node getNodeById(int nodeId) throws RepositoryException {

		final String nodeByIdQueryString = DBManager.sqlSelectAll + tableNode
			+ DBManager.sqlWhere + colNodeId + " = ? ;";

		Connection connection = null;

		PreparedStatement nodeByIdStatement = null;

		Node node = null;

		try {

			connection = DBManager.getConnection();

			nodeByIdStatement = connection.prepareStatement(nodeByIdQueryString);
			nodeByIdStatement.setInt(1, nodeId);

			ResultSet resultSet = nodeByIdStatement.executeQuery();

			if(resultSet.next())
				node = nodeFromResultSet(resultSet);

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not query node by id");

		} catch (TDSEnumException enumException){

			throw new RepositoryException("could not query node by id");

		} finally {

			DBManager.closeResource(nodeByIdStatement);
			DBManager.closeConnection(connection);
		}

		return node;
	}

	@Override
	public int resolveNodeId(String ip, int port) throws RepositoryException {

		final String resolveIdQueryString = DBManager.sqlSelect
			+ colNodeId + DBManager.sqlFrom + tableNode
			+ DBManager.sqlWhere + colNodeIp + " = ? "
			+ DBManager.sqlAnd + colNodePort + " = ? ";

		Connection 			connection 		 = null;
		PreparedStatement 	resolveStatement = null;

		int nodeId = -1;

		try {

			connection = DBManager.getConnection();

			resolveStatement = connection.prepareStatement(resolveIdQueryString);
			resolveStatement.setString(1, ip);
			resolveStatement.setInt(2, port);

			ResultSet resultSet = resolveStatement.executeQuery();

			if(resultSet.next())
				nodeId = resultSet.getInt(1);

		} catch (SQLException sqle){

			throw new RepositoryException(
				"RepoExcep "+sqle.getMessage()
			);

		} finally {

			DBManager.closeResource(resolveStatement);
			DBManager.closeConnection(connection);
		}

		return nodeId;
	}

	@Override
	public int getNumNodes() throws RepositoryException {

		final String numNodesQueryString = DBManager.sqlCountRows + tableNode;

		Connection connection = null;

		PreparedStatement numNodesStatement = null;

		int numNodes = -1;

		try {

			connection = DBManager.getConnection();

			numNodesStatement = connection.prepareStatement(numNodesQueryString);

			ResultSet resultSet = numNodesStatement.executeQuery();

			if(resultSet.next())
				numNodes = resultSet.getInt(1);

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not get num node rows");
		
		} finally {

			DBManager.closeResource(numNodesStatement);
			DBManager.closeConnection(connection);
		}

		return numNodes;
	}	

	@Override
	public int getNumNodesByState(NodeState state) throws RepositoryException {

		final String numNodesQueryString = DBManager.sqlCountRows + tableNode
			+ DBManager.sqlWhere + colNodeStatus + " = ? ;";

		Connection connection = null;

		PreparedStatement numNodesStatement = null;

		int numNodes = -1;

		try {

			connection = DBManager.getConnection();

			numNodesStatement = connection.prepareStatement(numNodesQueryString);

			numNodesStatement.setString(1, state.toString());

			ResultSet resultSet = numNodesStatement.executeQuery();

			if(resultSet.next())
				numNodes = resultSet.getInt(1);

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not get num node rows");
		
		} finally {

			DBManager.closeResource(numNodesStatement);
			DBManager.closeConnection(connection);
		}

		return numNodes;
	}	

	@Override
	public List<Node> getAvailableNodes() throws RepositoryException {

		final String availableQueryString = DBManager.sqlSelectAll + tableNode
			+ DBManager.sqlWhere + colNodeStatus + " = ? ;";

		Connection connection = null;

		PreparedStatement availableStatement = null;

		ArrayList<Node> nodes = null;

		try {

			connection = DBManager.getConnection();

			availableStatement = connection.prepareStatement(availableQueryString);

			availableStatement.setString(1, NodeState.toString(NodeState.AVAILABLE));

			ResultSet resultSet = availableStatement.executeQuery();

			nodes = new ArrayList<Node>();

			while(resultSet.next())
				nodes.add(nodeFromResultSet(resultSet));

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not query available nodes");

		} catch (TDSEnumException enumException){

			throw new RepositoryException("could not query available nodes");

		} finally {

			DBManager.closeResource(availableStatement);
			DBManager.closeConnection(connection);
		}

		return nodes;
	}

	@Override
	public List<Node> getAllNodes() throws RepositoryException {

		final String allNodesQueryString = DBManager.sqlSelectAll + tableNode;

		Connection connection = null;

		PreparedStatement allNodesStatement = null;

		ArrayList<Node> nodes = null;

		try {

			connection = DBManager.getConnection();

			allNodesStatement = connection.prepareStatement(allNodesQueryString);

			ResultSet resultSet = allNodesStatement.executeQuery();

			nodes = new ArrayList<Node>();

			while(resultSet.next())
				nodes.add(nodeFromResultSet(resultSet));

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not query all nodes");

		} catch (TDSEnumException enumException){

			throw new RepositoryException("could not query all nodes");

		} finally {

			DBManager.closeResource(allNodesStatement);
			DBManager.closeConnection(connection);
		}

		return nodes;
	}
}