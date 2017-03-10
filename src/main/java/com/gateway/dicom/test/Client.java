package com.gateway.dicom.test;

/*
 * Based on code provided by David Molloy
 * 
 * */

import java.net.*;

import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.protocols.C_ECHO_RSP;

import java.io.*;

public class Client {
	
	private static int portNumber = 5050;
    private Socket socket = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;
    private boolean listening = false;

	// the constructor expects the IP address of the server - the port is fixed
    public Client(String serverIP) {
    	
    	if (! connectToServer(serverIP)) {
    	
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);            
    	
    	}
    	
    }

    private boolean connectToServer(String serverIP) {
    	
    	try { // open a new socket to the server 
    	
    		this.socket = new Socket(serverIP,portNumber);
    		this.os = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    		this.listening = true;
    	
    	} 
        
    	catch (Exception e) {
        
    		System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
        	System.out.println("    Exception: " + e.toString());
        	this.listening = false;
        	return false;
        
    	}
		
    	return true;
    
    }

    private void getDate() {
    
    	String theDateCommand = "GetDate", theDateAndTime;
    	System.out.println("01. -> Sending Command (" + theDateCommand + ") to the server...");
    	this.send(theDateCommand);
    	
    	try{
    	
    		theDateAndTime = (String) receive();
    		System.out.println("05. <- The Server responded with: ");
    		System.out.println("    <- " + theDateAndTime);
    		
    	}
    	
    	catch (Exception e){
    	
    		System.out.println("XX. There was an invalid object sent back from the server");
    	
    	}
    	
    	System.out.println("06. -- Disconnected from Server.");
    
    }
	
    // method to send a generic object.
    private void send(Object o) {
    	
		try {
			
		    System.out.println("02. -> Sending an object...");
		    os.writeObject(o);
		    os.flush();
		    
		} 
	    
		catch (Exception e) {
		
			System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		
		}
    
    }

    // method to receive a generic object.
    private Object receive() {

    	Object o = null;
		
    	try {

    		System.out.println("03. -- About to receive an object...");
		    o = is.readObject();
		    C_ECHO_RQ request = (C_ECHO_RQ) o;
		    System.out.println("04. <- Object received: " + request.toString());
		    DataElement element = request.getMessageID();
		    
		    long mid = new Long(element.getElementData()).longValue();
		    
		    if (mid > 0) {
		    
			    C_ECHO_RSP response = new C_ECHO_RSP(element);
			    this.send(response);
			    this.listening = false;
			    
		    }
		    
    	} 
	    
    	catch (Exception e) {
		
    		System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		
    	}
		
    	return o;
    
    }

    public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public static void main(String args[]) {
    	
    	System.out.println("**. Java Client Application - EE592 DICOM Gateway Project, DCU");
    	
    	if (args.length == 1) {
    	
    		Client theApp = new Client(args[0]);
		    //theApp.getDate();
    		
    		while (theApp.listening) {
    		
    			C_ECHO_RQ request = (C_ECHO_RQ) theApp.receive();
    		
    		}
    		
    	}
    	
    	else {
    		
    		System.out.println("Error: you must provide the address of the server");
    		System.out.println("Usage is:  java Client x.x.x.x  (e.g. java Client 192.168.7.2)");
    		System.out.println("      or:  java Client hostname (e.g. java Client localhost)");
    	
    	}    
    	
    	System.out.println("**. End of Application.");
    
    }

}