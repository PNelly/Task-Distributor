package com.itt.tds.coordinator;

import java.util.*;

import com.itt.tds.comm.*;
import com.itt.tds.core.*;
import com.itt.tds.coordinator.db.repository.*;

public class ClientController implements TDSController {

    public ClientController() {

    }

    @Override
    public TDSResponse processRequest(TDSRequest request){

        String method = request.getMethod();

        if(method.equals("task-queue")){

            return queueTask(request);

        } else if(method.equals("task-result")){

            return queryResult(request);

        } else if(method.equals("task-status")){

            return queryStatus(request);

        } else {

            TDSResponse noMethod = TDSResponse.badRequest(request);
            noMethod.setErrorMessage("no such method");

            return noMethod;
        }
    }

    private TDSResponse queueTask(TDSRequest request) {

        try {

            Client client = null;
            Task   task   = null;

            String hostName = request.getParameter("hostName");
            String userName = request.getParameter("userName");

            client   = new Client(hostName, userName);

            client.setId(Repositories.getClientRepository().add(client));

            String taskName         = request.getParameter("taskName");
            String taskParameters   = request.getParameter("taskParameters");
            byte[] taskProgram      = request.getData();

            task    = new Task(taskName, taskParameters, client.getId(), taskProgram);

            task.setId(Repositories.getTaskRepository().add(task));

            task.setTaskExePath(TaskBytesStorage.storeTaskBytes(task));

            Repositories.getTaskRepository().modify(task);

            TDSResponse response = TDSResponse.goodRequest(request);
            response.setValue("taskId", task.getId());

            return response;

        } catch (Exception e){

            String name     = e.getClass().getName();
            String message  = e.getMessage();
            String err      = name + ": " + message;

            TDSResponse response = TDSResponse.serverError(request);

            response.setErrorMessage(err);

            return response;
        }
    }

    private TDSResponse queryResult(TDSRequest request) {
        
        try {

            int taskId = Integer.parseInt(request.getParameter("taskId"));

            TaskResult result = Repositories.getTaskResultRepository().getResultByTaskId(taskId);

            if(result != null){

                TDSResponse response = TDSResponse.goodRequest(request);

                response.setValue("errorCode",      result.errorCode);
                response.setValue("errorMessage",   result.errorMessage);
                response.setValue("taskId",         taskId);
                response.setValue("taskOutcome",    result.taskOutcome.toString());
                response.setData(result.resultBuffer);

                return response;

            } else {

                TDSResponse response = TDSResponse.badRequest(request);

                response.setErrorMessage("no result");

                return response;
            }

        } catch (RepositoryException e){

            return TDSResponse.serverError(request);
        }
    }

    private TDSResponse queryStatus(TDSRequest request) {

        try {

            int taskId = Integer.parseInt(request.getParameter("taskId"));

            Task task  = Repositories.getTaskRepository().getTaskById(taskId);

            TaskState state = task.getTaskState();
            
            TDSResponse response = TDSResponse.goodRequest(request);

            response.setValue("taskStatus", state.toString());

            return response;

        } catch (RepositoryException e){

            return TDSResponse.serverError(request);
        }
    }
}