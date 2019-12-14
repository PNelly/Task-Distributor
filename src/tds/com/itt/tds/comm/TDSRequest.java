package com.itt.tds.comm;

import java.util.*;
import java.lang.reflect.*;

import com.itt.tds.cfg.*;

public class TDSRequest extends TDSProtocol {

    /*
        Request Specific Key Value Constants
    */

    public static final String METHODLITERAL   = "method";

    /*
        Static methods to assist testing
    */

    public static TDSRequest getMockRequest() {

        final String plcHldrSrcIp    = "127.0.0.1";
        final String plcHldrDestIp   = "192.168.0.1";
        final int    plcHldrSrcPort  = 8291;
        final int    plcHldrDestPort = 3720;
        final String protocolFormat  = TDSConfiguration.getProtocolFormat();
        final String protocolVersion = TDSConfiguration.getProtocolVersion();

        TDSRequest request = new TDSRequest(
                                    protocolFormat,
                                    protocolVersion,
                                    plcHldrSrcIp,
                                    plcHldrSrcPort,
                                    plcHldrDestIp,
                                    plcHldrDestPort
        );

        request.setMethod("destroyAllHumans");
        request.addParameter("Planet","Earth");
        request.addParameter("Moons",1);
        request.setData(TDSProtocol.getMockData());

        return request;
    }

    public static String getMockRequestJson() {

        final String requestJson    = "{\"TDSProtocolFormat\":\"json\",\"TDSProtocolVersion\":\"1.0\",\"type\":\"request\",\"sourceIp\":\"127.0.0.1\",\"sourcePort\":8291,\"destIp\":\"192.168.0.1\",\"destPort\":3720,\"header\":{\"Moons\":\"1\",\"method\":\"destroyAllHumans\",\"Planet\":\"Earth\"},\"payload\":\"AQACAAM=\"}";
    
        return requestJson;
    }

    /*
        Instance Methods
    */

    /*
        Client Constructor
    */

    public TDSRequest(String method){

        super();

        this.protocolType    = TDSProtocol.REQUESTLITERAL;
        this.protocolFormat  = TDSConfiguration.getProtocolFormat();
        this.protocolVersion = TDSConfiguration.getProtocolVersion();

        setMethod(method);
    }

    /*
        Test Constructors
    */

    public TDSRequest(
        String format, String version,
        String srcIp, int srcPort, 
        String destIp, int destPort) {

        super(format, version, srcIp, srcPort, destIp, destPort);

        this.protocolType = TDSProtocol.REQUESTLITERAL;
    }

    public TDSRequest(
        String format, String version,
        String srcIp, int srcPort, 
        String destIp, int destPort, 
        HashMap<String,String> headers, byte[] data) {

        super(format, version, srcIp, srcPort, destIp, destPort, headers, data);

        this.protocolType = TDSProtocol.REQUESTLITERAL;
    }

    public String getMethod() {
        return getHeader(METHODLITERAL);
    }

    public void setMethod(String method) {
        setHeader(METHODLITERAL, method);
    }

    public String getParameter(String key) {
        return getHeader(key);
    }

    public void addParameter(String key, Object value) {
        setHeader(key, value);
    }
}