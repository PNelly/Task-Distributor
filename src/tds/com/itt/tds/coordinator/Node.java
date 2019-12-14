package com.itt.tds.coordinator;

import java.util.*;
import java.net.Socket;

import com.itt.tds.core.Task;
import com.itt.tds.core.NodeState;

/*
    Server side representation of Node
*/

public class Node {

	private int        id;
    private int        port;
	private String     ip;
	private NodeState  state;
	private Socket     socket;

    /*
        Test Constructor
    */

    public Node(String ip, int port, NodeState state){

        this.ip         = ip;
        this.port       = port;
        this.state = state;

        socket = null;
        id = -1;
    }

    public Node(int id, String ip, int port, NodeState state) {

    	this.id 		= id;
    	this.ip 		= ip;
    	this.port 		= port;
    	this.state = state;

        socket = null;
    }

    public int getId(){
    	return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getIp(){
    	return ip;
    }

    public int getPort(){
    	return port;
    }

    public NodeState getState(){
    	return state;
    }

    public void sendTask(Task task) {
        // TODO implement here
    }
}