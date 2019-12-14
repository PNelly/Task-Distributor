package com.itt.tds.coordinator;

import java.util.*;

/*
	Server side representation of client
*/

public class Client {

	private int 	id;
	private String 	hostName;
	private String 	userName;

	public Client(int id, String hostName, String userName){

		this.id 		= id;
		this.hostName	= hostName;
		this.userName 	= userName;
	}

	public Client(String hostName, String userName){

		this.hostName = hostName;
		this.userName = userName;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getHostName(){
		return hostName;
	}

	public String getUserName(){
		return userName;
	}
}