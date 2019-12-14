package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.*;
import java.util.*;
import java.io.IOException;
import com.itt.tds.comm.*;

public class TestSerializer {
	
	private static TDSSerializer serializer 	= TDSSerializerFactory.getSerializer();

	@Test
	public void testSerializeRequest() {

		TDSRequest request = TDSRequest.getMockRequest();

		String json = serializer.serialize(request);

		assertTrue(json.equals(TDSRequest.getMockRequestJson()));
	}

	@Test
	public void testSerializeResponse() {

		TDSResponse response = TDSResponse.getMockResponse();

		String json = serializer.serialize(response);

		assertTrue(json.equals(TDSResponse.getMockResponseJson()));
	}

	@Test
	public void testDeserializeRequest() throws IOException {

		TDSRequest inRequest  = TDSRequest.getMockRequest();

		TDSRequest outRequest = (TDSRequest) serializer.deserialize(TDSRequest.getMockRequestJson());

		assertEquals(
			inRequest.getSourceIp(),
			outRequest.getSourceIp()
		);

		assertEquals(
			inRequest.getSourcePort(),
			outRequest.getSourcePort()
		);

		assertEquals(
			inRequest.getDestIp(),
			outRequest.getDestIp()
		);

		assertEquals(
			inRequest.getDestPort(),
			outRequest.getDestPort()
		);

		assertEquals(
			inRequest.getType(),
			outRequest.getType()
		);

		assertEquals(
			inRequest.getMethod(),
			outRequest.getMethod()
		);

		assertEquals(
			inRequest.getHeaders().hashCode(),
			outRequest.getHeaders().hashCode()
		);

		assertEquals(
			Arrays.hashCode(inRequest.getData()),
			Arrays.hashCode(outRequest.getData())
		);
	}

	@Test
	public void testDeserializeResponse() throws IOException {

		TDSResponse inResponse  = TDSResponse.getMockResponse();

		TDSResponse outResponse = (TDSResponse) serializer.deserialize(TDSResponse.getMockResponseJson());

		assertEquals(
			inResponse.getSourceIp(),
			outResponse.getSourceIp()
		);

		assertEquals(
			inResponse.getSourcePort(),
			outResponse.getSourcePort()
		);

		assertEquals(
			inResponse.getDestIp(),
			outResponse.getDestIp()
		);

		assertEquals(
			inResponse.getDestPort(),
			outResponse.getDestPort()
		);

		assertEquals(
			inResponse.getType(),
			outResponse.getType()
		);

		assertEquals(
			inResponse.getStatus(),
			outResponse.getStatus()
		);

		assertEquals(
			inResponse.getErrorCode(),
			outResponse.getErrorCode()
		);

		assertEquals(
			inResponse.getErrorMessage(),
			outResponse.getErrorMessage()
		);

		assertEquals(
			inResponse.getHeaders().hashCode(),
			outResponse.getHeaders().hashCode()
		);

		assertEquals(
			Arrays.hashCode(inResponse.getData()),
			Arrays.hashCode(outResponse.getData())
		);
	}
}