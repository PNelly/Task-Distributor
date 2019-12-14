package com.itt.tds.core;

import java.util.*;

import com.google.gson.*;

public class Task {

    public static final String ARGSKEY = "args";

    private String      taskName;
    private String      taskParameters;
    private String      taskExePath;

    private TaskState   taskState;

    private TaskResult  taskResult;

    private byte[]      programBytes;

    private int         id;
    private int         userId;
    private int         assignedNodeId;

    /*
        Constructor for Coordinator Receipt
    */

    public Task(String taskName, String taskParameters,
        int userId, byte[] programBytes){

        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.userId         = userId;
        this.programBytes   = programBytes;

        this.taskState      = TaskState.PENDING;

        this.taskExePath    = "";
        this.taskResult     = null;
        this.id             = -1;
        this.assignedNodeId = -1;
    }

    /*
        Constructor for Client file read
    */

    public Task(String taskName, String taskParameters,
        byte[] programBytes){

        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.programBytes   = programBytes;

        this.id             = -1;
        this.userId         = -1;
        this.taskState      = null;
        this.taskExePath    = "";
        this.taskResult     = null;
        this.assignedNodeId = -1;
    }

    /*
        Constructor for Node Receipt
    */

    public Task(int id, String taskName, String taskParameters,
        byte[] programBytes){

        this.id             = id;
        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.programBytes   = programBytes;
        this.taskState      = TaskState.IN_PROGRESS;

        this.userId         = -1;
        this.taskExePath    = "";
        this.taskResult     = null;
        this.assignedNodeId = -1;
    }

    /*
        Test Constructors
    */

    public Task(byte[] programBytes){

        this.programBytes = programBytes;
    }

    public Task(String taskName, String taskParameters, String taskExePath,
        TaskState taskState, int userId, int assignedNodeId){

        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.taskExePath    = taskExePath;
        this.taskState      = taskState;
        this.userId         = userId;
        this.assignedNodeId = assignedNodeId;
    }

    public Task(int id, String taskName, String taskParameters, String taskExePath,
        TaskState taskState, int userId, int assignedNodeId){

        this.id             = id;
        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.taskExePath    = taskExePath;
        this.taskState      = taskState;
        this.userId         = userId;
        this.assignedNodeId = assignedNodeId;
    }

    public Task(String taskName, String taskParameters, String taskExePath,
        TaskState taskState, TaskResult taskResult, int id, 
        byte[] programBytes, int userId, int assignedNodeId) {

        this.taskName       = taskName;
        this.taskParameters = taskParameters;
        this.taskExePath    = taskExePath;
        this.taskState      = taskState;
        this.taskResult     = taskResult;
        this.id             = id;
        this.programBytes   = programBytes;
        this.userId         = userId;
        this.assignedNodeId = assignedNodeId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskExePath(String taskExePath) {
        this.taskExePath = taskExePath;
    }

    public String getTaskExePath() {
        return taskExePath;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskParameters(String taskParameters){
        this.taskParameters = taskParameters;
    }

    public String getTaskParameters(){
        return taskParameters;
    }

    public String getCommandLineArgs() throws JsonSyntaxException {

        JsonObject params 
            = (new JsonParser()).parse(taskParameters).getAsJsonObject();

        return params.getAsJsonPrimitive(ARGSKEY).getAsString();
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProgramBytes(byte[] programBytes){
        this.programBytes = programBytes;
    }

    public byte[] getProgramBytes(){
        return programBytes;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public int getUserId(){
        return userId;
    }

    public int getAssignedNodeId(){
        return assignedNodeId;
    }
}