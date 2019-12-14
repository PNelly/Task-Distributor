package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.core.*;
import com.itt.tds.coordinator.Node;

public abstract class AbsNodeRepository {
	
	/*
		Node Table Column Name Constants
	*/

	protected static final String tableNode 	= "node";
	protected static final String colNodeId 	= "nodeId";
	protected static final String colNodeIp 	= "nodeIp";
	protected static final String colNodePort 	= "nodePort";
	protected static final String colNodeStatus = "nodeStatus";

	/*
		Node Repository Members
	*/

	protected static String[] nodeColumns =
		{colNodeIp, colNodePort, colNodeStatus};

	protected static final int numNodeColumns = nodeColumns.length;


	/*
		NodeRepository Methods
	*/

	protected static Node nodeFromResultSet(ResultSet resultSet) throws SQLException, TDSEnumException {

		Node node = new Node(
						resultSet.getInt(colNodeId),
						resultSet.getString(colNodeIp),
						resultSet.getInt(colNodePort),
						NodeState.fromString(resultSet.getString(colNodeStatus))
					);

		return node;
	}

	public abstract int add(Node node)
		throws RepositoryException;

	public abstract void modify(Node node)
		throws RepositoryException;

	public abstract void delete(Node node)
		throws RepositoryException;

	public abstract Node getNodeById(int nodeId)
		throws RepositoryException;

	public abstract int resolveNodeId(String ip, int port)
		throws RepositoryException;

	public abstract int getNumNodes()
		throws RepositoryException;

	public abstract int getNumNodesByState(NodeState state)
		throws RepositoryException;

	public abstract List<Node> getAvailableNodes()
		throws RepositoryException;

	public abstract List<Node> getAllNodes()
		throws RepositoryException;
}