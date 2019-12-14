package com.itt.tds.core;

import java.util.*;

public enum TaskState {

	/*
		Enum requires specific values to correspond with
		enums used in the node database table

		private field with constructor necessary to
		satisfy compiler
	*/

	PENDING(1), IN_PROGRESS(2), COMPLETED(3);

	private final int number;

	TaskState(int number){

		this.number = number;
	}

	public static String toString(TaskState state) throws TDSEnumException {

		switch (state) {

			case PENDING:
				return "PENDING";

			case IN_PROGRESS:
				return "IN_PROGRESS";

			case COMPLETED:
				return "COMPLETED";

			default:
				throw new TDSEnumException("unsupported task state");
		}
	}

	public static TaskState fromString(String stateString) throws TDSEnumException {

		switch (stateString) {

			case "PENDING":
				return PENDING;

			case "IN_PROGRESS":
				return IN_PROGRESS;

			case "COMPLETED":
				return COMPLETED;

			default:
				throw new TDSEnumException("unsupported task state string");
		}
	}
}