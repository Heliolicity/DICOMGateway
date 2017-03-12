package com.gateway.dicom.test;

public class Main {

	public static void main(String args []) {
		
		Server server = new Server();
		server.setPort(5050);
		server.activate();
		
	}
	
}
