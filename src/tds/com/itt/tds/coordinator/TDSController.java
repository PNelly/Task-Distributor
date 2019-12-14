package com.itt.tds.coordinator;

import com.itt.tds.comm.*;

public interface TDSController {

	public TDSResponse processRequest(TDSRequest request);
}