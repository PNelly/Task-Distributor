package com.itt.tds.test;

import java.util.*;

import com.itt.tds.core.*;
import com.itt.tds.coordinator.Node;
import com.itt.tds.coordinator.db.repository.*;
import com.itt.tds.coordinator.Client;

public class TestUtil {
	
	private static Random random = new Random();

	public static int randomPort() {

		int lower = 49152;
		int upper = 65535;
		int range = upper -lower;

		return (lower + random.nextInt(range));
	}

	public static String randomString(int len) {

		int lower = 97;  // 'a'
		int upper = 122; // 'z'
		int range = upper -lower;

		StringBuilder builder = new StringBuilder(len);

		do {

			builder.append((char) (lower + random.nextInt(range)));

		} while(builder.length() < len);

		return builder.toString();
	}

	public static String randomJsonPair() {

		String json = "{\"" + randomString(16) + "\":\"" + randomString(16) + "\"}";

		return json;
	}

	public static String randomIp() {

		String str = "";

		for(int idx = 0; idx < 4; ++idx){
			str += Integer.toString(random.nextInt(255));

			if(idx < 3)
				str += ".";
		}

		return str;
	}

	public static Node randomNode() throws RepositoryException {

		String 		nodeIp 		= TestUtil.randomIp();
		int 		nodePort  	= TestUtil.randomPort();
		NodeState	nodeState 	= NodeState.AVAILABLE;

		while(Repositories.getNodeRepository().resolveNodeId(
				nodeIp,
				nodePort
				) > 0
			){

			nodeIp 		= TestUtil.randomIp();
			nodePort 	= TestUtil.randomPort();
		}

		return new Node(nodeIp, nodePort, nodeState);		
	}

	public static Client randomClient() throws RepositoryException {

		String hostName = TestUtil.randomString(16);
		String userName = TestUtil.randomString(16);

		while(Repositories.getClientRepository().resolveClientId(
				hostName,
				userName
				) > 0
			){

			hostName = TestUtil.randomString(16);
			userName = TestUtil.randomString(16);
		}

		return new Client(hostName, userName);		
	}

	public static Task randomTask(int clientId) {

		String taskName 		= randomString(16);
		String taskParameters	= randomJsonPair();

		byte[] taskBytes 		= new byte[16];

		random.nextBytes(taskBytes);

		return new Task(
			taskName,
			taskParameters,
			clientId,
			taskBytes
		);
	}

	public static TaskResult randomResult(int taskId) {

		byte[] resultBuff = new byte[16];

		random.nextBytes(resultBuff);

		return new TaskResult(
			200,
			"Success",
			resultBuff,
			taskId,
			TaskOutcome.SUCCESS
		);
	}
}