package com.gateway.dicom.engineroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.protocols.C_ECHO_RSP;
import com.gateway.dicom.protocols.PDU;
import com.gateway.dicom.protocols.P_DATA_TF;
import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.A_RELEASE_RP;
import com.gateway.dicom.protocols.A_RELEASE_RQ;
import com.gateway.dicom.protocols.A_ABORT;
import com.gateway.dicom.protocols.A_ASSOCIATE_AC;
import com.gateway.dicom.protocols.A_ASSOCIATE_RJ;
import com.gateway.dicom.client.Client;
import com.gateway.dicom.entities.AbstractSyntax;
import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.AsynchronousOperationsWindowSubItem;
import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.ExtendedNegotiationSubItem;
import com.gateway.dicom.entities.ImplementationClassUIDSubItem;
import com.gateway.dicom.entities.ImplementationItem;
import com.gateway.dicom.entities.ImplementationVersionNameSubItem;
import com.gateway.dicom.entities.MaximumLengthSubItem;
import com.gateway.dicom.entities.PresentationContext_AC;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.PresentationDataValue;
import com.gateway.dicom.entities.SCPSCURoleSelectionNegotiationSubItem;
import com.gateway.dicom.entities.TransferSyntax;
import com.gateway.dicom.entities.UserInformation;

public class Engine {

    private C_ECHO_RQ echoRequest = null;
    private C_ECHO_RSP echoResponse = null;
    private A_ASSOCIATE_RQ associateRequestRQ = null;
    private A_ASSOCIATE_AC associateRequestAC = null;
    private A_ASSOCIATE_RJ associateRequestRJ = null;
    private A_RELEASE_RQ releaseRequestRQ = null;
    private A_RELEASE_RP releaseRequestRP = null;
    private A_ABORT abortRQ = null;
    private P_DATA_TF dataTF = null;
    private ImplementationVersionNameSubItem impName;
    private ImplementationClassUIDSubItem impUID;
    private MaximumLengthSubItem maxLen;
    
    private byte[] targetAssociateRQData1;
    private int targetAssociateRQDataLen;
    private byte[] targetAssociateRQData2;
    
    private byte[] targetPDataTFData1;
    private int targetPDataTFDataLen;
    private byte[] targetPDataTFData2;
    
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
    	
    	try {
    	
	    	this.connected = this.client.connectToServer();
	    	byte status;
	    	byte type;
	    	int length;
	    	byte[] data;
	    	int count;
	    	String strData;
	    	ImplementationVersionNameSubItem impName;
	    	ImplementationClassUIDSubItem impUID;
	    	
	    	if (this.connected) {
	    		
	    		//Build an A-ASSOCIATE-RQ
	    		this.requestBuilt = this.buildAssociateRequest();
	    		
	    		if (this.requestBuilt) {
	    			
	    			//Send the A-ASSOCIATE-RQ
	    			this.requestSent = this.sendAssociateRequest();
	    			
	    			if (this.requestSent) {
	    				
	    				this.dataReceived = this.client.receive();
	    				
	    				if (this.dataReceived) {
	    					
	    					this.receivedData = this.client.getReceivedData();
	    					status = this.receivedData[0];
	    		        	
	    		        	if (status == 2) {
	    		        		
	    		        		//Build the A-ASSOCIATE-AC
	    						pl("Request succesfully acknowledged");
	    		        		this.requestAcknowledged = true;
	    		        		this.requestRejected = false;
	    		        		this.buildAssociateAcknowledgement();
	    	        		
	    		        		this.buildDataTF();
	    		        		
	    		        		//Send the A_RELEASE-RQ
	    		        		/*if (this.buildReleaseRequest()) {
	    		        			
	    		        			if (this.sendReleaseRequest()) {
	    		        				
	    		        				pl("Successfully sent A-RELEASE-RQ");
	    		        				this.dataReceived = this.client.receive();
	    		        				
	    		        				if (this.dataReceived) {
	    		        					
	    		        					pl("Received data in response to A-RELEASE-RQ");
	    		        					this.receivedData = this.client.getReceivedData();
	    		        					status = this.receivedData[0];
	    			    		        	pl("Release status: " + status);
	    			    		        	
	    			    		        	this.processData(this.receivedData);
	    			    		        	
	    		        					
	    		        				}
	    		        				
	    		        				else {
	    		        					
	    		        					pl("Did not receive data in response to A-RELEASE-RQ");
	    		        					
	    		        				}
	    		        				
	    		        			}
	    		        			
	    		        			else {
	    		        				
	    		        				pl("Problem sending A-RELEASE-RQ");
	    		        				
	    		        			}
	    		        			
	    		        		}
	    		        		
	    		        		else {
	    		        			
	    		        			pl("Problem building A-RELEASE-RQ");
	    		        			
	    		        		}*/
	    		        		
	    		        	}
	    		        	
	    		        	else if (status == 3) {
	    		        		
	    		        		pl("A-ASSOCIATE-RQ was rejected");
	    		        		this.requestAcknowledged = false;
	    		        		this.requestRejected = true;
	    		        		this.buildAssociateRejection();
	    		        		
	    		        	}
	    		        	
	    		        	else {
	    		        		
	    		        		pl("A-ASSOCIATE-RQ neither acknowledged nor rejected");
	    		        		this.requestAcknowledged = false;
	    		        		this.requestRejected = false;
	    		        		
	    		        	}
	    					
	    				}
	    				
	    				else {
	    					
	    					pl("No data was received from PACS");
	    					
	    				}
	    				
	    			}
	    			
	    			else {
	    				
	    				pl("THE REQUEST WASN'T SUCCESSFULLY SENT");
	    				
	    			}
	    			
	    		}
	    		
	    		else {
	    			
	    			pl("THE REQUEST WASN'T SUCCESSFULLY BUILT");
	    			
	    		}
	    		
	    	}
	    	
	    	else {
	    		
	    		pl("PROBLEM WITH THE CONNECTION IN ENGINE RUN METHOD");
	    		
	    	}
    	
    	} 
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void connect() {
    	
    	try {
    		
    		this.connected = this.client.connectToServer();
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void close() {
    	
    	try {
    		
    		this.connected = (! this.client.closeConnections());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public boolean buildAssociateRequest() {
    	
    	boolean retval = false;
    	
    	byte[] arr1 = {0x01,
    			0x00}; 
    	this.targetAssociateRQData1 = arr1;
    	
    	int len = 204;
    	this.targetAssociateRQDataLen = len;
    	
		byte[] arr2 = {0x00,
				0x01,
				0x00,
				0x00,
				0x43,
				0x4f,
				0x4e,
				0x51,
				0x55,
				0x45,

				0x53,
				0x54,
				0x53,
				0x52,
				0x56,
				0x31,
				0x20,
				0x20,
				0x20,
				0x20,
				0x54,
				0x48,
				0x49,
				0x53,
				0x43,
				0x4f,

				0x4d,
				0x50,
				0x55,
				0x54,
				0x45,
				0x52,
				0x20,
				0x20,
				0x20,
				0x20,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,

				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,

				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x10,
				0x00,
				0x00,
				0x15,
				0x31,
				0x2e,

				0x32,
				0x2e,
				0x38,
				0x34,
				0x30,
				0x2e,
				0x31,
				0x30,
				0x30,
				0x30,
				0x38,
				0x2e,
				0x33,
				0x2e,
				0x31,
				0x2e,

				0x31,
				0x2e,
				0x31,
				0x20,
				0x00,
				0x00,
				0x2e,
				0x01,
				0x00,
				0x00,
				0x00,
				0x30,
				0x00,
				0x00,
				0x11,
				0x31,

				0x2e,
				0x32,
				0x2e,
				0x38,
				0x34,
				0x30,
				0x2e,
				0x31,
				0x30,
				0x30,
				0x30,
				0x38,
				0x2e,
				0x31,
				0x2e,
				0x31,

				0x40,
				0x00,
				0x00,
				0x11,
				0x31,
				0x2e,
				0x32,
				0x2e,
				0x38,
				0x34,
				0x30,
				0x2e,
				0x31,
				0x30,
				0x30,
				0x30,

				0x38,
				0x2e,
				0x31,
				0x2e,
				0x32,
				0x50,
				0x00,
				0x00,
				0x39,
				0x51,
				0x00,
				0x00,
				0x04,
				0x00,
				0x00,
				0x40,

				0x00,
				0x52,
				0x00,
				0x00,
				0x1e,
				0x31,
				0x2e,
				0x32,
				0x2e,
				0x38,
				0x32,
				0x36,
				0x2e,
				0x30,
				0x2e,
				0x31,

				0x2e,
				0x33,
				0x36,
				0x38,
				0x30,
				0x30,
				0x34,
				0x33,
				0x2e,
				0x32,
				0x2e,
				0x31,
				0x33,
				0x39,
				0x36,
				0x2e,

				0x39,
				0x39,
				0x39,
				0x55,
				0x00,
				0x00,
				0x0b,
				0x43,
				0x68,
				0x61,
				0x72,
				0x72,
				0x75,
				0x61,
				0x53,
				0x6f,

				0x66,
				0x74};
		this.targetAssociateRQData2 = arr2;

    	
    	try {
    		
    		//Create a byte stream for re-use
    		//ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		//byte[] arr;
    		byte type;
    		
    		//Application Context
    		type = 0x10;
    		ApplicationContext applicationContext = new ApplicationContext(type, "1.2.840.10008.3.1.1.1");
    																			//1.2.840.10008.3.1.1.1
    		
    		//Presentation Context
    		type = 0x30;
    		AbstractSyntax abstractSyntax = new AbstractSyntax(type, "1.2.840.10008.1.1"); //UID is DICOM ping
    															    //1.2.840.10008.1.1						
    		
    		type = 0x40;
    		TransferSyntax transferSyntax = new TransferSyntax(type, "1.2.840.10008.1.2");
    																//1.2.840.10008.1.2
    		List<TransferSyntax> transferSyntaxes = new ArrayList<TransferSyntax>();
    		transferSyntaxes.add(transferSyntax);
    		//transferSyntax = new TransferSyntax(type, "1.2.840.10008.1.2.2");
    		//transferSyntaxes.add(transferSyntax);
    		
    		type = 0x20;
    		List<PresentationContext_RQ> presentationContexts = new ArrayList<PresentationContext_RQ>();
    		PresentationContext_RQ presentationContext_RQ = new PresentationContext_RQ(type, 1, transferSyntaxes, abstractSyntax);
    		presentationContexts.add(presentationContext_RQ);
    		
    		//User Information
    		type = 0x51;
    		MaximumLengthSubItem maximumLengthSubItem = new MaximumLengthSubItem(type, 16384);
    		
    		type = 0x52;
    		ImplementationClassUIDSubItem implementationClassUIDSubItem = new ImplementationClassUIDSubItem(type, "1.2.826.0.1.3680043.2.1396.999"); 
    		
    		type = 0x55;
    		ImplementationVersionNameSubItem implementationVersionNameSubItem = new ImplementationVersionNameSubItem(type, "CharruaSoft");
    		
    		/*
    		type = 0x53;
    		AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem = new AsynchronousOperationsWindowSubItem(type, 50, 50);
    		
    		type = 0x54;
    		SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem = new SCPSCURoleSelectionNegotiationSubItem(type, "TEST", 0, 0);
    		
    		type = 0x56;
    		ExtendedNegotiationSubItem extendedNegotiationSubItem = new ExtendedNegotiationSubItem(type, "TEST", "TEST");
    		*/
    		
    		ImplementationItem implementationItem = new ImplementationItem(implementationClassUIDSubItem, implementationVersionNameSubItem);
    		
    		type = 0x50;
    		//UserInformation userInformation = new UserInformation(type, maximumLengthSubItem, implementationItem, asynchronousOperationsWindowSubItem, scpSCURoleSelectionNegotiationSubItem, extendedNegotiationSubItem);
    		UserInformation userInformation = new UserInformation();
    		userInformation.setItemType(type);
    		userInformation.setMaximumLengthSubItem(maximumLengthSubItem);
    		userInformation.setImplementationItem(implementationItem);
    		
    		//A-ASSOCIATE-RQ
    		type = 0x01;
    		this.associateRequestRQ = new A_ASSOCIATE_RQ();
    		this.associateRequestRQ.setPduType(type);
    		this.associateRequestRQ.setProtocolVersion(1);
    		//this.associateRequestRQ.setCalledAE("CONQUESTSRV1    ");
    		this.associateRequestRQ.setCalledAE("CONQUESTSRV1");
    		this.associateRequestRQ.setCallingAE("THISCOMPUTER    ");
    		//this.associateRequestRQ.setCallingAE("local");
    		this.associateRequestRQ.setPresentationContexts(presentationContexts);
    		this.associateRequestRQ.setApplicationContext(applicationContext);
    		this.associateRequestRQ.setUserInformation(userInformation);
    		//this.associateRequestRQ.calculateLength();
    		
    		/*for (int a = 0; a < this.targetAssociateRQData1.length; a ++)
    			
    			p("" + this.targetAssociateRQData1[a]);
    		
    		p("" + this.targetAssociateRQDataLen);
    		
    		for (int b = 0; b < this.targetAssociateRQData2.length; b ++)
    			
    			p("" + this.targetAssociateRQData2[b]);
    		
    		pl();*/
    		
    		this.associateRequestRQ.writeToBuffer();
    		//pl("SIZE: " + this.associateRequestRQ.getPduLength());
    		
    		byte[] test = this.associateRequestRQ.getBuffer().toByteArray();
    		byte[] arr3 = {0x01, 0x00};
    		len = this.associateRequestRQ.getPduLength();
    		byte[] arr4 = test;
    		
    		/*for (int c = 0; c < arr3.length; c ++)
    		
    			p("" + arr3[c]);
    		
    		p("" + len);
    			
    		for (int d = 0; d < arr4.length; d ++)
        		
    			p("" + arr4[d]);
    		*/
    		
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
    	
    	/*0 - acceptance
    	
		1 - user-rejection
		
		2 - no-reason (provider rejection)
		
		3 - abstract-syntax-not-supported (provider rejection)
		
		4 - transfer-syntaxes-not-supported (provider rejection)
    	*/
    	
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
    	MaximumLengthSubItem maximumLengthSubItem;
    	ImplementationClassUIDSubItem implementationClassUIDSubItem = null;
    	ImplementationVersionNameSubItem implementationVersionNameSubItem = null;
    	AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem;
    	SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem;
    	ExtendedNegotiationSubItem extendedNegotiationSubItem;
    	ImplementationItem implementationItem;
    	int pos = 0;
    	int length = 0;
    	byte[] arr;
    	int mlsiPos = 0;
    	int icsiPos = 0;
    	int ivsiPos = 0;
    	int aosiPos = 0;
    	int srsiPos = 0;
    	int ensiPos = 0;
    	byte[] a1;
    	byte[] a2;
    	byte[] a3;
    	byte[] a4;
    	byte[] a5;
    	byte[] a6;
    	
    	//if ((this.receivedData != null) && (this.client.isRequestAcknowledged() == true)) {
    	if ((this.receivedData != null) && (this.isRequestAcknowledged() == true)) {
    	
    		this.associateRequestAC = new A_ASSOCIATE_AC();
    		this.associateRequestAC.setPduType(this.receivedData[0]);
    		
    		b1 = this.receivedData[2];
    	    b2 = this.receivedData[3];
    	    b3 = this.receivedData[4];
    	    b4 = this.receivedData[5];
    	    i = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |
    	            ((0xFF & b3) << 8) | (0xFF & b4);
    		/*
    	    //Change this - the PDU Length should indicate the number of bytes to take from the array
    	    this.associateRequestAC.setPduLength(i);
    	    
    	    b1 = this.receivedData[6];
    	    b2 = this.receivedData[7];
    	    
    	    i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    
    	    this.associateRequestAC.setProtocolVersion(i);
    	    
    	    applicationContext = new ApplicationContext();
    	    
    	    if (this.receivedData.length > 74) {
    	    
	    	    b = this.receivedData[74];
	    	    applicationContext.setItemType(b);
	    	    b1 = this.receivedData[76];
	    	    b2 = this.receivedData[77];
	    	    i = ((0xFF & b1) << 8) | (0xFF & b2);
	    	    applicationContext.setItemLength(i);
	    	    length = i;
	    	    
	    	    //Look for the number 33 - this will signify the end of the Application Context Name
	    	    for (int c = 78; c < this.receivedData.length; c ++) 
	    	    
	    	    	if (this.receivedData[c] == 33) 
	    	    		
	    	    		pos = c;
	    	    
	    	    if (pos > 0) {
	    	    	
		    	    arr = Arrays.copyOfRange(this.receivedData, 78, pos);
		    	    s = new String(arr, 0, arr.length);
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
		    	    	
		    		    	    	
		    	    	b = this.receivedData[pos + 6];
		    	    	presentationContext.setResult(b);
		    	    	
		    	    	if (b == 0) {
		    	    		
		    	    		//Process the Transfer Syntax sub items
		    	    		arr = Arrays.copyOfRange(this.receivedData, pos + 8, pos + 4 + presentationContext.getItemLength());
		    	    		
		    	    		//Rewrite this later to handle multiple Transfer Syntaxes
		    	    		transferSyntax = new TransferSyntax();
		    	    		transferSyntax.setItemType(arr[0]);
		    	    		
		    	    		b1 = arr[2];
		        	    	b2 = arr[3];
		        	    	i = ((0xFF & b1) << 8) | (0xFF & b2);
		    	    		transferSyntax.setItemLength(i);
		    	    		
		    	    		arr = Arrays.copyOfRange(arr, 4, 4 + i);
		    	    		s = new String(arr, 0, arr.length);
		    	    	    transferSyntax.setTransferSyntaxName(s);
		    	    	    pl("TRANSFER SYNTAX NAME: " + s);
		    	    	    
		    	    	    transferSyntaxes = new ArrayList<TransferSyntax>();
		    	    	    transferSyntaxes.add(transferSyntax);
		    	    	    presentationContext.setTransferSyntaxSubItem(transferSyntaxes);
		    	    		this.associateRequestAC.setPresentationContext(presentationContext);
		    	    	    
		    	    	    pos = pos + 4 + presentationContext.getItemLength();
		    	    	    arr = Arrays.copyOfRange(this.receivedData, pos, this.receivedData.length);
		    	    	    
		    	    	    if (arr.length > 0) {
		    	    	    
			    	    	    userInformation = new UserInformation();
			    	    	    userInformation.setItemType(this.receivedData[pos]);
			    	    	    
			    	    	    for (int d = 0; d < arr.length; d ++) {
			    	    	    	
			    	    	    	switch(arr[d]) {
			    	    	    	
			    	    	    		case 0x51: mlsiPos = d; break;
			    	    	    		case 0x52: icsiPos = d; break;
			    	    	    		case 0x55: ivsiPos = d; break;
			    	    	    		case 0x53: aosiPos = d; break;
			    	    	    		case 0x54: srsiPos = d; break;
			    	    	    		case 0x56: ensiPos = d; break;
			    	    	    		
			    	    	    	}
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    if (mlsiPos > 0) {
			    	    	    	
			    	    	    	a1 = Arrays.copyOfRange(arr, mlsiPos, arr.length);
			    	    	    	b1 = a1[2];
			    	    	    	b2 = a1[3];
			    	    	    	i = ((0xFF & b1) << 8) | (0xFF & b2);
			    	    	    	a1 = Arrays.copyOfRange(a1, 0, i + 4);
			    	    	    	
			    	    	    	maximumLengthSubItem = new MaximumLengthSubItem();
			            	    	maximumLengthSubItem.setItemType(a1[0]);
			            	    	maximumLengthSubItem.setItemLength(i);
			            	    	
			            	    	b1 = a1[4];
			                	    b2 = a1[5];
			                	    b3 = a1[6];
			                	    b4 = a1[7];
			                	    i = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |
			                	            ((0xFF & b3) << 8) | (0xFF & b4);
			    	    	    	
			                	    maximumLengthSubItem.setMaxPDULengthReceive(i);
			            	    	userInformation.setMaximumLengthSubItem(maximumLengthSubItem);
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    if (icsiPos > 0) {
			    	    	    	
			    	    	    	a2 = Arrays.copyOfRange(arr, icsiPos, arr.length);
			    	    	    	b1 = a2[2];
			            	    	b2 = a2[3];
			            	    	i = ((0xFF & b1) << 8) | (0xFF & b2);
			            	    	a2 = Arrays.copyOfRange(a2, 0, i + 4);
			            	    	
			            	    	implementationClassUIDSubItem = new ImplementationClassUIDSubItem();
			            	    	implementationClassUIDSubItem.setItemType(a2[0]);
			            	    	implementationClassUIDSubItem.setItemLength(i);
			            	    	
			            	    	s = new String(a2, 0, a2.length);
			            	    	
			            	    	implementationClassUIDSubItem.setImplementationClassUID(s);
			            	    	
			    	    	    }
		    	    	    
			    	    	    if (ivsiPos > 0) {
			    	    	    	
			    	    	    	a3 = Arrays.copyOfRange(arr, ivsiPos, arr.length);
			    	    	    	b1 = a3[2];
			    	    	    	b2 = a3[3];
			            	    	i = ((0xFF & b1) << 8) | (0xFF & b2);
			            	    	a3 = Arrays.copyOfRange(a3, 0, i + 4);
			    	    	    	
			            	    	implementationVersionNameSubItem = new ImplementationVersionNameSubItem();
			            	    	implementationVersionNameSubItem.setItemType(a3[0]);
			            	    	implementationVersionNameSubItem.setItemLength(i);
			            	    	
			            	    	s = new String(a3, 0, a3.length);
			            	    	
			            	    	implementationVersionNameSubItem.setImplementationVersionName(s);
			            	    	
			    	    	    }
			    	    	    
			    	    	    if ((implementationClassUIDSubItem != null) && (implementationVersionNameSubItem != null)) {
			    	    	    	
			    	    	    	implementationItem = new ImplementationItem();
			    	    	    	implementationItem.setImplementationClassUIDSubItem(implementationClassUIDSubItem);
			    	    	    	implementationItem.setImplementationVersionNameSubItem(implementationVersionNameSubItem);
			    	    	    	userInformation.setImplementationItem(implementationItem);
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    if (aosiPos > 0) {
			    	    	    	
			    	    	    	
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    if (srsiPos > 0) {
			    	    	    	
			    	    	    	
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    if (ensiPos > 0) {
			    	    	    	
			    	    	    	
			    	    	    	
			    	    	    }
			    	    	    
			    	    	    this.associateRequestAC.setUserInformation(userInformation);
			    	    	    
		    	    	    }
		    	    	    
		    	    	}
		    	    	
		    	    	else {
		    	    		
		    	    		//The Transfer Syntax sub-items aren't significant in the case of non-acceptance
		    	    		pl("RESPONSE: " + presentationContext.response());
		    	    		retval = false;
		    	    		
		    	    	}
		    	    	
		    	    }
	
	    	    }
	    	        	    
	    	    else {
	    	    	
	    	    	//Problem with the Presentation Context
	    	    	pl("First byte of the Presentation Context is not 21H");
	    	    	retval = false;
	    	    	
	    	    }
    	    
    	    }
    	    
    	    else {
    	    	
    	    	//No Application Context received
    	    	pl("No Application Context received");
    	    	
    	    }
    	    */
    	} 
    	
    	else {
    		
    		pl("Either no data was received or the Associate Request was not acknowledged");
    		retval = false;
    		
    	}
    	
    	return retval;
    	
    }

    public boolean buildAssociateRejection() {
    	
    	boolean retval = true;
    	byte b1;
    	byte b2;
    	byte b3;
    	byte b4;
    	int i;
    	
    	//if ((this.receivedData != null) && (this.client.isRequestRejected() == true)) {
    	if ((this.receivedData != null) && (this.isRequestRejected() == true)) {
    		
    		this.associateRequestRJ = new A_ASSOCIATE_RJ();
    		this.associateRequestRJ.setPduType(this.receivedData[0]);
    		
    		b1 = this.receivedData[2];
    		b2 = this.receivedData[3];
    		b3 = this.receivedData[4];
    		b4 = this.receivedData[5];
    		i = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |
    	            ((0xFF & b3) << 8) | (0xFF & b4);
    		
    		this.associateRequestRJ.setPduLength(i);
    		this.associateRequestRJ.setResult(this.receivedData[7]);
    		this.associateRequestRJ.setSource(this.receivedData[8]);
    		this.associateRequestRJ.setReasonDiag(this.receivedData[9]);
    		
    	}
    	
    	else {
    		
    		pl("Either no data was received or the Association Request was not Rejected");
    		retval = false;
    		
    	}
    	
    	return retval;
    	
    }
    
    public boolean sendAssociateRequest() {
    	
    	    			
    	
    	boolean retval = false;
    	
    	try {
    	
    		//this.client.write(arr1);
    		//this.client.writeUInt32(len);
    		//this.client.write(arr2);
    		this.client.writeByte(this.associateRequestRQ.getPduType());
    		this.client.writeByte(this.associateRequestRQ.getReserved());
    		this.client.writeUInt32(this.associateRequestRQ.getPduLength());
    		this.client.write(this.associateRequestRQ.getBuffer().toByteArray());
    		
			this.client.flush();
			
    		pl("Successfully sent A-ASSOCIATE-RQ");
			retval = true;
    	
        } 
    	
        catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }	
    		
    	return retval;
    	
    }
    
    public boolean sendReleaseRequest() { 
    	
    	boolean retval = false;
    	
    	try {
    	
    		this.client.writeByte(this.releaseRequestRQ.getPduType());
    		this.client.writeByte(this.releaseRequestRQ.getReserved());
    		this.client.writeInt(this.releaseRequestRQ.getPduLength());
    		
    		for (int i = 0; i < 4; i ++) this.client.writeByte(this.releaseRequestRQ.getReserved());
    		
    		this.client.flush();
    		
    		retval = true;
    		
    	}
    	
    	catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }
    	
    	return retval;
    	
    }
    
    public boolean sendDataTF() {
    	
    	byte[] arr;
    	boolean retval = false;
    	
    	try {
    		
    		this.client.writeByte(this.dataTF.getPduType());
    		pl("HERE 1");
    		this.client.writeByte(this.dataTF.getReserved());
    		pl("HERE 2");
    		this.client.writeInt(this.dataTF.getPduLength());
    		
    		for (PresentationDataValue item: this.dataTF.getPresentationDataValueItems()) {
    			
    			this.client.writeInt(item.getItemLength());
    			this.client.writeByte(item.getPresentationContextID());
    			
    			//Will need a for loop here for multiple PDV Iteams
    			this.client.writeByte(item.getMessageControlHeader());
    			
    			PDU pdu = item.getPdvData();
    			//Need some way of telling what kind of PDU it is
    			//For now assume C-ECHO-RQ
    			
    			C_ECHO_RQ echo = (C_ECHO_RQ) pdu;
    			
    			DataElement element = echo.getCommandGroupLength();
    			this.client.writeUInt16(element.getGroupNumber());
    			this.client.writeUInt16(element.getElementNumber());
    			this.client.write(element.getValueRepresentation().getBytes());
    			String data = element.getElementData();
    			int length = Integer.valueOf(data);
    			this.client.writeUInt32(length);
    			
    			element = echo.getAffectedServiceClassUID();
    			this.client.writeUInt16(element.getGroupNumber());
    			this.client.writeUInt16(element.getElementNumber());
    			this.client.write(element.getValueRepresentation().getBytes());
    			data = element.getElementData();
    			this.client.write(data.getBytes());
    			
    			element = echo.getCommandField();
    			this.client.writeUInt16(element.getGroupNumber());
    			this.client.writeUInt16(element.getElementNumber());
    			this.client.write(element.getValueRepresentation().getBytes());
    			data = element.getElementData();
    			this.client.write(data.getBytes());
    			
    			element = echo.getMessageID();
    			this.client.writeUInt16(element.getGroupNumber());
    			this.client.writeUInt16(element.getElementNumber());
    			this.client.write(element.getValueRepresentation().getBytes());
    			data = element.getElementData();
    			this.client.write(data.getBytes());
    			
    			element = echo.getDataSetType();
    			this.client.writeUInt16(element.getGroupNumber());
    			this.client.writeUInt16(element.getElementNumber());
    			this.client.write(element.getValueRepresentation().getBytes());
    			data = element.getElementData();
    			this.client.write(data.getBytes());
    			
    			
    		}
    		
    		this.client.flush();
    		
    		retval = true;
    		
    	}
    	
    	catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }	
    	
    	return retval;
    	
    }
    
    public boolean sendAbortRequest() {
    	
    	boolean retval = false;
    	
    	try {
    	
    		this.client.writeByte(this.abortRQ.getPduType());
    		this.client.writeByte(this.abortRQ.getReserved());
    		this.client.writeInt(this.abortRQ.getPduLength());
    		this.client.writeByte(this.abortRQ.getReserved());
    		this.client.writeByte(this.abortRQ.getReserved());
    		this.client.writeByte(this.abortRQ.getSource());
    		this.client.writeByte(this.abortRQ.getReason());
    		
    		this.client.flush();
    		
    		retval = true;
    		
    	}
    	
    	catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
        	retval = false;
            
        }	
    	
    	return retval;
    	
    }
    
    public boolean buildEchoRequest() {
    	
    	boolean retval = false;
    	short randomNum = (short) (ThreadLocalRandom.current().nextInt(0, 65535) - 32768);
    	this.echoRequest = new C_ECHO_RQ(randomNum);
    	retval = true;
    	return retval;
    	
    }
    
    public boolean buildReleaseRequest() {
    	
    	boolean retval = false;
    	byte type = 0x05;
    	this.releaseRequestRQ = new A_RELEASE_RQ();
    	this.releaseRequestRQ.setPduType(type);
    	retval = true;
    	return retval;
    	
    }
    
    public boolean buildDataTF() {
    	
    	byte[] arr1 = {0x04, 0x00};
    	
    	int len = 74;
    	
    	byte[] arr2 = {0x00,
    			0x00,
    			0x00,
    			0x46,
    			0x01,
    			0x03,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x04,
    			0x00,
    			0x00,
    			0x00,

    			0x38,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x02,
    			0x00,
    			0x12,
    			0x00,
    			0x00,
    			0x00,
    			0x31,
    			0x2e,
    			0x32,
    			0x2e,

    			0x38,
    			0x34,
    			0x30,
    			0x2e,
    			0x31,
    			0x30,
    			0x30,
    			0x30,
    			0x38,
    			0x2e,
    			0x21,
    			0x2e,
    			0x31,
    			0x00,
    			0x00,
    			0x00,

    			0x00,
    			0x01,
    			0x02,
    			0x00,
    			0x00,
    			0x00,
    			0x30,
    			0x00,
    			0x00,
    			0x00,
    			0x10,
    			0x01,
    			0x02,
    			0x00,
    			0x00,
    			0x00,

    			0x03,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x08,
    			0x02,
    			0x00,
    			0x00,
    			0x00,
    			0x01,
    			0x01};
    	
    	boolean retval = false;
    	
    	try {
    	
    		this.targetPDataTFData1 = arr1;
    		this.targetPDataTFDataLen = len;
    		this.targetPDataTFData2 = arr2;
    		
    		byte type;
    		
    		//FOR NOW JUST USE C-ECHO REQUEST 
    		//CHANGE THIS LATER SO DIFFERENT PDVS CAN BE SENT
    		
    		type = 0x04;
    		
    		//PresentationContext_AC presentationContext = this.associateRequestAC.getPresentationContext();
    		PresentationContext_RQ presentationContext = this.associateRequestRQ.getPresentationContexts().get(0);
    		int pcID = presentationContext.getPresentationContextID();
    		int header = 0x03;
    		
    		this.dataTF = new P_DATA_TF();
    		this.dataTF.setPduType(type);
    		
    		if (this.buildEchoRequest()) {
    		
    			PresentationDataValue pdValue = new PresentationDataValue(header, pcID, this.echoRequest, "C-ECHO");
    			ArrayList<PresentationDataValue> pdValueItems = new ArrayList<PresentationDataValue>();
    			pdValueItems.add(pdValue);
    			this.dataTF.setPresentationDataValueItems(pdValueItems);
    			retval = true;
    			
    			for (int a = 0; a < this.targetPDataTFData1.length; a ++)
    				
    				p("" + this.targetPDataTFData1[a]);
    			
    			p("" + this.targetPDataTFDataLen);
    			
    			for (int b = 0; b < this.targetPDataTFData2.length; b ++)
    			
    				p("" + this.targetPDataTFData2[b]);
    				
    			pl();
    			
    			this.dataTF.writeToBuffer();
    			
    			/*byte[] arr3 = {0x04, 0x00};
    			len = this.dataTF.getPduLength();
    			pl("LEN: " + len);
    			byte[] arr4 = this.dataTF.getBuffer().toByteArray();
    			
    			for (int c = 0; c < arr3.length; c ++)
    				
    				p("" + arr3[c]);
    			
    			p("" + len);
    			
    			for (int d = 0; d < arr4.length; d ++) 
    				
    				p("" + arr4[d]);
    			*/
    			
    			byte[] arr5 = this.dataTF.getBuffer().toByteArray();
    			
    			for (int e = 0; e < arr5.length; e ++)
    				
    				p("" + arr5[e]);
    			
    		}
    		
    		else {
    			
    			//Problem building Echo Request
    			pl("THERE WAS A PROBLEM BUILDING THE ECHO REQUEST");
    			retval = false;
    			
    		}
    		
    	}
    	
    	catch (Exception e) {

        	pl(e.getMessage());
        	e.printStackTrace();
        	retval = false;
    		
    	}
    	
    	return retval;
    	
    }
    
    public boolean buildAbortRequest() {
    	
    	boolean retval = false;
    	byte type;
    	byte source;
    	byte reason;
    	
    	type = 0x07;
    	source = 0;
    	reason = 0;
    	this.abortRQ = new A_ABORT(type, source, reason);
    	
    	retval = true;
    	return retval;
    	
    }
    
    public void processData(byte[] arr) {
    	
    	this.impName = null;
    	this.impUID = null;
    	this.maxLen = null;
    	this.releaseRequestRP = null;
    	
    	//Loop through array searching for DICOM objects
    	//Assume first entry in the array is the PDU type and positions three and four are the PDU length
    	byte[] data;
    	byte[] subData;
    	int arrLength;
    	int segLength;
    	byte type;
    	String strData;
    	int intData;
    	
    	arrLength = arr.length;
    	
    	if (arrLength > 0) {
    		
    		data = arr;
    		pl("LENGTH: " + data.length);
    		
    		while (data.length > 0) {
    			pl("HERE");
    			type = data[0];
    			segLength = data[2] + data[3];
    			subData = Arrays.copyOfRange(data, 4, 4 + segLength);

    			switch(type) {
    			
    				case 85: strData = new String(subData);
	    				this.impName = new ImplementationVersionNameSubItem();
	    				this.impName.setItemType(type);
	    				this.impName.setItemLength(segLength);
	    				this.impName.setImplementationVersionName(strData);
	    				pl("GOT AN IMPLEMENTATION VERSION NAME SUB ITEM");
    					break;
    					
    				case 82: strData = new String(subData);
	    				this.impUID = new ImplementationClassUIDSubItem();
	    				this.impUID.setItemType(type);
	    				this.impUID.setItemLength(segLength);
	    				this.impUID.setImplementationClassUID(strData);
	    				pl("GOT AN IMPLEMENTATION CLASS UID SUB ITEM");
	    				break;
    		
    				case 81: intData = data[4] + data[5] + data[6] + data[7];
    					this.maxLen = new MaximumLengthSubItem();
    					this.maxLen.setMaxPDULengthReceive(intData);
    					pl("GOT A MAXIMUM LENGTH SUB ITEM");
    					break;
	    				
    				case 6: this.releaseRequestRP = new A_RELEASE_RP();
    					this.releaseRequestRP.setPduType(type);
    					pl("GOT A RELEASE RESPONSE");
    					break;
	    				
    				default: break;
    			
    			}
    			
    			data = Arrays.copyOfRange(data, 4 + segLength, data.length);
    			
    		}
    		
    	}
    	
    	else {
    		
    	}
    	
    	
    }
    
    public void receive() {
    	
    	if (this.client.receive()) 
    		
    		this.receivedData = this.client.getReceivedData();
    	
    }
        
	public C_ECHO_RQ getEchoRequest() {
		return echoRequest;
	}

	public void setEchoRequest(C_ECHO_RQ echoRequest) {
		this.echoRequest = echoRequest;
	}

	public C_ECHO_RSP getEchoResponse() {
		return echoResponse;
	}

	public void setEchoResponse(C_ECHO_RSP echoResponse) {
		this.echoResponse = echoResponse;
	}

	public A_ASSOCIATE_RQ getAssociateRequestRQ() {
		return associateRequestRQ;
	}

	public void setAssociateRequestRQ(A_ASSOCIATE_RQ associateRequestRQ) {
		this.associateRequestRQ = associateRequestRQ;
	}

	public A_ASSOCIATE_AC getAssociateRequestAC() {
		return associateRequestAC;
	}

	public void setAssociateRequestAC(A_ASSOCIATE_AC associateRequestAC) {
		this.associateRequestAC = associateRequestAC;
	}

	public A_ASSOCIATE_RJ getAssociateRequestRJ() {
		return associateRequestRJ;
	}

	public void setAssociateRequestRJ(A_ASSOCIATE_RJ associateRequestRJ) {
		this.associateRequestRJ = associateRequestRJ;
	}

	public byte[] getReceivedData() {
		return receivedData;
	}

	public void setReceivedData(byte[] receivedData) {
		this.receivedData = receivedData;
	}

	public boolean isRequestAcknowledged() {
		return requestAcknowledged;
	}

	public void setRequestAcknowledged(boolean requestAcknowledged) {
		this.requestAcknowledged = requestAcknowledged;
	}

	public boolean isRequestRejected() {
		return requestRejected;
	}

	public void setRequestRejected(boolean requestRejected) {
		this.requestRejected = requestRejected;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isRequestBuilt() {
		return requestBuilt;
	}

	public void setRequestBuilt(boolean requestBuilt) {
		this.requestBuilt = requestBuilt;
	}

	public boolean isRequestSent() {
		return requestSent;
	}

	public void setRequestSent(boolean requestSent) {
		this.requestSent = requestSent;
	}

	public boolean isDataReceived() {
		return dataReceived;
	}

	public void setDataReceived(boolean dataReceived) {
		this.dataReceived = dataReceived;
	}

	private void p(String s) { System.out.print(s); }
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }

}
