package com.itt.tds.core;

public class TDSStartupException extends Exception {
	
	public TDSStartupException() {

		super();
	}

	public TDSStartupException(String message){

		super(message);
	}

	public TDSStartupException(String message, Throwable cause) {

		super(message, cause);
	}

	public TDSStartupException(Throwable cause) {

		super(cause);
	}
}