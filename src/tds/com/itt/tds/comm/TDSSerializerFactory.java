package com.itt.tds.comm;

import com.itt.tds.cfg.*;

public class TDSSerializerFactory {
	
	public static TDSSerializer getSerializer(){

		if(TDSConfiguration.getProtocolFormat().equals("json"))
			return new JSONSerializer();

		return null;
	}
}