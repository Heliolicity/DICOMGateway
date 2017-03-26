package com.gateway.dicom.test;

import java.io.*;
import java.net.*;

public class ConquestTest {

	//192.168.1.23
	private String ipAddress;
	private int port;
	private Socket socket = null;
	private DataOutputStream dos = null;
    private DataInputStream dis = null;
	
	public ConquestTest(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public boolean connectToServer() {
		
		pl("HERE 1");
		
		try { // open a new socket to the server 
	    	
			pl("HERE 2");
			this.socket = new Socket(this.ipAddress, this.port);
			pl("HERE 3");
			this.dos = new DataOutputStream(this.socket.getOutputStream());
    		pl("HERE 4");
    		this.dis = new DataInputStream(this.socket.getInputStream());
    		pl("HERE 5");
    		pl("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		pl("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    		
    	} 
        
    	catch (Exception e) {
    		
    		pl("XX. Failed to Connect to the Server at port: " + this.port);
        	pl("    Exception: " + e.toString());
        	return false;
        
    	}
		
    	return true;
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
	public static void main(String args []) {
		
		ConquestTest conquestTest = new ConquestTest("localhost", 5678);
		conquestTest.connectToServer();
		
	}
	
}
