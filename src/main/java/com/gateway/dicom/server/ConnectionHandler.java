package com.gateway.dicom.server;

/*
 * Based on code provided by David Molloy
 * 
 * */

import java.net.*;
import java.io.*;

import com.gateway.dicom.lib.DicomValueRepresentationInputStream;
import com.gateway.dicom.lib.DicomValueRepresentationOutputStream;
import com.gateway.dicom.protocols.C_ECHO_RQ;

public class ConnectionHandler extends Thread {

	private Socket clientSocket;				
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
	private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private DicomValueRepresentationInputStream inputStream;
    private DicomValueRepresentationOutputStream outputStream;
    private ByteArrayOutputStream byteOutputStream;
    private ByteArrayInputStream byteInputStream;

    
    public ConnectionHandler() {}
	
    public ConnectionHandler(Socket clientSocket) { this.clientSocket = clientSocket; }
    
    public void run() {

    	pl("HERE 1");
    	
    	try {
		
    		//this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
    		//this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
    		pl("HERE 2");
    		this.inputStream = new DicomValueRepresentationInputStream(this.clientSocket.getInputStream(), DicomValueRepresentationOutputStream.BYTE_ORDERING_LITTLE_ENDIAN);
    		this.outputStream = new DicomValueRepresentationOutputStream(this.clientSocket.getOutputStream(), DicomValueRepresentationOutputStream.BYTE_ORDERING_LITTLE_ENDIAN);
    		pl("HERE 3");
    		
    		//Send a C-ECHO-RQ request
    		//C_ECHO_RQ request = new C_ECHO_RQ();
    		//this.send(request);
    		
    		while (this.receive()) {
    			
    			//pl("HERE 4");
    			Thread.sleep(1000);
    			
    			
    		}
    		
		} 
    	
		catch (Exception e) {
		
			pl("ERROR HERE");
			e.printStackTrace();
			pl("XX. There was a problem with the Input/Output Communication:");
			pl(e.getMessage());
			
		}
    	
    }
    
    private boolean receive() {
    	
    	boolean retval = true;
    	Object o;
    	String s;
    	byte[] arr;
    	int i;
    	
    	try {
    		//o = this.inputStream.readObject();
    		//s = this.inputStream.readLine();
    		//pl("GOT THIS: " + s);
    		
    		pl("IN SERVER.  RECEIVING");
    		
    		arr = this.inputStream.readByteArray(10);

    		for (int a = 0; a < arr.length; a ++) p("" + arr[a]);
    		
    		/*i = this.inputStream.readUInt32();
    		
    		pl("" + i);*/
    		
        } 
    	
        catch (Exception e) {   
        	
        	this.closeSocket();
            retval = false;
            return retval;
            
        }
    	
        //pl("01. <- Received an Object from the client (" + o.toString() + ").");
        
    	return retval;
    	
    }
    
    private void send(Object o) {

    	try {
            
    		System.out.println("02. -> Sending (" + o +") to the client.");
            //this.outputStream.writeObject(o);
            //this.outputStream.flush();
            
        }
    	
        catch (Exception e) {
        
        	System.out.println("XX." + e.getStackTrace());
        	
        }
    	
    }
    
    public void closeSocket() { 
    	
		try {
		
			this.outputStream.close();
			//this.inputStream.close();
			this.clientSocket.close();
			
		} 
		
		catch (Exception e) {
		
			System.out.println("XX. " + e.getStackTrace());
			
		}
		
    }
    
    private void p(String s) { System.out.print(s); }
    
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }
	
}

