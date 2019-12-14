package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;

import com.itt.tds.coordinator.Client;

public abstract class AbsClientRepository {

	/*
		Client Table Column Name Constants
	*/

	protected static final String tableClient 	= "client";
	protected static final String colClientId 	= "clientId";
	protected static final String colHostName 	= "hostName";
	protected static final String colUserName 	= "userName";

	/*
		ClientRepository Members
	*/

	protected static String[] clientColumns = 
		{colHostName, colUserName};

	protected static final int numClientColumns = clientColumns.length;


	/*
		ClientRepository Methods
	*/

	protected static Client clientFromResultSet(ResultSet resultSet) throws SQLException {

		Client client = new Client(
								resultSet.getInt(colClientId),
								resultSet.getString(colHostName),
								resultSet.getString(colUserName)
							);

		return client;
	}

	public abstract int add(Client client) 
		throws RepositoryException;

	public abstract void modify(Client client) 
		throws RepositoryException;

	public abstract void delete(Client client) 
		throws RepositoryException;

	public abstract Client getClientById(int clientId) 
		throws RepositoryException;

	public abstract int resolveClientId(String hostName, String userName)
		throws RepositoryException;

	public abstract int getNumClients() 
		throws RepositoryException;

	public abstract List<Client> getClients() 
		throws RepositoryException;
}