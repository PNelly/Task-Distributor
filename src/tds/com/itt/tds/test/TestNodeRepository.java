package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.List;

import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.Node;
import com.itt.tds.core.NodeState;

public class TestNodeRepository {

	private static AbsNodeRepository nodeRepository 
		= Repositories.getNodeRepository();

	private static Node nodeAddHelper() throws RepositoryException {

		Node node = TestUtil.randomNode();

		int failValue = -1;

		int addId = failValue;

		addId = nodeRepository.add(node);

		assertTrue(failValue != addId);

		node.setId(addId);

		return node;		
	}

	@Test
	public void testAdd() throws RepositoryException {

		int failValue = -1;

		Node addNode = nodeAddHelper();

		assertTrue(failValue != addNode.getId());
	}

	@Test
	public void testModify() throws RepositoryException {

		/* Create Unique Mod Target */

		String 		modNodeIp 		= TestUtil.randomIp();
		int 		modNodePort  	= TestUtil.randomPort();
		NodeState	modNodeState 	= NodeState.BUSY;

		while(nodeRepository.resolveNodeId(
				modNodeIp,
				modNodePort
				) > 0
			){

			modNodeIp 	= TestUtil.randomIp();
			modNodePort = TestUtil.randomPort();
		}

		/* Setup by Adding a new Node */

		Node node  = nodeAddHelper();
		int nodeId = node.getId();

		/* attempt modify using other node instance */

		Node modNode = new Node(nodeId, modNodeIp, modNodePort, modNodeState);

		nodeRepository.modify(modNode);

		Node retrieveNode = nodeRepository.getNodeById(nodeId);

		assertEquals(modNodeIp,    retrieveNode.getIp());
		assertEquals(modNodePort,  retrieveNode.getPort());
		assertEquals(modNodeState, retrieveNode.getState());
	}

	@Test
	public void testDelete() throws RepositoryException {

		Node node = nodeAddHelper();

		nodeRepository.delete(node);

		Node retrieveNode = nodeRepository.getNodeById(node.getId());

		assertNull(retrieveNode);
	}

	@Test
	public void testGetById() throws RepositoryException {

		/* Setup by Adding a new Node */

		Node node = nodeAddHelper();

		int nodeId = node.getId();

		/* Attempt Retrieve of same Node */

		Node retrieveNode = null;

		retrieveNode = nodeRepository.getNodeById(nodeId);

		assertNotNull(retrieveNode);
	}

	@Test
	public void testGetNumNodes() throws RepositoryException {

		assertTrue(nodeRepository.getNumNodes() >= 0);
	}

	@Test
	public void testNumNodesByState() throws RepositoryException {

		assertTrue(nodeRepository.getNumNodesByState(NodeState.AVAILABLE) >= 0);
		assertTrue(nodeRepository.getNumNodesByState(NodeState.BUSY) >= 0);
		assertTrue(nodeRepository.getNumNodesByState(NodeState.NOT_OPERATIONAL) >= 0);
	}

	@Test
	public void testGetAllNodes() throws RepositoryException {

		int numNodes = nodeRepository.getNumNodes();

		List<Node> nodes = nodeRepository.getAllNodes();

		assertEquals(numNodes, nodes.size());
	}

	@Test
	public void testGetAvailableNodes() throws RepositoryException {

		int numAvailable = nodeRepository.getNumNodesByState(NodeState.AVAILABLE);

		List<Node> nodes = nodeRepository.getAvailableNodes();

		assertEquals(numAvailable, nodes.size());
	}
}