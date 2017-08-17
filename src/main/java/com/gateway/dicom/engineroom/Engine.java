package com.gateway.dicom.engineroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

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

	private Client client;
    private C_ECHO_RQ echoRequest = null;
    private C_ECHO_RSP echoResponse = null;
    private A_ASSOCIATE_RQ associateRequestRQ = null;
    private A_ASSOCIATE_AC associateRequestAC = null;
    private A_ASSOCIATE_RJ associateRequestRJ = null;
    private A_RELEASE_RQ releaseRequestRQ = null;
    private A_RELEASE_RP releaseRequestRP = null;
    private A_ABORT abortRQ = null;
    private P_DATA_TF dataTF = null;
    private P_DATA_TF dataTFResponse = null;
    private ImplementationVersionNameSubItem impName;
    private ImplementationClassUIDSubItem impUID;
    private MaximumLengthSubItem maxLen;
    private int messageID;
    
    private byte[] targetAssociateRQData1;
    private int targetAssociateRQDataLen;
    private byte[] targetAssociateRQData2;
    
    private byte[] targetPDataTFData1;
    private int targetPDataTFDataLen;
    private byte[] targetPDataTFData2;
    
    private byte[] receivedData = null;
    private boolean requestReceived;
    private boolean requestAcknowledged;
    private boolean requestRejected;
    private boolean connected;
    private boolean requestBuilt;
    private boolean requestSent;
    private boolean dataReceived;
    
    private Scanner keyboard;
    
    private final byte A_ASSOCIATE_AC_PDU_TYPE = 0x02;
    private final byte A_ASSOCIATE_RJ_PDU_TYPE = 0x03;
    private final byte P_DATA_TF_PDU_TYPE = 0x04;
    private final byte C_ECHO_RSP_PDU_TYPE = 0x04;
    
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    
    public Engine() {}
    
    public Engine(Client client) { this.client = client; }
    
    public void run() {
    	
    	String input = "";
    	byte[] arr;
    	int length = 0;
    	byte type;
    	
    	try {
    	
    		this.keyboard = new Scanner(System.in);
    		this.connected = this.client.connectToServer();
    		
    		if (this.connected) {
    			
    			pl("Send A-ASSOCIATE-RQ Y/N?");
    			input = this.keyboard.nextLine().toUpperCase();
    			
    			if (input.equals("Y")) {
    				
    				this.requestBuilt = this.buildAssociateRequest();
    				
    				if (this.requestBuilt) {
    					
    					this.requestSent = this.sendAssociateRequest();
    					
    					if (this.requestSent) {
    						
    						type = this.client.readByte();
    						pl("PDU type received: " + type);
    						
    						if (type == this.A_ASSOCIATE_AC_PDU_TYPE) {
    							
    							pl("A-ASSOCIATE-RQ acknowledged");
    							this.dataReceived = true;
    							this.requestAcknowledged = true;
    							this.client.skip(1);
    							length = this.client.readInt();
    							pl("PDU length: " + length);
    							this.receivedData = this.client.readByteArray(length);
    							this.requestBuilt = this.buildAssociateAcknowledgement();
    							
    							if (! this.requestBuilt) {
    								
    								pl("There was a problem building the A-ASSOCIATE-AC\nDo you wish to continue Y/N?");
    				    			System.exit(0);
    								    				    			
    							}
    							
    							else {
    								
    								pl("Select from the following options\n\n1 - Send C-ECHO-RQ\n2 - Send C-STORE-RQ");
    				    			input = this.keyboard.nextLine().toUpperCase();
    				    			
    				    			if (input.equals("1")) {
    				    				
    				    				this.requestBuilt = this.buildDataTF(1);
    				    				
    				    				if (this.requestBuilt) {
    				    					
    				    					this.requestSent = this.sendDataTF();
    				    					
    				    					if (this.requestSent) {
    				    						
    				    						pl("Successfully sent P-DATA-TF C-ECHO-RQ");
    				    						type = this.client.readByte();
    				    						pl("PDU type received: " + type);
    				    						
    				    						if (type == this.P_DATA_TF_PDU_TYPE) {
    				    							
    				    							pl("Received a P-DATA-TF in response");
    				    							this.dataTFResponse = new P_DATA_TF();
    				    							this.dataTFResponse.setPduType(type);
    				    							this.dataReceived = true;
    				    							this.client.skip(1);
    				    							length = this.client.readInt();
    				    							this.dataTFResponse.setPduLength(length);
    				    							pl("PDU length: " + length);
    				    							this.dataInputStream = this.client.getDataInputStream();
    				    							//this.receivedData = this.client.readByteArray(length);
    				    							//this.requestBuilt = this.buildDataTFResponse();
    				    							this.requestBuilt = this.buildEchoResponse();
    				    							
    				    							if (this.requestBuilt) pl("C-ECHO was successful");
    				    							else pl("C-ECHO was not successful");
    				    							
    				    						}
    				    						
    				    						/*if (type == this.C_ECHO_RSP_PDU_TYPE) {
    				    							
    				    							pl("C-ECHO-RQ acknowledged");
    				    							this.dataReceived = true;
    				    							this.requestAcknowledged = true;
    				    							this.client.skip(1);
    				    							length = this.client.readInt();
    				    							pl("PDU length: " + length);
    				    							this.receivedData = this.client.readByteArray(length);
    				    							this.requestBuilt = this.buildEchoResponse();
    				    							
    				    							
    				    							
    				    						}*/
    				    						
    				    					}
    				    					
    				    					else {
    				    						
    				    						pl("There was a problem sending P-DATA-TF C-ECHO-RQ");
    				    						
    				    					}
    				    					
    				    				}
    				    				
    				    				else {
    				    					
    				    					pl("There was a problem building the P-DATA-TF C-ECHO-RQ - Exiting");
    				    					System.exit(0);
    				    					
    				    				}
    				    				
    				    			}
    				    			
    				    			else if (input.equals("2")) {
    				    				
    				    			}
    				    			
    				    			else {
    				    				
    				    				pl("Not a valid choice - Exiting programme");
    				    				System.exit(0);
    				    				
    				    			}
    								
    							}
    							
    						}
    						
    						else if (type == this.A_ASSOCIATE_RJ_PDU_TYPE){
    							
    							pl("A-ASSOCIATE-RQ rejected");
    							
    						}
    						
    						
    					}
    					
    					else {
    						
    						pl("Error sending A-ASSOCIATE-RQ");
        					System.exit(0);
    						
    					}
    					
    				}
    				
    				else {
    					
    					pl("Error building A-ASSOCIATE-RQ");
    					System.exit(0);
    					
    				}
    				
    			}
    			
    		}
    		
    		else {
    			
    			pl("Could not connect to the PACS");
    			System.exit(0);
    			
    		}
    		
    		
    	}
		
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    		
    }
    
    /*public void run() {
    	
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
	    	        		
	    		        		if (this.buildDataTF()) {
	    		        			
	    		        			this.requestSent = this.sendDataTF();
	    		        			
	    		        			if (this.requestSent) {
	    		        				
	    		        				pl("P-DATA-TF sent successfully");
	    		        				//this.client.setSkip(120);
	    		        				this.dataReceived = this.client.receive();
	    		        				
	    		        				if (this.dataReceived) {
	    		        					
	    		        					pl("Data received from PACS");
	    		        					this.receivedData = this.client.getReceivedData();
	    		        					   		        					
	    		        				}
	    		        				
	    		        				else {
	    		        				
	    		        					pl("No data received from PACS");
	    		        					this.requestAcknowledged = true;
		    	    		        		this.requestRejected = false;
	    		        					
	    		        				}
	    		        				
	    		        				
	    		        				
	    		        			}
	    		        			
	    		        			else {
	    		        				
	    		        				pl("P-DATA-TF not sent successfully");
	    		        				this.requestAcknowledged = false;
	    	    		        		this.requestRejected = true;
	    		        				
	    		        			}
	    		        			
	    		        		}
	    		        		
	    		        		else {
	    		        		
	    		        			pl("P-DATA-TF was not successfully built");
		    		        		
	    		        			
	    		        		}
	    		        		
	    		        		//Send the A_RELEASE-RQ
	    		        		if (this.buildReleaseRequest()) {
	    		        			
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
	    		        			
	    		        		}
	    		        		
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
	    				
	    				pl("A-ASSOCIATE-RQ was not successfully sent");
	    				
	    			}
	    			
	    		}
	    		
	    		else {
	    			
	    			pl("A-ASSOCIATE-RQ was not successfully built");
	    			
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
    	
    }*/
    
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
    	List<PresentationContext_AC> presentationContexts = new ArrayList<PresentationContext_AC>();
    	AbstractSyntax abstractSyntax;
    	List<TransferSyntax> transferSyntaxes = new ArrayList<TransferSyntax>();
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
    	int count = 0;
    	int length = 0;
    	byte[] arr;
    	byte[] subData;
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
    	
    	if ((this.receivedData != null) && (this.isRequestAcknowledged() == true)) {
    		
    		this.associateRequestAC = new A_ASSOCIATE_AC();
    		this.associateRequestAC.setPduType(this.A_ASSOCIATE_AC_PDU_TYPE);
    	    this.associateRequestAC.setPduLength(this.receivedData.length);
    	    
    	    b1 = this.receivedData[0];
    	    b2 = this.receivedData[1];
    	    
    	    i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    
    	    this.associateRequestAC.setProtocolVersion(i);
    	    pl("Protocol Version: " + i);
    	    
    	    //Next two positions in the array are reserved and can be ignored
    	    
    	    arr = Arrays.copyOfRange(this.receivedData, 4, 20);
    	    s = new String(arr);
    	    this.associateRequestRQ.setCalledAE(s);
    	    pl("Called AE: " + s);
    	    
    	    arr = Arrays.copyOfRange(this.receivedData, 20, 36);
    	    s = new String(arr);
    	    this.associateRequestRQ.setCallingAE(s);
    	    pl("Calling AE: " + s);
    	    
    	    //Next 32 positions in the array are reserved and can be ignored
    	    pos = 68;
    	    
    	    arr = Arrays.copyOfRange(this.receivedData, pos, this.receivedData.length);
    	    b = arr[0];
    	    
    	    if (b == 0x10) {
    	    	
    	    	//Skip the next bit (arr[1])
    	    	b1 = arr[2];
        	    b2 = arr[3];
        	    
        	    i = ((0xFF & b1) << 8) | (0xFF & b2);
    	    	length = i;
    	    	subData = Arrays.copyOfRange(arr, 4, 4 + length);
        	    s = new String(subData);
        	    applicationContext = new ApplicationContext();
        	    applicationContext.setItemType(b);
        	    applicationContext.setItemLength(length);
        	    applicationContext.setApplicationContextName(s);
        	    this.associateRequestAC.setApplicationContext(applicationContext);
        	    pl("Application Context: " + s);
    	    	
        	    pos += (4 + length);
        	    //arr = Arrays.copyOfRange(this.receivedData, 68 + 4 + length, this.receivedData.length);
        	    arr = Arrays.copyOfRange(this.receivedData, pos, this.receivedData.length);
        	    count = 0;
        	    b = 0x21;
        	    
        	    /*for (int a = 0; a < arr.length; a ++)
        	    	
        	    	pl("arr[" + a + "]: " + arr[a]);*/
        	    
        	    for (int a = 0; a < arr.length; a ++) 
        	    	
        	    	//pl("arr[" + a + "]: " + arr[a]);
        	    	if(arr[a] == b && arr[a + 1] == 0 && a + 1 < arr.length) 
        	    		
        	    		count ++;
        	    	
        	    pl("Presentation Contexts found: " + count);
        	    
        	    if (count == 1) {
        	    	
        	    	presentationContext = new PresentationContext_AC();
        	    	presentationContext.setItemType(b);
        	    	
        	    	//Skip the next byte (arr[1])
        	    	b1 = arr[2];
            	    b2 = arr[3];
            	    
            	    i = ((0xFF & b1) << 8) | (0xFF & b2);
        	    	
            	    length = i;
        	    	presentationContext.setItemLength(length);
        	    	b = arr[4];
        	    	presentationContext.setPresentationContextID(b);
        	    	
        	    	//Skip the next bytes (arr[5 - 7])
        	    	
        	    	//arr = Arrays.copyOfRange(arr, 8, 4 + length);
        	    	pos += 8;
        	    	arr = Arrays.copyOfRange(this.receivedData, pos, this.receivedData.length);
        	    	b = arr[0];
        	    	
        	    	/*for (int a = 0; a < arr.length; a ++)
            	    	
            	    	pl("arr[" + a + "]: " + arr[a]);*/
        	    	
        	    	if (b == 0x40) {
        	    	
        	    		transferSyntax = new TransferSyntax();
        	    		transferSyntax.setItemType(b);
        	    		
            	    	//Skip the next byte (arr[1])
            	    	b1 = arr[2];
                	    b2 = arr[3];
                	    
                	    i = ((0xFF & b1) << 8) | (0xFF & b2);
            	    	
                	    length = i;
                	    pos += (4 + length);
                	    transferSyntax.setItemLength(length);
        	    	    subData = Arrays.copyOfRange(arr, 4, length);
                	    s = new String(subData);
                	    transferSyntax.setTransferSyntaxName(s);
                	    transferSyntaxes.add(transferSyntax);
                	    presentationContext.setTransferSyntaxSubItems(transferSyntaxes);
                	    presentationContexts.add(presentationContext);
                	    this.associateRequestAC.setPresentationContexts(presentationContexts);
                	    pl("Presentation Context: " + presentationContext.getPresentationContextID());
                	    pl("Transfer Syntax: " + s);
                	    
                	    arr = Arrays.copyOfRange(this.receivedData, pos, this.receivedData.length);
                	    
                	    /*for (int a = 0; a < arr.length; a ++)
                	    	
                	    	pl("arr[" + a + "]: " + arr[a]);*/
                	    
                	    
                	    
        	    	}
        	    	
        	    	else {
        	    		
        	    	}
        	    	
        	    }
        	    
        	    else if (count < 1) {
        	    	
        	    	pl("No Presentation Context has been found in the acknowledgement");
        	    	retval = false;
        	    	
        	    }
        	    
        	    else if (count > 1) {
        	    	
        	    	pl("More than one Presentation Context has been found in the acknowledgement");
        	    	retval = true;
        	    	
        	    }
        	    
    	    }
    	    
    	    else {
    	    	
    	    	pl("No Application Context has been found in the acknowledgement");
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
    
    public boolean buildEchoRequest() {
    	
    	boolean retval = false;
    	short randomNum = (short) (ThreadLocalRandom.current().nextInt(0, 65535) - 32768);
    	this.echoRequest = new C_ECHO_RQ(randomNum);
    	this.messageID = this.echoRequest.getMessageID().getIntElementData();
    	retval = true;
    	return retval;
    	
    }
    
    public boolean buildEchoResponse() {
    	
    	boolean retval = false;
    	int pdvItemLength;
    	byte pid;
    	byte command;
    	int m;
    	int n;
    	int groupNumber;
    	int elementNumber;
    	int elementLength;
    	int intData;
    	String strData;
    	byte b1;
    	byte b2;
    	byte b3;
    	byte b4;
    	DataElement commandGroupLength;
    	DataElement affectedSOPClassUID;
    	DataElement commandField;
    	DataElement messageID;
    	DataElement dataSetType;
    	DataElement status;
    	byte[] arr;
    	
    	try {

    		pdvItemLength = this.dataInputStream.readInt();
    		pid = this.dataInputStream.readByte();
    		command = this.dataInputStream.readByte();
    		
    		switch(pid) {
        	
	        	case 1 : //Implicit VR Little Endian - read array from right to left
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b4, b3, b2, b1);
	        		
	        		commandGroupLength = new DataElement();
	        		commandGroupLength.setGroupNumber(groupNumber);
	        		commandGroupLength.setElementNumber(elementNumber);
	        		commandGroupLength.setElementLength(elementLength);
	        		commandGroupLength.setIntElementData(intData);
	        		//pl("Command Group Length Group Number: " + groupNumber);
	        		//pl("Command Group Length Element Number: " + elementNumber);
	        		//pl("Command Group Length Element Length: " + elementLength);
	        		//pl("Command Group Length Element Data: " + intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		arr = new byte[elementLength];
	        		
	        		for (int a = 0; a < elementLength; a ++) 
	        			
	        			arr[a] = this.dataInputStream.readByte();
	        		
	        		strData = new String(arr);
	        		
	        		affectedSOPClassUID = new DataElement();
	        		affectedSOPClassUID.setGroupNumber(groupNumber);
	        		affectedSOPClassUID.setElementNumber(elementNumber);
	        		affectedSOPClassUID.setElementLength(elementLength);
	        		affectedSOPClassUID.setElementData(strData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b2, b1);
	        		
	        		commandField = new DataElement();
	        		commandField.setGroupNumber(groupNumber);
	        		commandField.setElementNumber(elementNumber);
	        		commandField.setElementLength(elementLength);
	        		commandField.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b2, b1);
	        		
	        		messageID = new DataElement();
	        		messageID.setGroupNumber(groupNumber);
	        		messageID.setElementNumber(elementNumber);
	        		messageID.setElementLength(elementLength);
	        		messageID.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b2, b1);
	        		
	        		dataSetType = new DataElement();
	        		dataSetType.setGroupNumber(groupNumber);
	        		dataSetType.setElementNumber(elementNumber);
	        		dataSetType.setElementLength(elementLength);
	        		dataSetType.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b2, b1);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b4, b3);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b4, b3, b2, b1);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b2, b1);
	        		
	        		status = new DataElement();
	        		status.setGroupNumber(groupNumber);
	        		status.setElementNumber(elementNumber);
	        		status.setElementLength(elementLength);
	        		status.setIntElementData(intData);
	        		
	        		break;
	        	case 2 : //Implicit VR Big Endian - read array from left to right
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2, b3, b4);
	        		
	        		commandGroupLength = new DataElement();
	        		commandGroupLength.setGroupNumber(groupNumber);
	        		commandGroupLength.setElementNumber(elementNumber);
	        		commandGroupLength.setElementLength(elementLength);
	        		commandGroupLength.setIntElementData(intData);
	        		//pl("Command Group Length Group Number: " + groupNumber);
	        		//pl("Command Group Length Element Number: " + elementNumber);
	        		//pl("Command Group Length Element Length: " + elementLength);
	        		//pl("Command Group Length Element Data: " + intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		arr = new byte[elementLength];
	        		
	        		for (int a = 0; a < elementLength; a ++) 
	        			
	        			arr[a] = this.dataInputStream.readByte();
	        		
	        		strData = new String(arr);
	        		
	        		affectedSOPClassUID = new DataElement();
	        		affectedSOPClassUID.setGroupNumber(groupNumber);
	        		affectedSOPClassUID.setElementNumber(elementNumber);
	        		affectedSOPClassUID.setElementLength(elementLength);
	        		affectedSOPClassUID.setElementData(strData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		commandField = new DataElement();
	        		commandField.setGroupNumber(groupNumber);
	        		commandField.setElementNumber(elementNumber);
	        		commandField.setElementLength(elementLength);
	        		commandField.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		messageID = new DataElement();
	        		messageID.setGroupNumber(groupNumber);
	        		messageID.setElementNumber(elementNumber);
	        		messageID.setElementLength(elementLength);
	        		messageID.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		dataSetType = new DataElement();
	        		dataSetType.setGroupNumber(groupNumber);
	        		dataSetType.setElementNumber(elementNumber);
	        		dataSetType.setElementLength(elementLength);
	        		dataSetType.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		status = new DataElement();
	        		status.setGroupNumber(groupNumber);
	        		status.setElementNumber(elementNumber);
	        		status.setElementLength(elementLength);
	        		status.setIntElementData(intData);
	        		
	        		break;
	        	default : 
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2, b3, b4);
	        		
	        		commandGroupLength = new DataElement();
	        		commandGroupLength.setGroupNumber(groupNumber);
	        		commandGroupLength.setElementNumber(elementNumber);
	        		commandGroupLength.setElementLength(elementLength);
	        		commandGroupLength.setIntElementData(intData);
	        		//pl("Command Group Length Group Number: " + groupNumber);
	        		//pl("Command Group Length Element Number: " + elementNumber);
	        		//pl("Command Group Length Element Length: " + elementLength);
	        		//pl("Command Group Length Element Data: " + intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		arr = new byte[elementLength];
	        		
	        		for (int a = 0; a < elementLength; a ++) 
	        			
	        			arr[a] = this.dataInputStream.readByte();
	        		
	        		strData = new String(arr);
	        		
	        		affectedSOPClassUID = new DataElement();
	        		affectedSOPClassUID.setGroupNumber(groupNumber);
	        		affectedSOPClassUID.setElementNumber(elementNumber);
	        		affectedSOPClassUID.setElementLength(elementLength);
	        		affectedSOPClassUID.setElementData(strData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		commandField = new DataElement();
	        		commandField.setGroupNumber(groupNumber);
	        		commandField.setElementNumber(elementNumber);
	        		commandField.setElementLength(elementLength);
	        		commandField.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		messageID = new DataElement();
	        		messageID.setGroupNumber(groupNumber);
	        		messageID.setElementNumber(elementNumber);
	        		messageID.setElementLength(elementLength);
	        		messageID.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		dataSetType = new DataElement();
	        		dataSetType.setGroupNumber(groupNumber);
	        		dataSetType.setElementNumber(elementNumber);
	        		dataSetType.setElementLength(elementLength);
	        		dataSetType.setIntElementData(intData);
	        		
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		groupNumber = this.convertBytesToInt(b1, b2);
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementNumber = this.convertBytesToInt(b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		b3 = this.dataInputStream.readByte();
	        		b4 = this.dataInputStream.readByte();
	        		elementLength = this.convertBytesToInt(b1, b2, b3, b4);
	        		b1 = this.dataInputStream.readByte();
	        		b2 = this.dataInputStream.readByte();
	        		intData = this.convertBytesToInt(b1, b2);
	        		
	        		status = new DataElement();
	        		status.setGroupNumber(groupNumber);
	        		status.setElementNumber(elementNumber);
	        		status.setElementLength(elementLength);
	        		status.setIntElementData(intData);
	        		
	        		break;
	    		
    		}
    		
    		this.echoResponse = new C_ECHO_RSP();
    		this.echoResponse.setCommandGroupLength(commandGroupLength);
    		this.echoResponse.setAffectedSOPClassUID(affectedSOPClassUID);
    		this.echoResponse.setCommandField(commandField);
    		this.echoResponse.setMessageIDBeingRespondedTo(messageID);
    		this.echoResponse.setCommandDataSetType(dataSetType);
    		this.echoResponse.setStatus(status);
    		
    		pl("Message ID: " + this.echoResponse.getMessageIDBeingRespondedTo().getIntElementData());
    		pl("Original Message ID: " + this.messageID);
    		pl("Command: " + this.echoResponse.getCommandField().getIntElementData());
    		pl("Status: " + this.echoResponse.getStatus().getIntElementData());
    		
    		if ((this.echoResponse.getMessageIDBeingRespondedTo().getIntElementData() == this.messageID && 
				(this.echoResponse.getCommandField().getIntElementData() == 32816) && 
				(this.echoResponse.getStatus().getIntElementData() == 0))) 
    				
    			retval = true;
    		
    		else retval = false;
    		
    	}
    	
    	catch (Exception e) {
    	
    		pl("There was a problem building the C-ECHO-RSP response");
    		e.printStackTrace();
    		
    	}
  
    	return retval;
    	
    }
    
    public boolean buildEchoResponse(byte[] arr, int byteOrder) {
    	
    	boolean retval = false;
    	int m = 0;
    	int n = 0;
    	int length = 0;
    	int intData = 0;
    	int pos = 0;
    	DataElement commandGroupLength;
    	DataElement affectedSOPClassUID;
    	DataElement commandField;
    	byte[] subData;
    	String s = "";
    	
    	if (arr != null) {
    		
    		this.echoResponse = new C_ECHO_RSP();
    		
    		switch(byteOrder) {
        	
	        	case 1 : //Implicit VR Little Endian - read array from right to left
	        		m = this.convertBytesToInt(arr[1], arr[0]);
	        		n = this.convertBytesToInt(arr[3], arr[2]);
	        		length = this.convertBytesToInt(arr[7], arr[6], arr[5], arr[4]);
	        		intData = this.convertBytesToInt(arr[11], arr[10], arr[9], arr[8]);
	        		break;
	        	case 2 : //Implicit VR Big Endian - read array from left to right
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		intData = this.convertBytesToInt(arr[8], arr[9], arr[10], arr[11]);
	        		break;
	        	default : 
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		intData = this.convertBytesToInt(arr[8], arr[9], arr[10], arr[11]);
	        		break;
        		
        	}

    		commandGroupLength = new DataElement();
    		commandGroupLength.setGroupNumber(m);
    		commandGroupLength.setElementNumber(n);
    		commandGroupLength.setElementLength(length);
    		commandGroupLength.setIntElementData(intData);
    		
    		arr = Arrays.copyOfRange(arr, 12, arr.length);
    		
    		/*for (int a = 0; a < arr.length; a ++) 
    			
    			pl("" + arr[a]);*/
    		
    		switch(byteOrder) {
        	
	        	case 1 : //Implicit VR Little Endian - read array from right to left
	        		m = this.convertBytesToInt(arr[1], arr[0]);
	        		n = this.convertBytesToInt(arr[3], arr[2]);
	        		length = this.convertBytesToInt(arr[7], arr[6], arr[5], arr[4]);
	        		break;
	        	case 2 : //Implicit VR Big Endian - read array from left to right
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		break;
	        	default : 
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		break;
	    		
    		}
    		
    		affectedSOPClassUID = new DataElement();
    		affectedSOPClassUID.setGroupNumber(m);
    		affectedSOPClassUID.setElementNumber(n);
    		affectedSOPClassUID.setElementLength(length);
    		
    		if (length % 2 == 0) pos = 7 + length; 
    		else pos = 8 + length;
    			
    		subData = Arrays.copyOfRange(arr, 8, pos);
    		
    		s = new String(subData);
    		affectedSOPClassUID.setElementData(s);
    		
    		/*for (int a = 0; a < subData.length; a ++) 
    			
    			pl("" + subData[a]);*/
    		
    		arr = Arrays.copyOfRange(arr, pos + 1, arr.length);
    		
    		/*for (int a = 0; a < arr.length; a ++) 
    			
    			pl("" + arr[a]);*/
    		
    		switch(byteOrder) {
        	
	        	case 1 : //Implicit VR Little Endian - read array from right to left
	        		m = this.convertBytesToInt(arr[1], arr[0]);
	        		n = this.convertBytesToInt(arr[3], arr[2]);
	        		length = this.convertBytesToInt(arr[7], arr[6], arr[5], arr[4]);
	        		intData = this.convertBytesToInt(arr[9], arr[8]);
	        		pl("" + arr[9]);
	        		pl("" + arr[8]);
	        		pl("" + intData);
	        		int test = arr[9];
	        		pl("" + test);
	        		break;
	        	case 2 : //Implicit VR Big Endian - read array from left to right
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		intData = this.convertBytesToInt(arr[8], arr[9]);
	        		break;
	        	default : 
	        		m = this.convertBytesToInt(arr[0], arr[1]);
	        		n = this.convertBytesToInt(arr[2], arr[3]);
	        		length = this.convertBytesToInt(arr[4], arr[5], arr[6], arr[7]);
	        		intData = this.convertBytesToInt(arr[8], arr[9]);
	        		break;
	    		
			}
    		
    		commandField = new DataElement();
    		commandField.setGroupNumber(m);
    		commandField.setElementNumber(0x0100);
    		commandField.setElementLength(length);
    		commandField.setIntElementData(intData);
    		
    	}
    	
    	else {
    		
    		pl("Problem building C-ECHO-RSP");
    		retval = false;
    		
    	}
    	
    	//this.echoResponse = new C_ECHO_RSP();
    	//retval = true;
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
    
    public boolean buildDataTF(int n) {
    	
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
    		
    		switch (n) {
    		
	    		case 1 : //C-ECHO-RQ
	    			
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
	        			this.dataTF.writeToBuffer();
	        			retval = true;
	        			
	        			/*for (int a = 0; a < this.targetPDataTFData1.length; a ++)
	        				
	        				p("" + this.targetPDataTFData1[a]);
	        			
	        			p("" + this.targetPDataTFDataLen);
	        			
	        			for (int b = 0; b < this.targetPDataTFData2.length; b ++)
	        			
	        				p("" + this.targetPDataTFData2[b]);
	        				
	        			pl();*/
	        			
	        			/*byte[] arr3 = {0x04, 0x00};
	        			len = this.dataTF.getPduLength();
	        			pl("LEN: " + len);
	        			byte[] arr4 = this.dataTF.getBuffer().toByteArray();
	        			
	        			for (int c = 0; c < arr3.length; c ++)
	        				
	        				p("" + arr3[c]);
	        			
	        			p("" + len);
	        			
	        			for (int d = 0; d < arr4.length; d ++) 
	        				
	        				p("" + arr4[d]);
	        			
	        			
	        			byte[] arr5 = this.dataTF.getBuffer().toByteArray();
	        			
	        			for (int e = 0; e < arr5.length; e ++)
	        				
	        				p("" + arr5[e]);
	        			*/
	        			
	        			
	        		}
	        		
	        		else {
	        			
	        			//Problem building Echo Request
	        			pl("C-ECHO-RQ was not built successfully");
	        			retval = false;
	        			
	        		}

	    			
	    			break;
	    			
	    		case 2 : break;
    		
    		}
    		    		
    	}
    	
    	catch (Exception e) {

        	pl(e.getMessage());
        	e.printStackTrace();
        	retval = false;
    		
    	}
    	
    	return retval;
    	
    }
    
    
    public boolean buildDataTFResponse() {
    	
    	boolean retval = false;
    	byte b1;
    	byte b2;
    	int length;
    	PresentationDataValue pdv;
    	int n = 0;
    	int pid = 0;
    	byte[] arr;
    	
    	/*for (int a = 0; a < this.receivedData.length; a ++) 
    		
    		pl("" + this.receivedData[a]);*/
    	
	    length = this.convertBytesToInt(this.receivedData[0], this.receivedData[1], this.receivedData[2], this.receivedData[3]);
    	pdv = new PresentationDataValue();
    	pdv.setItemLength(length);
    	pid = this.receivedData[4];
    	pdv.setPresentationContextID(pid);
    	n = this.receivedData[5];
    	pdv.setMessageControlHeader(n);
    	arr = Arrays.copyOfRange(this.receivedData, 6, this.receivedData.length);
    	
    	this.buildEchoResponse(arr, pid);
	    
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
    		
    		this.client.write(this.dataTF.getBuffer().toByteArray());
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

	public boolean isRequestReceived() {
		return requestReceived;
	}

	public void setRequestReceived(boolean requestReceived) {
		this.requestReceived = requestReceived;
	}

	private int convertBytesToInt(byte b1, byte b2) {
		
	    int retval = ((0xFF & b1) << 8) | (0xFF & b2);
		return retval;
		
	}
	
	private int convertBytesToInt(byte b1, byte b2, byte b3, byte b4) {
		
		int retval = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |
	            ((0xFF & b3) << 8) | (0xFF & b4);
		return retval;
		
	}
	
	private void p(String s) { System.out.print(s); }
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }

}
