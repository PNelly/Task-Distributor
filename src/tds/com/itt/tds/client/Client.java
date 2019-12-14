package com.itt.tds.client;

import java.util.*;
import java.net.Socket;
import java.io.*;
import java.lang.Runtime;
import java.lang.Process;

import com.itt.tds.core.*;
import com.itt.tds.comm.*;
import com.itt.tds.cfg.*;

public class Client {

    private String          hostName;
    private String          userName;
    private TDSSerializer   serializer;

    public Client() throws TDSStartupException {

        this.serializer = TDSSerializerFactory.getSerializer();
        this.userName   = queryUserName();
        this.hostName   = queryHostName();        
    }

    private String queryUserName() throws TDSStartupException {

        try {

            return System.getProperty("user.name");

        } catch (Exception e){

            throw new TDSStartupException(
                e.getClass().getName() + " : " + e.getMessage()
            );
        }
    }

    private String queryHostName() throws TDSStartupException {

        try {

            Process hostNameProc = Runtime.getRuntime().exec("hostname");

            BufferedReader procIn = new BufferedReader(
                                        new InputStreamReader(
                                            hostNameProc.getInputStream()
                                        )
                                    );

            String hostName = procIn.readLine();

            hostNameProc.destroy();
        
            return hostName;

        } catch (Exception e){

            throw new TDSStartupException(
                e.getClass().getName() + " : " + e.getMessage()
            );
        }
    }

    public int queueTask(Task task) throws CommunicationException, TDSEnumException {
        
        TDSRequest request = new TDSRequest("task-queue");

        request.addParameter("hostName", hostName);
        request.addParameter("userName", userName);
        request.addParameter("taskName", task.getTaskName());
        request.addParameter("taskParameters", task.getTaskParameters());
        request.setData(task.getProgramBytes());

        TDSResponse response = sendRequest(request);

        if(!response.getStatus()
        ||  response.getErrorCode() != 200
        ||  response.getValue("taskId") == null)
            return -1;
        else
            return Integer.parseInt(response.getValue("taskId"));
    }

    public TaskResult queryResult(int taskId) throws CommunicationException, TDSEnumException {

        TDSRequest request = new TDSRequest("task-result");

        request.addParameter("taskId", Integer.toString(taskId));

        TDSResponse response = sendRequest(request);

        if(response.getStatus()
        && response.getErrorCode() == 200)
            return new TaskResult(
                Integer.parseInt(response.getValue("errorCode")),
                response.getValue("errorMessage"),
                response.getData(),
                Integer.parseInt(response.getValue("taskId")),
                TaskOutcome.fromString(response.getValue("taskOutcome"))
            );
        else
            return null;
    }

    public TaskState queryStatus(int taskId) throws CommunicationException, TDSEnumException {

        TDSRequest request = new TDSRequest("task-status");

        request.addParameter("taskId", Integer.toString(taskId));

        TDSResponse response = sendRequest(request);

        if(!response.getStatus()
        ||  response.getErrorCode() != 200)
            return null;
        else
            return TaskState.fromString(response.getValue("taskStatus"));
    }

    public Task getTask(String name, String path, String parameters)
        throws IOException {

        File file = new File(path);

        FileInputStream fis = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];

        fis.read(bytes);
        fis.close();

        return new Task(name, parameters, bytes);
    }

    public TDSResponse sendRequest(TDSRequest request) throws CommunicationException {

        try {

            Socket socket = new Socket(
                TDSConfiguration.getCoordinatorIp(),
                TDSConfiguration.getCoordinatorPort(),
                null,
                0
            );

            BufferedReader socketIn = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()
                )
            );

            PrintWriter socketOut = new PrintWriter(
                socket.getOutputStream(),
                true 
            );

            request.setSourceIp(socket.getLocalAddress().getHostAddress());
            request.setSourcePort(socket.getLocalPort());
            request.setDestIp(TDSConfiguration.getCoordinatorIp());
            request.setDestPort(TDSConfiguration.getCoordinatorPort());

            socketOut.println(serializer.serialize(request));

            TDSResponse response = 
                (TDSResponse) serializer.deserialize(socketIn.readLine());

            socket.close();

            return response;

        } catch(Exception e){

            String message = "Encountered "+e.getClass().getName()
                + ": " + e.getMessage();

            throw new CommunicationException(message);
        }
    }
}