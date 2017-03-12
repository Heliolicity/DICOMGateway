package com.gateway.dicom.test;

/*
 * Based on code provided by David Molloy
 * 
 * */

import java.net.*;

import com.gateway.dicom.protocols.C_ECHO_RQ;

import java.io.*;

public class ConnectionHandler extends Thread {

	private Socket clientSocket;				
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    public ConnectionHandler() {}
	
    public ConnectionHandler(Socket clientSocket) { this.clientSocket = clientSocket; }
    
    public void run() {

    	try {
		
    		this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
    		this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
    		
    		//Send a C-ECHO-RQ request
    		C_ECHO_RQ request = new C_ECHO_RQ();
    		this.send(request);
    		
    		while (this.receive()) {}
    		
		} 
		catch (IOException e) {
		
			pl("XX. There was a problem with the Input/Output Communication:");
			pl(e.getMessage());
			
		}
    	
    }
    
    private boolean receive() {
    	
    	boolean retval = true;
    	Object o;
    	
    	try {
    		
            o = this.inputStream.readObject();
            
        } 
    	
        catch (Exception e) {   
        	
        	this.closeSocket();
            retval = false;
            return retval;
            
        }
    	
        pl("01. <- Received an Object from the client (" + o.toString() + ").");
        
    	return retval;
    	
    }
    
    private void send(Object o) {

    	try {
            
    		System.out.println("02. -> Sending (" + o +") to the client.");
            this.outputStream.writeObject(o);
            this.outputStream.flush();
            
        }
    	
        catch (Exception e) {
        
        	System.out.println("XX." + e.getStackTrace());
        	
        }
    	
    }
    
    public void closeSocket() { 
    	
		try {
		
			this.outputStream.close();
			this.inputStream.close();
			this.clientSocket.close();
			
		} 
		
		catch (Exception e) {
		
			System.out.println("XX. " + e.getStackTrace());
			
		}
		
    }
    
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }
	
}
