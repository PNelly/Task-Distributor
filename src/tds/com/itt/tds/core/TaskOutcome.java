package com.itt.tds.core;

import java.util.*;

public enum TaskOutcome {

	/*
		Enum requires specific values to correspond with
		enums used in the node database table

		private field with constructor necessary to
		satisfy compiler
	*/

	SUCCESS(1), FAILED(2);

	private final int number;

	TaskOutcome(int number){

		this.number = number;
	}

	public static String toString(TaskOutcome outcome) {

		switch (outcome){

			case SUCCESS:
				return "SUCCESS";

			case FAILED:
				return "FAILED";

			default:
				return "FAILED";
		}
	}

	public static TaskOutcome fromString(String outcomeString) throws TDSEnumException {

		switch (outcomeString){

			case "SUCCESS":
				return SUCCESS;

			case "FAILED":
				return FAILED;

			default:
				throw new TDSEnumException("unsupported task outcome string");
		}
	}

	public boolean equals(TaskOutcome other){

		return (this.number == other.number);
	}
}