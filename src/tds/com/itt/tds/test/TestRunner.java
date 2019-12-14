package com.itt.tds.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.itt.tds.test.*;

public class TestRunner {

	public static void main(String[] args){

		Result result = JUnitCore.runClasses(
			TestTDSConfiguration.class,
			TestDBManager.class,
			TestClientRepository.class,
			TestNodeRepository.class,
			TestTaskRepository.class,
			TestTaskResultRepository.class,
			TestSerializer.class,
			TestCommunicationManager.class,
			TestClient.class,
			TestNodeController.class,
			TestClientController.class,
			TestTaskBytesStorage.class,
			TestNode.class
		);

		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}

		String message = (result.wasSuccessful())
					   ? "\n#### All Tests Passed ####\n"
					   : "\n#### Test Failure ####\n";

		System.out.println(message);
	}
}