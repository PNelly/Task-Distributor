package com.itt.tds.comm;

import java.util.*;
import java.lang.reflect.*;

public class TDSProtocol {

    /*
        Protocol-Wide Key Value Constants
    */

	public static final String VERSIONKEY 	= "TDSProtocolVersion";
	public static final String FORMATKEY 	= "TDSProtocolFormat";
	public static final String TYPEKEY 		= "type";
	public static final String HEADERKEY 	= "header";
	public static final String PAYLOADKEY	= "payload";
	public static final String SRCIPKEY 	= "sourceIp";
	public static final String SRCPORTKEY 	= "sourcePort";
	public static final String DESTIPKEY 	= "destIp";
	public static final String DESTPORTKEY 	= "destPort";

    public static final String REQUESTLITERAL  = "request";
    public static final String RESPONSELITERAL = "response";

    /*
        Protocol-Wide String to Class
    */

    public static Class getProtocolClass(String type) throws ClassNotFoundException {

        if(type.equals(TDSProtocol.REQUESTLITERAL))
            return TDSRequest.class;
        else if (type.equals(TDSProtocol.RESPONSELITERAL))
            return TDSResponse.class;
        
        throw new ClassNotFoundException("no such protocol class: "+type);
    }

    public static final Class<?>[] deserializeConstructorParams = {
        String.class, 
        String.class, 
        String.class, 
        int.class, 
        String.class, 
        int.class, 
        HashMap.class, 
        byte[].class
    };

    /*
        Mock Data for Test Assistance
    */

    public static byte[] getMockData() {

        byte[] mockData = {1, 0, 2, 0, 3};

        return mockData;
    }

    /*
        Instance Members
    */

    protected String    protocolVersion;
    protected String    protocolFormat;

    private String 		sourceIp;
    private String 		destIp;
    private int 		sourcePort;
    private int 		destPort;

    protected String 	protocolType;

    private HashMap<String,String> headers = null;

    private byte[] data = null;

    public TDSProtocol() {

        headers = new HashMap<String,String>();
    }

    public TDSProtocol(
        String protocolFormat, String protocolVersion,
    	String sourceIp, int sourcePort, 
        String destIp, int destPort) {

            this();

        	this.protocolFormat 	= protocolFormat;
        	this.protocolVersion 	= protocolVersion;
        	this.sourceIp 			= sourceIp;
        	this.sourcePort			= sourcePort;
        	this.destIp 			= destIp;
        	this.destPort 			= destPort;
    }

    public TDSProtocol(
        String protocolFormat, String protocolVersion,
    	String sourceIp, int sourcePort, 
        String destIp, int destPort, 
    	HashMap<String,String> headers, byte[] data) {

        	this.protocolFormat 	= protocolFormat;
        	this.protocolVersion 	= protocolVersion;
        	this.sourceIp 			= sourceIp;
        	this.sourcePort 		= sourcePort;
        	this.destIp 			= destIp;
        	this.destPort 			= destPort;
        	this.headers 			= headers;
        	this.data 				= data;
    }

    public String getVersion(){
    	return protocolVersion;
    }

    public String getFormat(){
    	return protocolFormat;
    }

    public String getType(){
    	return protocolType;
    }

    public void setType(String type){
    	protocolType = type;
    }

    public String getSourceIp(){
    	return sourceIp;
    }

    public void setSourceIp(String ip){
        sourceIp = ip;
    }

    public String getDestIp(){
    	return destIp;
    }

    public void setDestIp(String ip){
        destIp = ip;
    }

    public int getSourcePort(){
    	return sourcePort;
    }

    public void setSourcePort(int port){
        sourcePort = port;
    }

    public int getDestPort(){
    	return destPort;
    }

    public void setDestPort(int port){
        destPort = port;
    }

    public HashMap<String,String> getHeaders(){
    	return headers;
    } 

    public void setHeader(String key, Object value) {
    	headers.put(key, value.toString());
    }

    public String getHeader(String key){
    	return headers.get(key);
    }

    public byte[] getData(){
    	return data;
    }

    public void setData(byte[] data){
    	this.data = data;
    }

    public String toString(){

    	String self = "\nversion : "+protocolVersion+"\n"
    		+ "format : "+protocolFormat+"\n"
    		+ "type : "+protocolType+"\n"
    		+ "srcIp : "+sourceIp+"\n"
    		+ "srcPort : "+sourcePort+"\n"
    		+ "destIp : "+destIp+"\n"
    		+ "destPort :"+destPort+"\n"
    		+ "headers :: \n";

    	for(Map.Entry<String,String> entry : headers.entrySet()){

    		self += entry.getKey()+" : "+entry.getValue()+"\n";
    	}

        self += (getData() != null)
              ? "data: "+getData().toString()
              : "data: no data";

    	return self;
    }

    public boolean equals(TDSProtocol other){

        if(!protocolVersion.equals(other.getVersion())
        || !protocolFormat.equals(other.getFormat())
        || !protocolType.equals(other.getType())
        || !sourceIp.equals(other.getSourceIp())
        ||  sourcePort != other.getSourcePort()
        || !destIp.equals(other.getDestIp())
        ||  destPort != other.getDestPort())
            return false;

        if(headers.size() != other.getHeaders().size())
            return false;

        for(Map.Entry<String,String> entry : headers.entrySet()){

            if(!headers.get(entry.getKey())
                .equals(other
                    .getHeaders()
                    .get(entry.getKey())
                )
            ) return false;
        }

        if(!Arrays.equals(data, other.getData()))
            return false;

        return true;
    }
}