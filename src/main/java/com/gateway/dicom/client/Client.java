package com.gateway.dicom.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.lib.DicomValueRepresentationInputStream;
import com.gateway.dicom.lib.DicomValueRepresentationOutputStream;

public class Client {

	private String ipAddress;
	private int port;
	private Socket socket = null;
	private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private DicomValueRepresentationInputStream inputStream;
    private DicomValueRepresentationOutputStream outputStream;
    private C_ECHO_RQ echoRequest = null;
    private A_ASSOCIATE_RQ associateRequest = null;
    
    public Client(String ipAddress, int port) {
    	this.ipAddress = ipAddress;
    	this.port = port;
    }
    
    public boolean connectToServer() {
		
		pl("Attempting to connect to Server at: " + this.ipAddress);
		
		try { // open a new socket to the server 
	    	
			this.socket = new Socket(this.ipAddress, this.port);
			pl("Created Socket successfully.");
			
			/*this.dos = new DataOutputStream(this.socket.getOutputStream());
    		pl("Created DataOutputStream successfully.");
    		this.dis = new DataInputStream(this.socket.getInputStream());
    		pl("Created DataInputStream successfully.");
    		pl("Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort() + " successfully ");
    		pl("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    		pl("Connection established.");*/
			
			this.outputStream = new DicomValueRepresentationOutputStream(this.socket.getOutputStream(), DicomValueRepresentationOutputStream.BYTE_ORDERING_LITTLE_ENDIAN);
			pl("Created output stream successfully.");
			
			this.inputStream = new DicomValueRepresentationInputStream(this.socket.getInputStream(), DicomValueRepresentationOutputStream.BYTE_ORDERING_LITTLE_ENDIAN);
			pl("Created input stream successfully.");
			
			pl("Connected to Server:" + this.socket.getInetAddress() 
				+ " on port: " + this.socket.getPort() + " successfully ");
			pl("    -> from local address: " + this.socket.getLocalAddress() 
					+ " and port: " + this.socket.getLocalPort());
			
			pl("Connection established.");
			
    	} 
        
    	catch (Exception e) {
    		
    		pl("Connection to: " + this.ipAddress + " was unsuccessful due to an Exception.");
        	pl("Exception: " + e.toString());
        	return false;
        
    	}
		
    	return true;
		
	}
	
    public boolean sendEchoRequest() {
    	
    	boolean retval = false;
    	
    	try {
    		
            //o = this.dis.read
    		this.echoRequest = new C_ECHO_RQ();
    		DataElement element = this.echoRequest.getCommandGroupLength();
    		this.outputStream.writeExplicitUL(element.getGroupNumber(), element.getElementNumber(), element.getElementLength()); //UL = Unsigned Long
            element = this.echoRequest.getAffectedSOPClassUID();
            this.outputStream.writeExplicitUI(element.getGroupNumber(), element.getElementNumber(), element.getElementData());
    		element = this.echoRequest.getCommandField();
    		short val = new Short(element.getElementData()).shortValue();
    		this.outputStream.writeImplicitUS(element.getGroupNumber(), element.getElementNumber(), val);
            element = this.echoRequest.getMessageID();
            val = new Short(element.getElementData()).shortValue();
            this.outputStream.writeImplicitUS(element.getGroupNumber(), element.getElementNumber(), val);
    		element = this.echoRequest.getCommandDataSetType();
            val = new Short(element.getElementData()).shortValue();
            this.outputStream.writeImplicitUS(element.getGroupNumber(), element.getElementNumber(), val);
    		retval = true;
            
        } 
    	
        catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }
    	
    	return retval;
    	
    }
    
    public boolean sendAssociationRequest() {
    	
    	boolean retval = false;
    	
    	try {
        	
    		this.associateRequest = new A_ASSOCIATE_RQ("1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1");
			this.associateRequest.buildProtocolVersion();
			this.associateRequest.buildCallingAE();
			this.associateRequest.buildCalledAE();
			this.associateRequest.buildApplicationContext();
			this.associateRequest.buildPresentationContext();
			this.associateRequest.buildUserInformation();
			this.associateRequest.buildPduLength();
			this.associateRequest.buildRequest();
			
			//this.outputStream.writeString(string);
			//Q: How to write a byte array to the output stream?
			
			
			retval = true;
        
        } 
    	
        catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }
        
    	
    	return retval;
    	
    }
    
    public boolean receive() {
    	
    	boolean retval = true;
    	
    	try {
    		
            //o = this.dis.read
            
        } 
    	
        catch (Exception e) {   
        	
        	retval = false;
            return retval;
            
        }
    	
        return retval;
    	
    }
    
    public void send(Object o) {

    	try {
            
    		System.out.println("02. -> Sending (" + o +") to the client.");
            //this.outputStream.wri
            //this.outputStream.flush();
            
        }
    	
        catch (Exception e) {
        
        	System.out.println("XX." + e.getStackTrace());
        	
        }
    	
    }
    
	private void pl(String s) { System.out.println(s); }
	
}
