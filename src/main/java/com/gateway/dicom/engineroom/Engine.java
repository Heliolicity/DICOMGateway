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
import com.gateway.dicom.entities.MessageControlHeader;
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
    private P_DATA_TF dataTF = null;
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
	    	
	    	if (this.connected) {
	    		
	    		//Build an A-ASSOCIATE-RQ
	    		this.requestBuilt = this.buildAssociateRequest();
	    		
	    		if (this.requestBuilt) {
	    			
	    			//Send the A-ASSOCIATE-RQ
	    			//this.client.setAssociateRequestRQ(this.associateRequestRQ);
	    			//this.requestSent = this.client.sendAssociateRequest();
	    			this.requestSent = this.sendAssociateRequest();
	    			
	    			if (this.requestSent) {
	    				
	    				this.dataReceived = this.client.receive();
	    				
	    				if (this.dataReceived) {
	    					
	    					this.receivedData = this.client.getReceivedData();
	    					status = this.receivedData[0];
	    		        	
	    		        	if (status == 2) {
	    		        		
	    		        		//Build the A-ASSOCIATE-AC
	    						pl("REQUEST ACKNOWLEDGED");
	    		        		this.requestAcknowledged = true;
	    		        		this.requestRejected = false;
	    		        		this.buildAssociateAcknowledgement();
	    		        		
	    		        		Thread.sleep(30000);
	    		        		
	    		        		//Build the P-DATA-TF
	    		        		if (this.buildDataTF()) {
	    		        			
	    		        			if (this.sendDataTF()) {
	    		        				
	    		        				pl("SUCCESSFULLY SENT DATA REQUEST");
	    		        				this.dataReceived = this.client.receive();
	    		        				
	    		        			}
	    		        			
	    		        			else {
	    		        				
	    		        				pl("PROBLEM SENDDING DATA REQUEST");
	    		        				
	    		        			}
	    		        			
	    		        		}
	    		        		
	    		        		else {
	    		        			
	    		        			pl("PROBLEM BUILDING DATA REQUEST");
	    		        		
	    		        		}
	    		        		
	    		        	}
	    		        	
	    		        	else if (status == 3) {
	    		        		
	    		        		pl("REQUEST REJECTED");
	    		        		this.requestAcknowledged = false;
	    		        		this.requestRejected = true;
	    		        		this.buildAssociateRejection();
	    		        		
	    		        	}
	    		        	
	    		        	else {
	    		        		
	    		        		pl("REQUEST NEITHER ACKNOWLEDGED NOR REJECTED");
	    		        		this.requestAcknowledged = false;
	    		        		this.requestRejected = false;
	    		        		
	    		        	}
	    					
	    				}
	    				
	    				else {
	    					
	    					pl("NO DATA WAS RECEIVED");
	    					
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
    	    		pl(presentationContext.response());
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
    	
    	byte[] arr;
    	boolean retval = false;
    	
    	try {
    	
    		this.client.writeByte(this.associateRequestRQ.getPduType());
    		this.client.writeByte(this.associateRequestRQ.getReserved());
			this.client.writeInt(this.associateRequestRQ.getPduLength());
			
			//this.client.writeByte(this.associateRequest.getProtocolVersion());
			//For time being hard-code Protocol Version to two bytes of 0x00
			this.client.writeByte(this.associateRequestRQ.getReserved());
			this.client.writeByte(this.associateRequestRQ.getReserved());
			//End of Protocol Version encoding - BUT POSSIBLY CHANGE THIS LATER
			
			this.client.writeByte(this.associateRequestRQ.getReserved());
			this.client.writeByte(this.associateRequestRQ.getReserved());
			
			this.client.write(this.associateRequestRQ.getCalledAE().getBytes());
			this.client.write(this.associateRequestRQ.getCallingAE().getBytes());
			
			for (int a = 0; a < 32; a ++) this.client.writeByte(this.associateRequestRQ.getReserved());
			
			//Application Context
			this.client.writeByte(this.associateRequestRQ.getApplicationContext().getItemType());
			this.client.writeByte(this.associateRequestRQ.getApplicationContext().getReserved());
			//this.client.writeInt(this.associateRequest.getApplicationContext().getItemLength());
			this.client.writeUInt16(this.associateRequestRQ.getApplicationContext().getItemLength());
			this.client.write(this.associateRequestRQ.getApplicationContext().getApplicationContextName().getBytes());
    		
			//Presentation Context
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getItemType());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
			//this.client.writeInt(this.associateRequest.getPresentationContext().getItemLength());
			this.client.writeUInt16(this.associateRequestRQ.getPresentationContext().getItemLength());
			//this.client.writeInt(this.associateRequest.getPresentationContext().getPresentationContextID());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getPresentationContextID());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getReserved());
			
			//Abstract Syntax
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getReserved());
			//this.client.writeInt(this.associateRequest.getPresentationContext().getAbstractSyntaxSubItem().getItemLength());
			this.client.writeUInt16(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getItemLength());
			this.client.write(this.associateRequestRQ.getPresentationContext().getAbstractSyntaxSubItem().getAbstractSyntaxName().getBytes());
			
			//Transfer Syntaxes
			int tsi = this.associateRequestRQ.getPresentationContext().getTransferSyntaxSubItems().size();
			
			for (int b = 0; b < tsi; b ++) {
				
				TransferSyntax transferSyntax = this.associateRequestRQ.getPresentationContext().getTransferSyntaxSubItems().get(b);
				this.client.writeByte(transferSyntax.getItemType());
				this.client.writeByte(transferSyntax.getReserved());
				//this.client.writeInt(transferSyntax.getItemLength());
				this.client.writeUInt16(transferSyntax.getItemLength());
				this.client.write(transferSyntax.getTransferSyntaxName().getBytes());
				
			}
			
			//User Information
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getItemLength());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getItemLength());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getMaxPDULengthReceive());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getItemLength());
			this.client.write(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationClassUIDSubItem().getImplementationClassUID().getBytes());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getItemLength());
			this.client.write(this.associateRequestRQ.getUserInformation().getImplementationItem().getImplementationVersionNameSubItem().getImplementationVersionName().getBytes());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getItemLength());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getMaximumNumberOperationsInvoked());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getAsynchronousOperationsWindowSubItem().getMaximumNumberOperationsPerformed());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getItemLength());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getUidLength());
			this.client.write((this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getSopClassUID()).getBytes());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getScuRole());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getScpSCURoleSelectionNegotiationSubItem().getScpRole());
			
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getItemType());
			this.client.writeByte(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getReserved());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getItemLength());
			this.client.writeInt(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getSopClassUIDLength());
			this.client.write(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getSopClassUID().getBytes());
			this.client.write(this.associateRequestRQ.getUserInformation().getExtendedNegotiationSubItem().getServiceClassApplicationInformation().getBytes());

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
    
    public boolean sendDataTF() {
    	
    	byte[] arr;
    	boolean retval = false;
    	
    	try {
    		
    		pl("HERE 1");
    		this.client.writeByte(this.dataTF.getPduType());
    		pl("HERE 2");
    		this.client.writeByte(this.dataTF.getReserved());
    		pl("HERE 3");
    		this.client.writeInt(this.dataTF.getPduLength());
    		pl("HERE 4");
    		
    		
    		/*
    		for (PresentationDataValue item: this.dataTF.getPresentationDataValueItems()) {
    			
    			this.client.writeInt(item.getItemLength());
    			this.client.writeByte(item.getPresentationContextID());
    			
    			//Will need a for loop here for multiple PDV Iteams
    			this.client.writeByte(item.getMessageControlHeader().getHeader());
    			
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
    			
    			
    		}*/
    		
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
    
    public boolean buildDataTF() {
    	
    	boolean retval = false;
    	
    	try {
    	
    		byte type;
    		
    		//FOR NOW JUST USE C-ECHO REQUEST 
    		//CHANGE THIS LATER SO DIFFERENT PDVS CAN BE SENT
    		
    		type = 0x04;
    		
    		PresentationContext_AC presentationContext = this.associateRequestAC.getPresentationContext();
    		int pcID = presentationContext.getPresentationContextID();
    		
    		MessageControlHeader header = new MessageControlHeader();
    		header.setIntHeader(192); //This will give the header a binary value of 11000000
    		
    		this.dataTF = new P_DATA_TF();
    		this.dataTF.setPduType(type);
    		
    		if (this.buildEchoRequest()) {
    		
    			PresentationDataValue pdValue = new PresentationDataValue(header, pcID, this.echoRequest);
    			ArrayList<PresentationDataValue> pdValueItems = new ArrayList<PresentationDataValue>();
    			pdValueItems.add(pdValue);
    			this.dataTF.setPresentationDataValueItems(pdValueItems);
    			retval = true;
    			
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

	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }

}
