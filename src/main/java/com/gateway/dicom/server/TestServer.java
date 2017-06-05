package com.gateway.dicom.server;

import com.gateway.dicom.server.Server;

public class TestServer {

	public static void main(String args []) {
		
		Server server = new Server();
		server.setPort(5050);
		server.run();
		
	}
	
}
