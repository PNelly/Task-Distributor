package com.itt.tds.comm;

import java.util.*;
import java.io.IOException;

public interface TDSSerializer {

	String serialize(TDSProtocol protocol);

	TDSProtocol deserialize(String data) throws IOException;
}