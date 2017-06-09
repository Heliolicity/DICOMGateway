package com.gateway.dicom.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.gateway.dicom.protocols.A_ASSOCIATE_AC;
import com.gateway.dicom.protocols.A_ASSOCIATE_RJ;
import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.ExtendedNegotiationSubItem;
import com.gateway.dicom.entities.ImplementationClassUIDSubItem;
import com.gateway.dicom.entities.ImplementationItem;
import com.gateway.dicom.entities.ImplementationVersionNameSubItem;
import com.gateway.dicom.entities.MaximumLengthSubItem;
import com.gateway.dicom.entities.PresentationContext_AC;
import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.AsynchronousOperationsWindowSubItem;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.SCPSCURoleSelectionNegotiationSubItem;
import com.gateway.dicom.entities.UserInformation;
import com.gateway.dicom.entities.AbstractSyntax;
import com.gateway.dicom.entities.TransferSyntax;
import com.gateway.dicom.lib.DicomValueRepresentationInputStream;
import com.gateway.dicom.lib.DicomValueRepresentationOutputStream;
import com.gateway.dicom.lib.DicomByteOrderable;

public class ClientBU extends DicomByteOrderable {

	private String ipAddress;
	private int port;
	private Socket socket = null;
	private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private DicomValueRepresentationInputStream inputStream;
    private DicomValueRepresentationOutputStream outputStream;
    private ByteArrayOutputStream byteOutputStream;
    private ByteArrayInputStream byteInputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private C_ECHO_RQ echoRequest = null;
    private A_ASSOCIATE_RQ associateRequestRQ = null;
    private A_ASSOCIATE_AC associateRequestAC = null;
    private A_ASSOCIATE_RJ associateRequestRJ = null;
    private byte[] receivedData = null;
    private boolean requestAcknowledged;
    private boolean requestRejected;
    
    public ClientBU(String ipAddress, int port) {
    	this.ipAddress = ipAddress;
    	this.port = port;
    	this.byteOrdering = BYTE_ORDERING_BIG_ENDIAN;
    }
    
    public boolean connectToServer() {
		
		pl("Attempting to connect to Server at: " + this.ipAddress);
		
		try { // open a new socket to the server 
	    	
			this.socket = new Socket(this.ipAddress, this.port);
			pl("Created Socket successfully.");
			
			this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    		pl("Created DataOutputStream successfully.");
    		this.dataInputStream = new DataInputStream(this.socket.getInputStream());
    		pl("Created DataInputStream successfully.");
    		pl("Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort() + " successfully ");
    		pl("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    		pl("Connection established.");
    		
    	} 
        
    	catch (Exception e) {
    		
    		pl("Connection to: " + this.ipAddress + " was unsuccessful due to an Exception.");
        	pl("Exception: " + e.toString());
        	e.printStackTrace();
        	return false;
        
    	}
		
    	return true;
		
	}
    
    public boolean closeConnections() {

		try { 

			if (this.dataInputStream != null) {
			
	    		this.dataInputStream.close();
				pl("Closed Client Data Input Stream");
			
			}
			
			if (this.dataOutputStream != null) {
			
				this.dataOutputStream.close();
				pl("Closed Client Data Output Stream");
			
			}
			
			if (this.socket != null) {
			
				this.socket.close();
				pl("Closed Client Socket");
				
			}
			
		}
		
		catch (Exception e) {
    		
    		pl("Exception: " + e.toString());
        	e.printStackTrace();
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
    	
		byte[] arr;
    	boolean retval = false;
    	
    	try {
        	
    		boolean built = this.buildAssociateRequest();
    		
    		if (built) {
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPduType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getPduLength());
    			
    			//this.dataOutputStream.writeByte(this.associateRequest.getProtocolVersion());
    			//For time being hard-code Protocol Version to two bytes of 0x00
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
    			//End of Protocol Version encoding - BUT POSSIBLY CHANGE THIS LATER
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
    			
    			this.dataOutputStream.write(this.associateRequestRQ.getCalledAE().getBytes());
    			this.dataOutputStream.write(this.associateRequestRQ.getCallingAE().getBytes());
    			
    			for (int a = 0; a < 32; a ++) this.dataOutputStream.writeByte(this.associateRequestRQ.getReserved());
				
    			//Application Context
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getApplicationContext().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getApplicationContext().getReserved());
    			//this.dataOutputStream.writeInt(this.associateRequest.getApplicationContext().getItemLength());
    			this.writeUInt16(this.associateRequestRQ.getApplicationContext().getItemLength());
    			this.dataOutputStream.write(this.associateRequestRQ.getApplicationContext().getApplicationContextName().getBytes());
    			
    			
    			//Presentation Context
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
    			//this.dataOutputStream.writeInt(this.associateRequest.getPresentationContext().getItemLength());
    			this.writeUInt16(this.associateRequestRQ.getPresentationContext().getItemLength());
    			//this.dataOutputStream.writeInt(this.associateRequest.getPresentationContext().getPresentationContextID());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getPresentationContextID());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
    			
    			
    				//Abstract Syntax
    				this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getItemType());
    				this.dataOutputStream.writeByte(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getReserved());
    				//this.dataOutputStream.writeInt(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getItemLength());
    				this.writeUInt16(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getItemLength());
    				this.dataOutputStream.write(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getAbstractSyntaxName().getBytes());
    				
    				//Transfer Syntaxes
    				int tsi = this.associateRequestRQ.getPresentationContext().getTransferSyntaxSubItems().size();
        			
        			for (int b = 0; b < tsi; b ++) {
        				
        				TransferSyntax transferSyntax = this.associateRequestRQ.getPresentationContext().getTransferSyntaxSubItems().get(b);
        				this.dataOutputStream.writeByte(transferSyntax.getItemType());
        				this.dataOutputStream.writeByte(transferSyntax.getReserved());
        				//this.dataOutputStream.writeInt(transferSyntax.getItemLength());
        				this.writeUInt16(transferSyntax.getItemLength());
        				this.dataOutputStream.write(transferSyntax.getTransferSyntaxName().getBytes());
        				
        			}
    				
    			//User Information
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getItemLength());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getItemLength());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getMaxPDULengthReceive());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getItemLength());
    			this.dataOutputStream.write(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getImplementationClassUID().getBytes());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getItemLength());
    			this.dataOutputStream.write(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getImplementationVersionName().getBytes());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getItemLength());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getMaximumNumberOperationsInvoked());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getMaximumNumberOperationsPerformed());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getItemLength());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getUidLength());
    			this.dataOutputStream.write((this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getSopClassUID()).getBytes());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getScuRole());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getScpRole());
    			
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getItemType());
    			this.dataOutputStream.writeByte(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getReserved());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getItemLength());
    			this.dataOutputStream.writeInt(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getSopClassUIDLength());
    			this.dataOutputStream.write(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getSopClassUID().getBytes());
    			this.dataOutputStream.write(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getServiceClassApplicationInformation().getBytes());
        		
    			this.dataOutputStream.flush();
    			
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
    		//ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		//byte[] arr;
    		byte type;
    		
    		//Application Context
    		type = 0x10;
    		ApplicationContext applicationContext = new ApplicationContext(type, "1.2.840.10008.3.1.1.1");
    		
    		//Presentation Context
    		type = 0x30;
    		AbstractSyntax abstractSyntax = new AbstractSyntax(type, "1.2.840.10008.1.1"); //UID is DICOM ping

    		type = 0x40;
    		TransferSyntax transferSyntax = new TransferSyntax(type, "1.2.840.10008.1.2");
    		List<TransferSyntax> transferSyntaxes = new ArrayList<TransferSyntax>();
    		transferSyntaxes.add(transferSyntax);
    		
    		type = 0x20;
    		PresentationContext_RQ presentationContext_RQ = new PresentationContext_RQ(type, 1, transferSyntaxes, abstractSyntax);
    		
    		//User Information
    		type = 0x51;
    		MaximumLengthSubItem maximumLengthSubItem = new MaximumLengthSubItem(type, 4000);
    		
    		type = 0x52;
    		ImplementationClassUIDSubItem implementationClassUIDSubItem = new ImplementationClassUIDSubItem(type, "TEST"); 
    		
    		type = 0x55;
    		ImplementationVersionNameSubItem implementationVersionNameSubItem = new ImplementationVersionNameSubItem(type, "TEST");
    		
    		type = 0x53;
    		AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem = new AsynchronousOperationsWindowSubItem(type, 50, 50);
    		
    		type = 0x54;
    		SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem = new SCPSCURoleSelectionNegotiationSubItem(type, "TEST", 0, 0);
    		
    		type = 0x56;
    		ExtendedNegotiationSubItem extendedNegotiationSubItem = new ExtendedNegotiationSubItem(type, "TEST", "TEST");
    		
    		ImplementationItem implementationItem = new ImplementationItem(implementationClassUIDSubItem, implementationVersionNameSubItem);
    		
    		type = 0x50;
    		UserInformation userInformation = new UserInformation(type, maximumLengthSubItem, implementationItem, asynchronousOperationsWindowSubItem, scpSCURoleSelectionNegotiationSubItem, extendedNegotiationSubItem);
    		
    		//A-ASSOCIATE-RQ
    		type = 0x01;
    		this.associateRequestRQ = new A_ASSOCIATE_RQ();
    		this.associateRequestRQ.setPduType(type);
    		this.associateRequestRQ.setCalledAE("CONQUESTSRV1    ");
    		//this.associateRequest.setCallingAE("1.2.840.10008.3.1.1.1");
    		this.associateRequestRQ.setCallingAE("THISCOMPUTER    ");
    		this.associateRequestRQ.setPresentationContext(presentationContext_RQ);
    		this.associateRequestRQ.setApplicationContext(applicationContext);
    		this.associateRequestRQ.setUserInformation(userInformation);
    		this.associateRequestRQ.calculateLength();
    		
    		retval = true;
    		
    	}
    	
    	catch (Exception e) {

        	pl(e.getMessage());
        	e.printStackTrace();
        	retval = false;
    		
    	}
    	
    	return retval;
    	
    }
    
    public boolean buildAssociateAcknowledgement() {
    	
    	boolean retval = true;
    	byte b;
    	byte b1;
    	byte b2;
    	byte b3;
    	byte b4;
    	int i;
    	String s;
    	ApplicationContext applicationContext;
    	PresentationContext_AC presentationContext;
    	AbstractSyntax abstractSyntax;
    	List<TransferSyntax> transferSyntaxes;
    	TransferSyntax transferSyntax;
    	UserInformation userInformation;
    	int pos = 0;
    	int length = 0;
    	byte[] arr;
    	
    	if ((this.receivedData != null) && (this.requestAcknowledged == true)) {
    	
    		this.associateRequestAC = new A_ASSOCIATE_AC();
    		this.associateRequestAC.setPduType(this.receivedData[0]);
    		
    		b1 = this.receivedData[2];
    	    b2 = this.receivedData[3];
    	    b3 = this.receivedData[4];
    	    b4 = this.receivedData[5];
    	    i = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |
    	            ((0xFF & b3) << 8) | (0xFF & b4);
    		
    	    //Change this - the PDU Length should indicate the number of bytes to take from the array
    	    this.associateRequestAC.setPduLength(i);
    	    
    	    b1 = this.receivedData[6];
    	    b2 = this.receivedData[7];
    	    
    	    i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    
    	    this.associateRequestAC.setProtocolVersion(i);
    	    
    	    applicationContext = new ApplicationContext();
    	    b = this.receivedData[74];
    	    applicationContext.setItemType(b);
    	    b1 = this.receivedData[76];
    	    b2 = this.receivedData[77];
    	    i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    applicationContext.setItemLength(i);
    	    length = i;
    	    
    	    //Look for the number 33 - this will signify the end of the Application Context Name
    	    for (int c = 78; c < this.receivedData.length; c ++) 
    	    
    	    	if (this.receivedData[c] == 33) pos = c;
    	    
    	    arr = Arrays.copyOfRange(this.receivedData, 78, pos);
    	    
    	    s = new String(arr, 0, arr.length);
    	    pl("APPLICATION CONTEXT NAME: " + s);
    	    applicationContext.setApplicationContextName(s);
    	    this.associateRequestAC.setApplicationContext(applicationContext);
    	    
    	    b = this.receivedData[pos];
    	    
    	    if (b == 33) {
    	    	
    	    	presentationContext = new PresentationContext_AC();
    	    	presentationContext.setItemType(b);
    	    	
    	    	b1 = this.receivedData[pos + 2];
    	    	b2 = this.receivedData[pos + 3];
    	    	i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    	presentationContext.setItemLength(i);
    	    	
    	    	b = this.receivedData[pos + 4];
    	    	i = (int) b;
    	    	presentationContext.setPresentationContextID(i);
    	    	
    	    	/*0 - acceptance

				1 - user-rejection
				
				2 - no-reason (provider rejection)
				
				3 - abstract-syntax-not-supported (provider rejection)
				
				4 - transfer-syntaxes-not-supported (provider rejection)
    	    	*/
    	    	
    	    	b = this.receivedData[pos + 6];
    	    	presentationContext.setResult(b);
    	    	
    	    	if (b == 0) {
    	    		
    	    		//Process the Transfer Syntax sub items
    	    		
    	    		
    	    	}
    	    	
    	    	else {
    	    	
    	    		//The Transfer Syntax sub-items aren't significant in the case of non-acceptance
    	    		pl(this.response(b));
    	    		retval = false;
    	    		
    	    	}
    	    	
    	    }
    	    
    	    else {
    	    	
    	    	//Problem with the Presentation Context
    	    	pl("First byte of the Presentation Context is not 21H");
    	    	retval = false;
    	    	
    	    }
    	    
    	} 
    	
    	else {
    		
    		pl("Either no data was received or the Associate Request was not acknowledged");
    		retval = false;
    		
    	}
    	
    	return retval;
    	
    }
    
    public boolean receive() {
    	
    	boolean retval = false;
    	ByteArrayOutputStream stream;
    	byte[] arr;
    	int result = 0;
    	byte rec;
    	int size = 0;
    	byte status = 0;
    	
    	try {
    		
    		size = this.dataInputStream.available();
    		pl("AVAILABLE: " + this.dataInputStream.available());
    		
    		if (size > 0) {
    		
    			retval = true;
	        	arr = new byte[size];
	        	this.dataInputStream.readFully(arr);
	        	
	        	for (int a = 0; a < arr.length; a ++) pl("arr[" + a + "]: " + arr[a]);
	        	
	        	this.receivedData = arr;
	        	status = arr[0];
	        	
	        	if (status == 2) {
	        		
	        		pl("REQUEST ACKNOWLEDGED");
	        		this.requestAcknowledged = true;
	        		this.requestRejected = false;
	        		
	        	}
	        	
	        	else if (status == 3) {
	        		
	        		pl("REQUEST REJECTED");
	        		this.requestAcknowledged = false;
	        		this.requestRejected = true;
	        		
	        	}
	        	
	        	else {
	        		
	        		pl("REQUEST NEITHER ACKNOWLEDGED NOR REJECTED");
	        		this.requestAcknowledged = false;
	        		this.requestRejected = false;
	        		
	        	}
    		
	        	if (this.requestAcknowledged == true) 
	        		
	        		this.buildAssociateAcknowledgement();
	        	
    		}
            
    	} 
    	
        catch (Exception e) {   
        	
        	retval = false;
        	pl(e.getMessage());
        	e.printStackTrace();
            return retval;
            
        }
    
    	return retval;
    	
    }
    
    public void writeUInt16(int value) throws IOException {
    	
		int b0 = value & 0x00ff;
		int b1 = (value & 0xff00) >> 8;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			this.dataOutputStream.write(b0);
			this.dataOutputStream.write(b1);
			
		}
		
		else {
		
			this.dataOutputStream.write(b1);
			this.dataOutputStream.write(b0);
			
		}
		
	}
    
    public String readString(int length) throws IOException {
		
    	byte[] byteArray = readByteArray(length);
		
    	if (byteArray[byteArray.length-1] == 0x00) {
		
    		return new String(byteArray, 0, byteArray.length - 1);
		
    	}
		
    	else {
	
    		return new String(byteArray);
		
    	}	
	
    }
	
	public byte[] readByteArray(int length) throws IOException {
	
		byte[] buffer = new byte[length];
		
		int totalBytesRead = 0;
		
		while(totalBytesRead != length) {
		
			totalBytesRead += this.dataInputStream.read(buffer, 
					totalBytesRead, 
					length - totalBytesRead);
		
		}	
		
		return buffer;
		
	}
    
	private String response(int i) {
		
		String response = "";
		
		switch(i) {
		
			case 0: response = "Association Request was accepted - acceptance"; break;
			case 1: response = "Association Request was rejected - user rejection"; break;
			case 2: response = "Association Request was rejected - no reason (provider rejection)"; break;
			case 3: response = "Association Request was rejected - abstract syntax not supporter (provider rejection)"; break;
			case 4: response = "Association Request was rejected - transfer syntaxes not supporter (provider rejection)"; break;

		}
		
		return response;
		
	}
	
    private void pl(String s) { System.out.println(s); }
    
}
