package com.itt.tds.coordinator;

import java.util.*;

import com.itt.tds.comm.*;
import com.itt.tds.core.*;
import com.itt.tds.coordinator.db.repository.*;

public class NodeController implements TDSController {

    public NodeController() {

    }

    @Override
    public TDSResponse processRequest(TDSRequest request){

        String method = request.getMethod();

        if(method.equals("node-add")){

            return registerNode(request);

        } else if(method.equals("node-result")){

            return saveResult(request);

        } else {

            TDSResponse noMethod = TDSResponse.badRequest(request);
            noMethod.setErrorMessage("no such method");

            return noMethod;
        }
    }

    private TDSResponse registerNode(TDSRequest request) {
        
        Node node = null;

        try {

            node = new Node(
                request.getSourceIp(), 
                request.getSourcePort(), 
                NodeState.fromString(request.getParameter("nodeState"))
            );

        } catch (Exception e) {

            TDSResponse badRequest = TDSResponse.badRequest(request);
            badRequest.setErrorMessage(e.getMessage());

            return badRequest;      
        }

        try {

            Repositories.getNodeRepository().add(node);
            
            return TDSResponse.goodRequest(request);

        } catch (RepositoryException e) {

            TDSResponse errResponse = TDSResponse.serverError(request);
            errResponse.setErrorMessage(e.getMessage());

            return errResponse;
        }
    }

    private TDSResponse saveResult(TDSRequest request) {
        
        TaskResult taskResult = null;

        try {

            taskResult = new TaskResult(
                Integer.parseInt(request.getParameter("errorCode")),
                request.getParameter("errorMessage"),
                request.getData(),
                Integer.parseInt(request.getParameter("taskId")),
                TaskOutcome.fromString(request.getParameter("taskOutcome"))
            );

        } catch (Exception e) {

            TDSResponse badRequest = TDSResponse.badRequest(request);
            badRequest.setErrorMessage(e.getMessage());

            return badRequest;
        }

        try {     

            Repositories.getTaskResultRepository().add(taskResult);

            return TDSResponse.goodRequest(request);

        } catch (RepositoryException e) {

            TDSResponse errResponse = TDSResponse.serverError(request);
            errResponse.setErrorMessage(e.getMessage());

            return errResponse;
        }
    }
}