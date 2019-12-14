package com.itt.tds.node;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

import com.itt.tds.core.*;
import com.itt.tds.comm.*;
import com.itt.tds.cfg.*;

/*
    Worker node class
*/

public class Node {

	public static final String TASK_THREAD_NAME = "TDSTaskRunnerThread";

    private String          ip          	= "";
    private int             port        	= -1;
    private TDSSerializer   serializer  	= null;
    private TaskListener 	taskListener 	= null;

    private volatile NodeState 	nodeState 	= NodeState.NOT_OPERATIONAL;

    public Node() {

    	serializer = TDSSerializerFactory.getSerializer();
    }

    public void startUp() throws IOException {

    	openListener();

    	setAvailable();

    	registerNode();
    }

    public void shutDown() {

    	stopListener();

    	nodeState = NodeState.NOT_OPERATIONAL;
    }

    private void setAvailable(){

    	nodeState 	= NodeState.AVAILABLE;
    }

    private void setBusy(){

    	nodeState 	= NodeState.BUSY;
    }

    public boolean isAvailable(){

    	return (nodeState == NodeState.AVAILABLE);
    }

    public boolean isBusy(){

    	return (nodeState == NodeState.BUSY);
    }

    public NodeState getState(){
        return nodeState;
    }

    public TaskResult executeTask(Task taskInstance) {

        try {

            // save task to class file

            String path = "." 
                + TDSConfiguration.getPathSeparator() 
                + taskInstance.getTaskName()
                + ".class";
            
            File program = new File(path);

            FileOutputStream fos = new FileOutputStream(program.getAbsoluteFile());

            fos.write(taskInstance.getProgramBytes());
            fos.flush();
            fos.close();

            // execute and get output

            String command = "java " 
                + taskInstance.getTaskName()
                + taskInstance.getCommandLineArgs(); 

            Process taskProcess = Runtime.getRuntime().exec(
                command, 
                null, 
                new File(System.getProperty("user.dir"))
            );

            BufferedReader procIn   = new BufferedReader(
                                        new InputStreamReader(
                                            taskProcess.getInputStream()
                                        )
                                    );

            BufferedReader procErr  = new BufferedReader(
                                        new InputStreamReader(
                                            taskProcess.getErrorStream()
                                        )
                                    );

            String line       = null;
            String taskOutput = "";
            String taskError  = "";

            while((line = procIn.readLine())  != null)
                taskOutput += line;

            while((line = procErr.readLine()) != null)
                taskError  += line;

            int result = taskProcess.waitFor();

            // clean up debris

            taskProcess.destroy();

            program.delete();

            // create result and return

            return new TaskResult(
                result,
                taskError,
                taskOutput.getBytes(StandardCharsets.UTF_8),
                taskInstance.getId(),
                TaskOutcome.SUCCESS
            );

        } catch (Exception e) {

            StringWriter sw = new StringWriter();

            e.printStackTrace(new PrintWriter(sw));

            String trace = sw.toString();

            String message = e.getClass().getName()
                + " : " + e.getMessage() + " \n "
                + trace + "\n";

            return new TaskResult(
                0,
                message,
                null,
                taskInstance.getId(),
                TaskOutcome.FAILED
            );
        }
    }

    /*
		Coordinator Messaging
    */

    public TDSResponse postResult(TaskResult result) {

    	TDSRequest request = new TDSRequest("node-result");

    	request.addParameter("errorCode", Integer.toString(result.errorCode));
    	request.addParameter("errorMessage", result.errorMessage);
    	request.addParameter("taskId", Integer.toString(result.taskId));
    	request.addParameter("taskOutcome", TaskOutcome.toString(result.taskOutcome));
    	request.setData(result.resultBuffer);

    	return sendRequest(request);
    }

    public TDSResponse registerNode() {

    	TDSRequest request = new TDSRequest("node-add");

    	request.addParameter("nodeState", nodeState.toString());

    	return sendRequest(request);
    }

    public TDSResponse sendRequest(TDSRequest request){

        TDSResponse response = null;

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

            PrintWriter socketOut   = new PrintWriter(
                                        socket.getOutputStream(),
                                        true 
                                    );

            request.setSourceIp(socket.getLocalAddress().getHostAddress());
            request.setSourcePort(socket.getLocalPort());
            request.setDestIp(TDSConfiguration.getCoordinatorIp());
            request.setDestPort(TDSConfiguration.getCoordinatorPort());

            socketOut.println(serializer.serialize(request));

            response = (TDSResponse) 
                serializer.deserialize(socketIn.readLine());

            socket.close();

        } catch (Exception e) {

            System.out.println(
                "Encountered "+e.getClass().getName()
            );

            System.out.println(e.getMessage());
        }

        return response;
    }

    /*
		Task Listener Management
    */

    public boolean openListener() throws IOException {

    	taskListener 	= new TaskListener(
    						TDSConfiguration.getNodePort()
						);

    	new Thread(taskListener).start();

    	return listening();
    }

    public boolean listening() {

    	return 	(taskListener.listening()
    				&& taskListener.bound()
    			);
    }

    public boolean stopListener(){

    	return 	(taskListener != null)
    			? taskListener.terminate()
    			: true;
    }

    /*
        Interior class to handle waiting for coordinator
        delegations on a separate thread
    */

    private class TaskListener implements Runnable {

        private volatile boolean listening  = false;
        private ServerSocket listenSocket   = null;

        public TaskListener(int port) throws IOException {

            listenSocket    = new ServerSocket(port);
            listening       = true;
        }

        @Override
        public void run() {

            while(listening){

                try {

                    Socket taskSocket = listenSocket.accept();

                    BufferedReader socketIn = new BufferedReader(
                                                new InputStreamReader(
                                                    taskSocket.getInputStream()
                                                )
                                            );

                    PrintWriter socketOut   = new PrintWriter(
                                                taskSocket.getOutputStream(),
                                                true
                                            );

                    TDSRequest taskRequest  =
                        (TDSRequest) serializer.deserialize(socketIn.readLine());

                    // reject request if not available

                	if(!isAvailable()){

                		TDSResponse busy = TDSResponse.badRequest(taskRequest);
                		busy.setErrorMessage("Unavailable");

                		socketOut.println(serializer.serialize(busy));

                		taskSocket.close();

                		continue;
                	}

                	// receive and acknowledge

                	Task task = new Task(
                		Integer.parseInt(taskRequest.getParameter("taskId")),
                		taskRequest.getParameter("taskName"),
                		taskRequest.getParameter("taskParameters"),
                		taskRequest.getData()
            		);

                	TDSResponse accepted = TDSResponse.goodRequest(taskRequest);

                	socketOut.println(serializer.serialize(accepted));

                	taskSocket.close();

                	// get to work

                	Thread taskThread = new Thread(new TaskRunner(task));

                	taskThread.setName(TASK_THREAD_NAME);
                	taskThread.start();

                } catch (IOException e) {

                	// return to loop and await next request
                }
            }
        }

        public boolean bound(){

        	return 	(listenSocket != null)
        			? listenSocket.isBound()
        			: false;
        }

        public boolean listening(){
            return listening;
        }

        public boolean terminate() {

            listening = false;

            try{
                listenSocket.close();
            } catch(IOException ioe){
                // nothing to be done if exception
                // thrown on shutdown
            }

            return (!listening() && listenSocket.isClosed());
        }
    }

    /*
		Interior Class for Task Execution
    */

	private class TaskRunner implements Runnable {

		Task task = null;

		public TaskRunner(Task task) {

			this.task = task;
		}

		@Override
		public void run(){

			setBusy();

			TaskResult result = executeTask(task);

			postResult(result);

			setAvailable();
		}
	}
}