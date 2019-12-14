package com.itt.tds.comm;

import java.util.*;
import java.lang.reflect.*;

import com.itt.tds.cfg.*;

public class TDSResponse extends TDSProtocol {

    /*
        Response Specific Key Value Constants
    */

    public static final String STATUSLITERAL       = "status";
    public static final String ERRORCODELITERAL    = "error-code";
    public static final String ERRORMSGLITERAL     = "error-message";

    /*
        Static Methods to assist testing
    */

    public static TDSResponse getMockResponse() {

        final String plcHldrSrcIp    = "127.0.0.1";
        final String plcHldrDestIp   = "192.168.0.1";
        final int    plcHldrSrcPort  = 8291;
        final int    plcHldrDestPort = 3720;
        final String protocolFormat  = TDSConfiguration.getProtocolFormat();
        final String protocolVersion = TDSConfiguration.getProtocolVersion();

        TDSResponse response = new TDSResponse( protocolFormat,
                                                protocolVersion,
                                                plcHldrSrcIp,
                                                plcHldrSrcPort,
                                                plcHldrDestIp,
                                                plcHldrDestPort
        );

        response.setStatus(true);
        response.setErrorCode(200);
        response.setErrorMessage("Humans Destroyed");
        response.setData(TDSProtocol.getMockData());

        return response;
    }

    public static String getMockResponseJson() {

        final String responseJson = "{\"TDSProtocolFormat\":\"json\",\"TDSProtocolVersion\":\"1.0\",\"type\":\"response\",\"sourceIp\":\"127.0.0.1\",\"sourcePort\":8291,\"destIp\":\"192.168.0.1\",\"destPort\":3720,\"header\":{\"error-message\":\"Humans Destroyed\",\"error-code\":\"200\",\"status\":\"true\"},\"payload\":\"AQACAAM=\"}";
    
        return responseJson;
    }

    /*
        Static methods for general cases
    */

    private static TDSResponse reflectRequest(TDSRequest request){

        // mirror ip and port information from source to dest //

        TDSResponse response = new TDSResponse(
            TDSConfiguration.getProtocolFormat(),
            TDSConfiguration.getProtocolVersion(),
            request.getDestIp(),
            request.getDestPort(),
            request.getSourceIp(),
            request.getSourcePort()
        );

        return response;
    }

    public static TDSResponse badRequest(TDSRequest request){

        TDSResponse response = reflectRequest(request);

        response.setStatus(false);
        response.setErrorCode(400);
        response.setErrorMessage("Bad Request");

        return response;
    }

    public static TDSResponse goodRequest(TDSRequest request){

        TDSResponse response = reflectRequest(request);

        response.setStatus(true);
        response.setErrorCode(200);
        response.setErrorMessage("Success");

        return response;
    }

    public static TDSResponse serverError(TDSRequest request){

        TDSResponse response = reflectRequest(request);

        response.setStatus(false);
        response.setErrorCode(500);
        response.setErrorMessage("Internal Server Error");

        return response;
    }

    /*
        Instance Methods
    */

    public TDSResponse(
        String format, String version,
        String srcIp, int srcPort, 
        String destIp, int destPort) {

        super(format, version, srcIp, srcPort, destIp, destPort);

        this.protocolType = TDSProtocol.RESPONSELITERAL;
    }

    public TDSResponse(
        String format, String version,
        String srcIp, int srcPort, 
        String destIp, int destPort,
        HashMap<String,String> headers, byte[] data) {

        super(format, version, srcIp, srcPort, destIp, destPort, headers, data);

        this.protocolType = TDSProtocol.RESPONSELITERAL;
    }

    public boolean getStatus() {
        return Boolean.parseBoolean(getHeader(STATUSLITERAL));
    }

    public void setStatus(boolean status) {
        setHeader(STATUSLITERAL, status);
    }

    public int getErrorCode() { 
        return Integer.parseInt(getHeader(ERRORCODELITERAL));
    }

    public void setErrorCode(int errorCode) {
        setHeader(ERRORCODELITERAL, errorCode);
    }

    public String getErrorMessage() {
        return getHeader(ERRORMSGLITERAL);
    }

    public void setErrorMessage(String message) {
        setHeader(ERRORMSGLITERAL, message);
    }

    public String getValue(String key) {
        return getHeader(key);
    }

    public void setValue(String key, Object value) {
        setHeader(key,value);
    }
}