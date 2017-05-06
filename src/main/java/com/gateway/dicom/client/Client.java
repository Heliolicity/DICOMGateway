package com.gateway.dicom.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;

import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_RQ;
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
    	/*
    	try {
        	
    		boolean built = this.buildAssociateRequest();
    		
    		if (built) {
    	
    			this.writeByte(this.associateRequest.getPduType());
    			this.writeByte(this.associateRequest.getReserved());
    			this.writeInt(this.associateRequest.getPduLength());
    			this.writeInt(this.associateRequest.getProtocolVersion());
    			this.writeByte(this.associateRequest.getReserved());
    			this.writeByte(this.associateRequest.getReserved());
    			this.writeString(this.associateRequest.getCalledAE());
    			this.writeString(this.associateRequest.getCallingAE());
    			
    			for (int a = 0; a < 32; a ++) this.writeByte(this.associateRequest.getReserved());
    			
    			this.writeByte(this.associateRequest.getApplicationContext().getItemType());
    			this.writeByte(this.associateRequest.getApplicationContext().getReserved());
    			this.writeInt(this.associateRequest.getApplicationContext().getItemLength());
    			this.writeString(this.associateRequest.getApplicationContext().getApplicationContextName());
    			
    			this.writeByte(this.associateRequest.getPresentationContext().getItemType());
    			this.writeByte(this.associateRequest.getPresentationContext().getReserved());
    			this.writeInt(this.associateRequest.getPresentationContext().getItemLength());
    			this.writeInt(this.associateRequest.getPresentationContext().getPresentationContextID());
    			
    				this.writeByte(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getItemType());
    				this.writeByte(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getReserved());
    				this.writeInt(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getItemLength());
    				this.writeString(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getAbstractSyntaxName());
    				
    				this.writeByte(this.associateRequest.getPresentationContext().getTransferSyntaxSubItem().getItemType());
    				this.writeByte(this.associateRequest.getPresentationContext().getTransferSyntaxSubItem().getReserved());
    				this.writeInt(this.associateRequest.getPresentationContext().getTransferSyntaxSubItem().getItemLength());
    				this.writeString(this.associateRequest.getPresentationContext().getTransferSyntaxSubItem().getTransferSyntaxName());
    				
    			this.writeByte(this.associateRequest.getUserInformation().getItemType());
    			this.writeByte(this.associateRequest.getUserInformation().getReserved());
    			this.writeInt(this.associateRequest.getUserInformation().getItemLength());
    			this.writeString(this.associateRequest.getUserInformation().getUserData());
    			
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
        
    	*/
    	return retval;
    	
    }

    public boolean buildAssociateRequest() {
    	
    	boolean retval = false;
    	/*
    	try {
    		
    		//Create a byte stream for re-use
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		byte[] arr;
    		byte type;
    		
    		//Build Application Context
    		ApplicationContext applicationContext = new ApplicationContext("1.2.840.10008.3.1.1.1");
    		arr = applicationContext.getApplicationContextName().getBytes();
    		applicationContext.setItemLength(arr.length);
    		
    		//Build User Information
    		UserInformation userInformation = new UserInformation("1.2.840.10008.3.1.1.1");
    		arr = userInformation.getUserData().getBytes();
    		userInformation.setItemLength(arr.length);
    		
    		//Build Transfer Syntax
    		TransferSyntax transferSyntax = new TransferSyntax("1.2.840.10008.3.1.1.1");
    		arr = transferSyntax.getTransferSyntaxName().getBytes();
    		transferSyntax.setItemLength(arr.length);
    		
    		//Build Abstract Syntax
    		type = 0x30;
    		AbstractSyntax abstractSyntax = new AbstractSyntax(type, "1.2.840.10008.3.1.1.1");
    		arr = abstractSyntax.getAbstractSyntaxName().getBytes();
    		abstractSyntax.setItemLength(arr.length);
    		
    		//Write the Application Context, Abstract Sytax, Transfer Syntax and User Information
    		//to a buffer and get the size of each item.  This value is needed for the Presentation Context
    		byte a = applicationContext.getItemType();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		a = applicationContext.getReserved();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		int b = applicationContext.getItemType();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = applicationContext.getApplicationContextName().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());

    		int applicationContextLength = stream.size();
    		
    		stream.reset();
    		
    		a = userInformation.getItemType();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		a = userInformation.getReserved();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		b = userInformation.getItemLength();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = userInformation.getUserData().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		int userInformationLength = stream.size();
    		
    		stream.reset();
    		
    		a = abstractSyntax.getItemType();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		a = abstractSyntax.getReserved();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		b = abstractSyntax.getItemLength();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = abstractSyntax.getAbstractSyntaxName().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		int abstractSyntaxLength = stream.size();
    		
    		stream.reset();
    		
    		a = transferSyntax.getItemType();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		a = transferSyntax.getReserved();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		b = transferSyntax.getItemLength();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = transferSyntax.getTransferSyntaxName().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		int transferSyntaxLength = stream.size();
    		
    		stream.reset();
    		
    		//Build the Presentation Context
    		PresentationContext_RQ presentationContext = new PresentationContext_RQ(1, transferSyntax, abstractSyntax);
    		
    		b = presentationContext.getPresentationContextID();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		for (int i = 0; i < 3; i ++) {
    		
    			a = presentationContext.getItemType();
    			stream.write(a);
    			pl("BUFFER SIZE: " + stream.size());
    			
    		}
    		
    		presentationContext.setItemLength(stream.size() + abstractSyntaxLength + transferSyntaxLength);
    		
    		int presentationContextLength = presentationContext.getItemLength();
    		
    		stream.reset();
    		
    		this.associateRequest = new A_ASSOCIATE_RQ();
    		this.associateRequest.setCalledAE("1.2.840.10008.3.1.1.1");
    		this.associateRequest.setCallingAE("1.2.840.10008.3.1.1.1");
    		this.associateRequest.setPresentationContext(presentationContext);
    		this.associateRequest.setApplicationContext(applicationContext);
    		this.associateRequest.setUserInformation(userInformation);
    		
    		b = this.associateRequest.getProtocolVersion();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		for (int i = 0; i < 2; i ++) {
    			
    			a = this.associateRequest.getReserved();
    			stream.write(a);
    			pl("BUFFER SIZE: " + stream.size());
    			
    		}
    		
    		arr = this.associateRequest.getCalledAE().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = this.associateRequest.getCallingAE().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
    		
    		for (int i = 0; i < 32; i ++) {
    			
    			a = this.associateRequest.getReserved();
    			stream.write(a);
    			pl("BUFFER SIZE: " + stream.size());
    			
    		}
    		
    		int associateRequestLength = stream.size();
    		associateRequestLength += applicationContextLength + presentationContextLength + userInformationLength;
    		this.associateRequest.setPduLength(associateRequestLength);
    		
    		retval = true;
    		
    	}
    	
    	catch (Exception e) {

        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
    		
    	}
    	*/
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
