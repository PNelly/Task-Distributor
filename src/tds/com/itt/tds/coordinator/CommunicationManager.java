package com.itt.tds.coordinator;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;

import com.itt.tds.comm.*;
import com.itt.tds.cfg.TDSConfiguration;

public class CommunicationManager {

    private RequestListener requestListener = null;
    private TDSResponse     mockResponse    = null;
    private TDSSerializer   commSerializer  = null;

    public CommunicationManager(){

        commSerializer = TDSSerializerFactory.getSerializer();
    }

    public CommunicationManager(TDSResponse mockResponse){

        this();
        this.mockResponse = mockResponse;
    }

    public void setMockResponse(TDSResponse mockResponse){

        this.mockResponse = mockResponse;
    }

    public void unsetMockResponse(){

        this.mockResponse = null;
    }

    public boolean openListener() throws IOException {

        requestListener     = new RequestListener(
                                TDSConfiguration.getCoordinatorPort()
                            );

        new Thread(requestListener).start();

        return listening();
    }

    public boolean listening() {

        return  (requestListener.listening() 
                    && requestListener.bound()
                );
    }

    public boolean stopListener() {

        return  (requestListener!=null) 
                ? requestListener.terminate() 
                : true;
    }

    /*
        =================================================
        Class interior to Communication Handler to
        maintain blocking server socket on another thread
        =================================================
    */

    private class RequestListener implements Runnable {

        private volatile boolean listening      = false;

        private ServerSocket    serverSocket    = null;

        public RequestListener(int port) throws IOException {

            serverSocket    = new ServerSocket(port);
            listening       = true;
        }

        @Override
        public void run() {

            while(listening){

                try {

                    RequestHandler handler  = new RequestHandler(
                                                serverSocket.accept()
                                            );

                    new Thread(handler).start();

                } catch (IOException e){

                    // Return to loop and await next request
                }
            }
        }

        public boolean bound(){

            return (serverSocket != null) 
                   ? serverSocket.isBound() 
                   : false;
        }

        public boolean listening(){

            return listening;
        }

        public boolean terminate() {

            listening = false;

            try{
                serverSocket.close();
            } catch (IOException ioe){
                // Nothing to be done if exception
                // thrown on shutdown
            }

            return (!listening() && serverSocket.isClosed());
        }

        /*
            ==================================================
            Class Interior to RequestListener to perform
            needed actions with a request on a separate thread
            ==================================================
        */

        private class RequestHandler implements Runnable {

            private Socket          socket          = null;
            private BufferedReader  socketIn        = null;
            private PrintWriter     socketOut       = null;

            public RequestHandler(Socket socket) throws IOException {

                this.socket     = socket;
                this.socketIn   = new BufferedReader(
                                    new InputStreamReader(
                                        socket.getInputStream()
                                    )
                                );
                this.socketOut  = new PrintWriter(
                                    socket.getOutputStream(),
                                    true
                                );
            }

            @Override
            public void run() {

                try {

                    TDSRequest  request  = 
                        (TDSRequest) commSerializer.deserialize(socketIn.readLine());

                    TDSResponse response = handleRequest(request);

                    socketOut.println(commSerializer.serialize(response));

                    socket.close();

                } catch (IOException e) {

                    // Nothing to be done, allow thread to terminate
                }
            }

            private TDSResponse handleRequest(TDSRequest request){

                if(mockResponse != null) return mockResponse;

                try {

                    TDSController controller 
                        = RequestDispatcher.getController(request);

                    return controller.processRequest(request);

                } catch (IOException e){

                    TDSResponse response = TDSResponse.badRequest(request);

                    response.setErrorMessage("no such method");

                    return response;
                }                
            }
        }        
    }
}