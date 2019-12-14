package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.Random;

import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.Client;

public class TestClientRepository {

	private static AbsClientRepository clientRepository
		= Repositories.getClientRepository();

	private static Client clientAddHelper() throws RepositoryException {

		Client client = TestUtil.randomClient();

		int failAdd = -1;

		int clientId = failAdd;

		clientId = clientRepository.add(client);

		client.setId(clientId);

		assertTrue(failAdd != clientId);

		return client;
	}

	@Test
	public void testAdd() throws RepositoryException {

		int failValue = -1;

		Client addClient = clientAddHelper();

		assertTrue(failValue != addClient.getId());
	}

	@Test
	public void testModify() throws RepositoryException {

		/* Get New Unique Mod Target */

		String modUserName = TestUtil.randomString(16);
		String modHostName = TestUtil.randomString(16);

		while(clientRepository.resolveClientId(
				modHostName,
				modUserName
				) > 0
			){

			modHostName = TestUtil.randomString(16);
			modUserName = TestUtil.randomString(16);
		}

		/* Setup by Adding a new Client */

		Client client = clientAddHelper();
		int clientId  = client.getId();

		Client modClient = new Client(clientId, modHostName, modUserName);

		clientRepository.modify(modClient);

		Client retrieveClient = clientRepository.getClientById(clientId);

		assertEquals(modUserName, retrieveClient.getUserName());
		assertEquals(modHostName, retrieveClient.getHostName());
	}

	@Test
	public void testDelete() throws RepositoryException {

		/* Setup by Add a new Client */

		Client client = clientAddHelper();

		int clientId = client.getId();

		/* Attempt Delete */

		clientRepository.delete(client);

		Client retrieveClient = clientRepository.getClientById(clientId);

		assertNull(retrieveClient);
	}

	@Test
	public void testGetById() throws RepositoryException {

		/* Setup by Adding a new Client */

		Client client = clientAddHelper();

		int clientId = client.getId();

		/* Attempt Retrieve of same Client */

		Client retrieveClient = null;

		retrieveClient = clientRepository.getClientById(clientId);

		assertNotNull(retrieveClient);
	}

	@Test
	public void testGetNumClients() throws RepositoryException {

		assertTrue(clientRepository.getNumClients() >= 0);
	}

	@Test
	public void testGetClients() throws RepositoryException {

		int numClients = clientRepository.getNumClients();

		List<Client> clients = clientRepository.getClients();

		assertEquals(numClients, clients.size());
	}

	@Test
	public void testResolveId() throws RepositoryException {

		Client client = clientAddHelper();

		int idIn = client.getId();

		int idOut = clientRepository.resolveClientId(
						client.getHostName(),
						client.getUserName()
					);

		assertTrue(idIn == idOut);
	}
}