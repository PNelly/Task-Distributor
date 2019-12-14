package com.itt.tds.coordinator;

import java.io.IOException;

import com.itt.tds.comm.*;

public class RequestDispatcher {

	public static TDSController getController(TDSRequest request) throws IOException {

		if(request.getMethod().startsWith("node-"))
			return new NodeController();

		if(request.getMethod().startsWith("task-"))
			return new ClientController();

		throw new IOException("no such method");
	}
}