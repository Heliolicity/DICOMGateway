package com.gateway.dicom.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.ExtendedNegotiationSubItem;
import com.gateway.dicom.entities.ImplementationClassUIDSubItem;
import com.gateway.dicom.entities.ImplementationItem;
import com.gateway.dicom.entities.ImplementationVersionNameSubItem;
import com.gateway.dicom.entities.MaximumLengthSubItem;
import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.AsynchronousOperationsWindowSubItem;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.SCPSCURoleSelectionNegotiationSubItem;
import com.gateway.dicom.entities.UserInformation;
import com.gateway.dicom.entities.AbstractSyntax;
import com.gateway.dicom.entities.TransferSyntax;
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
    private ByteArrayOutputStream byteOutputStream;
    private ByteArrayInputStream byteInputStream;
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
    
    public boolean sendAssociateRequest() {
    	
    	boolean retval = false;
    	
    	try {
        	
    		boolean built = this.buildAssociateRequest();
    		
    		if (built) {
    	
    			this.writeByte(this.associateRequest.getPduType());
    			this.writeByte(this.associateRequest.getReserved());
    			
    			pl("Successfully sent A-ASSOCIATE-RQ");
    			retval = true;
    			
    		}
    		
    		else {
    			
    			pl("Could not build A-ASSOCIATE-RQ");
    			retval = false;
    			
    		}
    	
        } 
    	
        catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }
        
    	return retval;
    	
    }

    public boolean buildAssociateRequest() {
    	
    	boolean retval = false;
    	
    	try {
    		
    		//Create a byte stream for re-use
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		byte[] arr;
    		byte type;
    		
    		//Application Context
    		type = 0x10;
    		ApplicationContext applicationContext = new ApplicationContext(type, "1.2.840.10008.3.1.1.1");
    		
    		//Presentation Context
    		type = 0x30;
    		AbstractSyntax abstractSyntax = new AbstractSyntax(type, "1.2.840.10008.5.1.4.1.1.2");

    		type = 0x40;
    		TransferSyntax transferSyntax = new TransferSyntax(type, "1.2.840.10008.1.2.4.57");
    		List<TransferSyntax> transferSyntaxes = new ArrayList<TransferSyntax>();
    		transferSyntaxes.add(transferSyntax);
    		
    		type = 0x20;
    		PresentationContext_RQ presentationContext_RQ = new PresentationContext_RQ(type, 1, transferSyntaxes, abstractSyntax);
    		
    		//User Information
    		type = 0x51;
    		MaximumLengthSubItem maximumLengthSubItem = new MaximumLengthSubItem(type, 4000);
    		
    		type = 0x52;
    		ImplementationClassUIDSubItem implementationClassUIDSubItem = new ImplementationClassUIDSubItem(type, ""); 
    		
    		type = 0x55;
    		ImplementationVersionNameSubItem implementationVersionNameSubItem = new ImplementationVersionNameSubItem(type, "");
    		
    		type = 0x53;
    		AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem = new AsynchronousOperationsWindowSubItem(type, 50, 50);
    		
    		type = 0x54;
    		SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem = new SCPSCURoleSelectionNegotiationSubItem(type, "", 0, 0);
    		
    		type = 0x56;
    		ExtendedNegotiationSubItem extendedNegotiationSubItem = new ExtendedNegotiationSubItem(type, "", "");
    		
    		ImplementationItem implementationItem = new ImplementationItem(implementationClassUIDSubItem, implementationVersionNameSubItem);
    		
    		type = 0x50;
    		UserInformation userInformation = new UserInformation(type, maximumLengthSubItem, implementationItem, asynchronousOperationsWindowSubItem, scpSCURoleSelectionNegotiationSubItem, extendedNegotiationSubItem);
    		
    		//A-ASSOCIATE-RQ
    		type = 0x01;
    		this.associateRequest = new A_ASSOCIATE_RQ();
    		this.associateRequest.setPduType(type);
    		this.associateRequest.setCalledAE("1.2.840.10008.3.1.1.1");
    		this.associateRequest.setCallingAE("1.2.840.10008.3.1.1.1");
    		this.associateRequest.setPresentationContext(presentationContext_RQ);
    		this.associateRequest.setApplicationContext(applicationContext);
    		this.associateRequest.setUserInformation(userInformation);
    		
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
    
    public void writeByte(byte val) {
    	
    	try {
            
    		this.outputStream.writeUInt8(val);
    		
        }
    	
        catch (Exception e) {
        
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	
        }
    	
    }
    
    public void writeInt(int val) {
    	
    	try {
            
    		this.outputStream.writeUInt32(val);
    		
        }
    	
        catch (Exception e) {
        
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	
        }
    	
    }
   
    public void writeString(String val) {
    	
    	try {
            
    		this.outputStream.writeString(val);
    		
        }
    	
        catch (Exception e) {
        
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	
        }
    	
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
