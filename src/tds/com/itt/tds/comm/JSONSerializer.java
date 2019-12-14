package com.itt.tds.comm;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import com.google.gson.*;

public class JSONSerializer implements TDSSerializer {

	private static final String FORMAT 	= "json";
	private static final String VERSION	= "1.0";

	private JsonParser parser = null;

    public JSONSerializer() {

    	parser = new JsonParser();
    }

    @Override
    public String serialize(TDSProtocol protocol) {
        
    	JsonObject json = new JsonObject();

    	json.addProperty(TDSProtocol.FORMATKEY, 	protocol.getFormat());
    	json.addProperty(TDSProtocol.VERSIONKEY, 	protocol.getVersion());
    	json.addProperty(TDSProtocol.TYPEKEY, 		protocol.getType());
    	json.addProperty(TDSProtocol.SRCIPKEY, 		protocol.getSourceIp());
    	json.addProperty(TDSProtocol.SRCPORTKEY, 	protocol.getSourcePort());
    	json.addProperty(TDSProtocol.DESTIPKEY, 	protocol.getDestIp());
    	json.addProperty(TDSProtocol.DESTPORTKEY, 	protocol.getDestPort());

    	Map<String,String> headerMap = protocol.getHeaders();

        JsonObject header = new JsonObject();

    	for(Map.Entry<String,String> entry : headerMap.entrySet()){

    		header.addProperty(entry.getKey(), entry.getValue());
    	}

    	json.add(TDSProtocol.HEADERKEY, header);

        if(protocol.getData() != null)
            json.addProperty(
                TDSProtocol.PAYLOADKEY,
                Base64.getEncoder().encodeToString(protocol.getData())
            );

    	return json.toString();
    }

    @Override
    public TDSProtocol deserialize(String jsonIn) throws IOException {

        try {

    		JsonObject json = parser.parse(jsonIn).getAsJsonObject();

        	String 	dataVersion = json.getAsJsonPrimitive(TDSProtocol.VERSIONKEY).getAsString();
        	String 	dataFormat 	= json.getAsJsonPrimitive(TDSProtocol.FORMATKEY).getAsString();
        	String 	type 		= json.getAsJsonPrimitive(TDSProtocol.TYPEKEY).getAsString();

        	String 	sourceIp 	= json.getAsJsonPrimitive(TDSProtocol.SRCIPKEY).getAsString();
        	int 	sourcePort 	= json.getAsJsonPrimitive(TDSProtocol.SRCPORTKEY).getAsInt();
        	String 	destIp 		= json.getAsJsonPrimitive(TDSProtocol.DESTIPKEY).getAsString();
        	int 	destPort 	= json.getAsJsonPrimitive(TDSProtocol.DESTPORTKEY).getAsInt();

        	JsonObject jsonHeaders = json.getAsJsonObject(TDSProtocol.HEADERKEY);

        	HashMap<String,String> headers = new HashMap<String,String>();

        	for(Map.Entry<String, JsonElement> entry : jsonHeaders.entrySet()){

        		headers.put(entry.getKey(), entry.getValue().getAsJsonPrimitive().getAsString());
        	}

            byte[] data = null;

            if(json.has(TDSProtocol.PAYLOADKEY)){

                String payload = json.getAsJsonPrimitive(TDSProtocol.PAYLOADKEY).getAsString();

                data = Base64.getDecoder().decode(payload);
            }

            Class<? extends TDSProtocol> protocolClass = 
                TDSProtocol.getProtocolClass(type);

            Constructor protocolConstructor = 
                protocolClass.getConstructor(
                    TDSProtocol.deserializeConstructorParams
            );

            Object protocolObject = 
                protocolConstructor.newInstance(
                    FORMAT, 
                    VERSION, 
                    sourceIp, 
                    sourcePort, 
                    destIp, 
                    destPort, 
                    headers, 
                    data
            );

            return protocolClass.cast(protocolObject);

        } catch (Exception e){

            throw new IOException("Bad Protocol Json Format: "+jsonIn);
        }
    }
}