package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

import com.itt.tds.cfg.*;
import com.itt.tds.node.*;
import com.itt.tds.core.*;
import com.itt.tds.comm.*;
import com.itt.tds.coordinator.Client;
import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.CommunicationManager;

public class TestNode {

	private static TDSSerializer serializer
		= TDSSerializerFactory.getSerializer();

	private static CommunicationManager commMgr 
		= new CommunicationManager();

	@BeforeClass
	public static void initialize() throws IOException {

		commMgr.openListener();
	}

	@AfterClass
	public static void shutDown() throws IOException {

		/*
			node spawns execution thread for task, for clean
			exit need to wait until this thread completes
			before shutting down communication manager
		*/

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for(Thread thread : threadSet){
			if(thread.getName().equals(Node.TASK_THREAD_NAME)){

				try {
					thread.join();
				} catch (InterruptedException e){
					// allow to die
				}
			}
		}

		commMgr.stopListener();
	}
	
	private static final String helloWorldByteCode = (
		"cafe babe 0000 0034 001d 0a00 0600 0f09"
		+"0010 0011 0800 120a 0013 0014 0700 1507"
		+"0016 0100 063c 696e 6974 3e01 0003 2829"
		+"5601 0004 436f 6465 0100 0f4c 696e 654e"
		+"756d 6265 7254 6162 6c65 0100 046d 6169"
		+"6e01 0016 285b 4c6a 6176 612f 6c61 6e67"
		+"2f53 7472 696e 673b 2956 0100 0a53 6f75"
		+"7263 6546 696c 6501 000f 4865 6c6c 6f57"
		+"6f72 6c64 2e6a 6176 610c 0007 0008 0700"
		+"170c 0018 0019 0100 0b48 656c 6c6f 2057"
		+"6f72 6c64 0700 1a0c 001b 001c 0100 0a48"
		+"656c 6c6f 576f 726c 6401 0010 6a61 7661"
		+"2f6c 616e 672f 4f62 6a65 6374 0100 106a"
		+"6176 612f 6c61 6e67 2f53 7973 7465 6d01"
		+"0003 6f75 7401 0015 4c6a 6176 612f 696f"
		+"2f50 7269 6e74 5374 7265 616d 3b01 0013"
		+"6a61 7661 2f69 6f2f 5072 696e 7453 7472"
		+"6561 6d01 0007 7072 696e 746c 6e01 0015"
		+"284c 6a61 7661 2f6c 616e 672f 5374 7269"
		+"6e67 3b29 5600 2100 0500 0600 0000 0000"
		+"0200 0100 0700 0800 0100 0900 0000 1d00"
		+"0100 0100 0000 052a b700 01b1 0000 0001"
		+"000a 0000 0006 0001 0000 0002 0009 000b"
		+"000c 0001 0009 0000 0025 0002 0001 0000"
		+"0009 b200 0212 03b6 0004 b100 0000 0100"
		+"0a00 0000 0a00 0200 0000 0600 0800 0700"
		+"0100 0d00 0000 0200 0e").replace(" ","");

	private static byte hexToByte(String hex) {

		int dig1 = toDigit(hex.charAt(0));
		int dig2 = toDigit(hex.charAt(1));

		return (byte) ((dig1 << 4) + dig2);
	}
	 
	private static int toDigit(char hex) {

		int dig = Character.digit(hex, 16);

		if(dig >= 0) return dig;

		throw new IllegalArgumentException(
			"Invalid Hex Char: " + hex
		);
	}

	private static byte[] decodeHexString(String hex) {

		if(hex.length() % 2 != 0)
			throw new IllegalArgumentException(
				"Invalid Hex String: " + hex
			);

		byte[] bytes = new byte[hex.length() / 2];

		for(int idx = 0; idx < hex.length(); idx += 2)
			bytes[idx / 2] = hexToByte(hex.substring(idx, idx + 2));

		return bytes;
	}

	@Test
	public void testExecuteTask() {

		Node node = new Node();

		Task helloWorld = new Task(
			"HelloWorld",
			"{ \""+Task.ARGSKEY+"\" : \"\" }",
			decodeHexString(helloWorldByteCode)
		);

		TaskResult result = node.executeTask(helloWorld);

		assertEquals(0, result.errorCode);
		assertEquals("", result.errorMessage);
		assertEquals(TaskOutcome.SUCCESS, result.taskOutcome);
		assertEquals("Hello World", new String(result.resultBuffer));
	}

	@Test
	public void testRequest() {

		commMgr.setMockResponse(TDSResponse.getMockResponse());

		Node node = new Node();

		TDSResponse compareResponse = TDSResponse.getMockResponse();
		TDSRequest  inRequest 		= TDSRequest.getMockRequest();

		TDSResponse outResponse 	= node.sendRequest(inRequest);

		assertTrue(compareResponse.equals(outResponse));

		commMgr.unsetMockResponse();
	}

	@Test
	public void testPostResult() throws RepositoryException {

		// Result requires task and client in place

		Client 	client 		= TestUtil.randomClient();
		int 	clientId  	= Repositories.getClientRepository().add(client);
		Task 	task 	  	= TestUtil.randomTask(clientId);
		int 	taskId 	  	= Repositories.getTaskRepository().add(task);

		Node node = new Node();

		TaskResult result 	 = TestUtil.randomResult(taskId);

		TDSResponse response = node.postResult(result);

		assertEquals(200, response.getErrorCode());
		assertTrue(response.getStatus());
	}

	@Test
	public void testRegisterNode() {

		Node node = new Node();

		TDSResponse response = node.registerNode();

		assertEquals("Success", response.getErrorMessage());
		assertEquals(200, response.getErrorCode());
		assertTrue(response.getStatus());
	}

	@Test
	public void testStartUpShutDown() throws IOException {

		Node node = new Node();

		node.startUp();

		assertTrue(node.isAvailable());

		node.shutDown();

		assertEquals(NodeState.NOT_OPERATIONAL, node.getState());
	}

	@Test
	public void testTaskReceive()
		throws IOException,
		RepositoryException,
		UnknownHostException,
		InterruptedException {

		Node node = new Node();

		node.startUp();

		Client 	client 		= TestUtil.randomClient();
		int 	clientId  	= Repositories.getClientRepository().add(client);

		Task helloWorld 	= new Task(
			"HelloWorld",
			"{ \""+Task.ARGSKEY+"\" : \"\" }",
			clientId,
			decodeHexString(helloWorldByteCode)
		);

		int 	taskId 	  	= Repositories.getTaskRepository().add(helloWorld);

		Socket socket = new Socket(
			"127.0.0.1",
			TDSConfiguration.getNodePort()
		);

		BufferedReader socketIn = new BufferedReader(
									new InputStreamReader(
										socket.getInputStream()
									)
								);

		PrintWriter socketOut 	= new PrintWriter(
									socket.getOutputStream(),
									true
								);

		TDSRequest request = new TDSRequest("task-assign");

		request.setSourceIp(socket.getLocalAddress().getHostAddress());
		request.setSourcePort(socket.getLocalPort());
		request.setDestIp("127.0.0.1");
		request.setDestPort(TDSConfiguration.getNodePort());

		request.addParameter("taskId", Integer.toString(taskId));
		request.addParameter("taskName", helloWorld.getTaskName());
		request.addParameter("taskParameters", helloWorld.getTaskParameters());
		request.setData(helloWorld.getProgramBytes());

		socketOut.println(serializer.serialize(request));

		TDSResponse response = (TDSResponse)
			serializer.deserialize(socketIn.readLine());

		socket.close();

		node.shutDown();

		assertTrue(TDSResponse
			.goodRequest(request)
			.equals(response)
		);
	}
}