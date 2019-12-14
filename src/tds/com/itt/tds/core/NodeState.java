package com.itt.tds.core;

import java.util.*;

public enum NodeState {

	/*
		Enum requires specific values to correspond with
		enums used in the node database table

		private field with constructor necessary to
		satisfy compiler
	*/

	AVAILABLE(1), BUSY(2), NOT_OPERATIONAL(3);

	private final int number;

	NodeState(int number){

		this.number = number;
	}

	public static String toString(NodeState state) throws TDSEnumException {

		switch (state){

			case AVAILABLE:
				return "AVAILABLE";

			case BUSY:	
				return "BUSY";

			case NOT_OPERATIONAL:
				return "NOT_OPERATIONAL";

			default:
				throw new TDSEnumException("unsupported node state");
		}
	}

	public static NodeState fromString(String stateString) throws TDSEnumException {

		switch (stateString){

			case "AVAILABLE":
				return AVAILABLE;

			case "BUSY":
				return BUSY;

			case "NOT_OPERATIONAL":
				return NOT_OPERATIONAL;

			default:
				throw new TDSEnumException("unsupported node state string");
		}
	}
}