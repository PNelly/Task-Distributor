package com.itt.tds.core;

import java.util.*;

public class TaskResult {

    public int 			errorCode;
    public String 		errorMessage;
    public byte[] 		resultBuffer;
    public int          taskId;
    public TaskOutcome  taskOutcome;
    
    public TaskResult(
    	int 		errorCode,
    	String 		errorMessage,
    	byte[] 		resultBuffer,
    	int 		taskId,
    	TaskOutcome outcome) {

    	this.errorCode 		= errorCode;
    	this.errorMessage	= errorMessage;
    	this.resultBuffer 	= resultBuffer;
    	this.taskId 		= taskId;
    	this.taskOutcome 	= outcome;
    }

    public boolean equals(TaskResult other){

    	if(this.errorCode != other.errorCode
    	||!this.errorMessage.equals(other.errorMessage)
    	||!Arrays.equals(this.resultBuffer, other.resultBuffer)
    	|| this.taskId != other.taskId
    	||!this.taskOutcome.equals(other.taskOutcome))
    		return false;

    	return true;
    }
}