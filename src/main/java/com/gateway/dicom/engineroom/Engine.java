package com.gateway.dicom.engineroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.protocols.C_ECHO_RSP;
import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.A_ASSOCIATE_AC;
import com.gateway.dicom.protocols.A_ASSOCIATE_RJ;
import com.gateway.dicom.client.Client;
import com.gateway.dicom.entities.AbstractSyntax;
import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.AsynchronousOperationsWindowSubItem;
import com.gateway.dicom.entities.ExtendedNegotiationSubItem;
import com.gateway.dicom.entities.ImplementationClassUIDSubItem;
import com.gateway.dicom.entities.ImplementationItem;
import com.gateway.dicom.entities.ImplementationVersionNameSubItem;
import com.gateway.dicom.entities.MaximumLengthSubItem;
import com.gateway.dicom.entities.PresentationContext_AC;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.SCPSCURoleSelectionNegotiationSubItem;
import com.gateway.dicom.entities.TransferSyntax;
import com.gateway.dicom.entities.UserInformation;

public class Engine {

    private C_ECHO_RQ echoRequest = null;
    private C_ECHO_RSP echoResponse = null;
    private A_ASSOCIATE_RQ associateRequestRQ = null;
    private A_ASSOCIATE_AC associateRequestAC = null;
    private A_ASSOCIATE_RJ associateRequestRJ = null;
    private byte[] receivedData = null;
    private boolean requestAcknowledged;
    private boolean requestRejected;
    private Client client;
    private boolean connected;
    private boolean requestBuilt;
    private boolean requestSent;
    private boolean dataReceived;
    
    public Engine() {}
    
    public Engine(Client client) { this.client = client; }
    
    public void run() {
    	
    	this.connected = this.client.connectToServer();
    	
    	if (this.connected) {
    		
    		//Build an A-ASSOCIATE-RQ
    		this.requestBuilt = this.buildAssociateRequest();
    		
    		if (this.requestBuilt) {
    			
    			//Send the A-ASSOCIATE-RQ
    			this.client.setAssociateRequestRQ(this.associateRequestRQ);
    			this.requestSent = this.client.sendAssociateRequest();
    			
    			if (this.requestSent) {
    				
    				this.dataReceived = this.client.receive();
    				
    				if (this.dataReceived) {
    					
    					if (this.requestAcknowledged) {
    						
    						//Build the A-ASSOCIATE-AC
    						this.receivedData = this.client.getReceivedData();
    						this.buildAssociateAcknowledgement();
    						
    					}
    					
    					else if (this.requestRejected) {
    						
    						//Build the A-ASSOCIATE-RJ
    						this.receivedData = this.client.getReceivedData();
    						
    						
    					}
    					
    					else {
    						
    						
    					}
    					
    				}
    				
    				else {
    					
    					
    					
    				}
    				
    			}
    			
    			else {
    				
    				
    				
    			}
    			
    		}
    		
    		else {
    			
    			
    			
    		}
    		
    	}
    	
    	else {
    		
    		
    		
    	}
    	
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
    
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }

}
