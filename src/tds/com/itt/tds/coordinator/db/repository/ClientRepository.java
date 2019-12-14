package com.itt.tds.coordinator.db.repository;

import java.util.*;
import java.sql.*;
import com.itt.tds.coordinator.Client;
import com.itt.tds.coordinator.db.DBManager;

public class ClientRepository extends AbsClientRepository {

	@Override
	public int add(Client client) throws RepositoryException {

		int existingId = resolveClientId(
							client.getHostName(),
							client.getUserName()
						);

		if(existingId > 0)
			return existingId;

		final String addQueryString = DBManager.sqlInsert + tableClient
			+ DBManager.sqlColumnList(clientColumns) + DBManager.sqlValues
			+ DBManager.sqlValuesList(numClientColumns) + " ;";

		int insertedId;
		Connection 			connection 	 = null;
		PreparedStatement 	addStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			addStatement = connection.prepareStatement(
					addQueryString,
					Statement.RETURN_GENERATED_KEYS
			);

			addStatement.setString(1, client.getHostName());
			addStatement.setString(2, client.getUserName());
			addStatement.executeUpdate();

			connection.commit();

			ResultSet keySet = addStatement.getGeneratedKeys();

			keySet.first();

			insertedId = keySet.getInt(1);

		} catch (SQLException sqlException){

			System.out.println("add client sql exception: "+sqlException.getMessage());

			try {

				connection.rollback();

			} catch (SQLException rollbackException) {

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on client add failed");

			} finally {

				throw new RepositoryException("could not add client");
			}

		} finally {

			DBManager.closeResource(addStatement);
			DBManager.closeConnection(connection);
		}

		return insertedId;
	}

	@Override
	public void modify(Client client) throws RepositoryException {

		int existingId = resolveClientId(
							client.getHostName(),
							client.getUserName()
						);

		if(existingId > 0)
			return;

		final String modifyQueryString = DBManager.sqlUpdate + tableClient
			+ DBManager.sqlSet + DBManager.sqlSetList(clientColumns)
			+ DBManager.sqlWhere + colClientId + " = ? ;";

		Connection 			connection 		= null;
		PreparedStatement 	modifyStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			modifyStatement = connection.prepareStatement(modifyQueryString);

			modifyStatement.setString(1, client.getHostName());
			modifyStatement.setString(2, client.getUserName());
			modifyStatement.setInt(   3, client.getId());
			modifyStatement.executeUpdate();

			connection.commit();

		} catch (SQLException sqlException) {

			try {

				connection.rollback();

			} catch (SQLException rollbackException) {

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on client modify failed");

			} finally {

				throw new RepositoryException(
					"could not modify client "+sqlException.getMessage()
				);
			}

		} finally {

			DBManager.closeResource(modifyStatement);
			DBManager.closeConnection(connection);
		}

		return;
	}

	@Override
	public void delete(Client client) throws RepositoryException {

		final String deleteQueryString = DBManager.sqlDelete + tableClient
			+ DBManager.sqlWhere + colClientId + " = ? ;";

		Connection 			connection 		= null;
		PreparedStatement 	deleteStatement = null;

		try {

			connection = DBManager.getConnection();
			connection.setAutoCommit(false);

			deleteStatement = connection.prepareStatement(deleteQueryString);
				
			deleteStatement.setInt(1, client.getId());
			deleteStatement.executeUpdate();

			connection.commit();

		} catch (SQLException sqlException) {

			try {

				connection.rollback();

			} catch (SQLException rollbackException) {

				System.out.println(rollbackException.getMessage());
				System.out.println("rollback on client delete failed");

			} finally {

				throw new RepositoryException("could not delete client");
			}

		} finally {

			DBManager.closeResource(deleteStatement);
			DBManager.closeConnection(connection);
		}

		return;
	}

	@Override
	public Client getClientById(int clientId) throws RepositoryException {

		final String clientByIdQueryString = DBManager.sqlSelectAll + tableClient
			+ DBManager.sqlWhere + colClientId + " = ? ;";

		Connection 			connection 			= null;
		PreparedStatement 	clientByIdStatement = null;
		Client 				client 				= null;

		try {

			connection = DBManager.getConnection();

			clientByIdStatement = connection.prepareStatement(clientByIdQueryString);
			clientByIdStatement.setInt(1, clientId);

			ResultSet resultSet = clientByIdStatement.executeQuery();

			if(resultSet.next())
				client = clientFromResultSet(resultSet);

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not query client by id");

		} finally {

			DBManager.closeResource(clientByIdStatement);
			DBManager.closeConnection(connection);
		}

		return client;
	}

	@Override
	public int resolveClientId(String hostName, String userName) throws RepositoryException {

		final String resolveIdQueryString = DBManager.sqlSelect 
			+ colClientId + DBManager.sqlFrom + tableClient
			+ DBManager.sqlWhere + colHostName + " = ? "
			+ DBManager.sqlAnd + colUserName + " = ? ;";

		Connection 			connection 		 = null;
		PreparedStatement 	resolveStatement = null;

		int clientId = -1;

		try {

			connection = DBManager.getConnection();

			resolveStatement = connection.prepareStatement(resolveIdQueryString);
			resolveStatement.setString(1, hostName);
			resolveStatement.setString(2, userName);

			ResultSet resultSet = resolveStatement.executeQuery();

			if(resultSet.next())
				clientId = resultSet.getInt(1);

		} catch (SQLException sqlException){

			throw new RepositoryException(
				"Repo Excep "+sqlException.getMessage()
			);

		} finally {

			DBManager.closeResource(resolveStatement);
			DBManager.closeConnection(connection);
		}

		return clientId;
	}

	@Override
	public int getNumClients() throws RepositoryException {

		final String numClientsQueryString = DBManager.sqlCountRows + tableClient;

		Connection 			connection 			= null;
		PreparedStatement 	numClientsStatement = null;

		int numClients = -1;

		try {

			connection = DBManager.getConnection();

			numClientsStatement = connection.prepareStatement(numClientsQueryString);

			ResultSet resultSet = numClientsStatement.executeQuery();

			if(resultSet.next())
				numClients = resultSet.getInt(1);

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not get num client rows");
		
		} finally {

			DBManager.closeResource(numClientsStatement);
			DBManager.closeConnection(connection);
		}

		return numClients;
	}

	@Override
	public List<Client> getClients() throws RepositoryException {

		final String allClientsQueryString = DBManager.sqlSelectAll + tableClient;

		Connection 			connection 			= null;
		PreparedStatement 	allClientsStatement = null;
		ArrayList<Client> 	clients 			= null;

		try {

			connection = DBManager.getConnection();

			allClientsStatement = connection.prepareStatement(allClientsQueryString);

			ResultSet resultSet = allClientsStatement.executeQuery();

			clients = new ArrayList<Client>();

			while(resultSet.next())
				clients.add(clientFromResultSet(resultSet));

		} catch (SQLException sqlException) {

			throw new RepositoryException("could not query all clients");

		} finally {

			DBManager.closeResource(allClientsStatement);
			DBManager.closeConnection(connection);
		}

		return clients;
	}
}