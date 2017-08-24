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
import com.gateway.dicom.protocols.C_STORE_RQ;
import com.gateway.dicom.protocols.ImagePacket;
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
import com.gateway.dicom.imagetypes.DicomImageGenerator;

public class Engine {

	private Client client;
	private DicomImageGenerator imageGenerator;
    private C_ECHO_RQ echoRequest = null;
    private C_ECHO_RSP echoResponse = null;
    private C_STORE_RQ storeRequest = null;
    private List<P_DATA_TF> imagePackets = null;
    private ImagePacket imagePacket = null;
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
    
    private String filePath;
    private List<String> filePaths;
    
    public Engine() {}
    
    public Engine(Client client) { 
    	this.client = client; 
    }
    
    public Engine(DicomImageGenerator imageGenerator) { 
    	this.imageGenerator = imageGenerator;
    }
    
    public Engine(Client client, DicomImageGenerator imageGenerator) { 
    	this.client = client; 
    	this.imageGenerator = imageGenerator;
    }
    
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
    				    							
    				    							if (this.requestBuilt) {
    				    								
    				    								pl("C-ECHO was successful");
    				    								this.requestAcknowledged = true;
    				    								
    				    								pl("Send A-RELEASE-RQ Y/N?");
    				    				    			input = this.keyboard.nextLine().toUpperCase();
    				    								
    				    				    			if (input.equals("Y")) {
    				    				    				
    				    				    				this.requestBuilt = this.buildReleaseRequest();
    				    				    				
    				    				    				if (this.requestBuilt) {
    				    				    					
    				    				    					this.requestSent = this.sendReleaseRequest();
    				    				    					
    				    				    					if (this.requestSent) {
    				    				    						
    				    				    						pl("Sucessfully sent A-RELEASE-RQ");
    				    				    						
    				    				    					}
    				    				    					
    				    				    					else {
    				    				    						
    				    				    						pl("There was a problem sending A-RELEASE-RQ");
        				    				    					System.exit(0);
    				    				    						
    				    				    					}
    				    				    					
    				    				    				}
    				    				    				
    				    				    				else {
    				    				    					
    				    				    					pl("There was a problem building A-RELEASE-RQ");
    				    				    					System.exit(0);
    				    				    					
    				    				    				}
    				    				    				
    				    				    			}
    				    				    			
    				    				    			else {
    				    				    				
    				    				    				pl("Exiting programme");
    				    				    				System.exit(0);
    				    				    				
    				    				    			}
    				    				    			
    				    							}
    				    							
    				    							else {
    				    								
    				    								pl("C-ECHO was not successful");
    				    								System.exit(0);
    				    								
    				    							}
    				    							
    				    						}
    				    						
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
    				    			
    				    				this.requestBuilt = this.buildDataTF(2);
    				    				
    				    				if (this.requestBuilt) {
    				    					
    				    					this.requestSent = this.sendDataTF();
    				    					
    				    					if (this.requestSent) {
    				    						
    				    						pl();
    				    						pl("Successfully sent P-DATA-TF C-STORE-RQ");
    				    						this.filePath = this.imageGenerator.getFilePath();
    				    						this.imageGenerator.setHeight(512);
    				    						this.imageGenerator.setWidth(512);
    				    						this.requestBuilt = this.buildDataTF(3);
    				    						
    				    						if (this.requestBuilt) {
    				    						
    				    							this.requestSent = this.sendImagePackets();
    				    							
    				    							if (this.requestSent) {
    				    								
    				    								pl("Successfully sent image packets");
    				    								
    				    								pl("HERE 1");
    	    				    						type = this.client.readByte();
    	    				    						pl("HERE 2");
    	    				    						pl("PDU type received: " + type);
    	    				    						pl("HERE 3");
    	    				    						
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
    	    				    							System.exit(0);
    	    				    							
    	    				    						}

    				    								
    				    							} 
    				    							
    				    							else {
    				    								
    				    								pl("There was a problem sending the image packets");
    				    								System.exit(0);
    				    								
    				    							}
    				    							
    				    						}
    				    						
    				    						else {
    				    							
    				    							pl("There was an error building the image packets");
    				    							System.exit(0);
    				    							
    				    						}
    				    						
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
    	int randomNum = (short) (ThreadLocalRandom.current().nextInt(0, 65535) - 32768);
    	//this.echoRequest = new C_ECHO_RQ(randomNum);
    	this.echoRequest = new C_ECHO_RQ(3);
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
    		
    		/*if ((this.echoResponse.getMessageIDBeingRespondedTo().getIntElementData() == this.messageID && 
				(this.echoResponse.getCommandField().getIntElementData() == 32816) && 
				(this.echoResponse.getStatus().getIntElementData() == 0))) 
    				
    			retval = true;*/
    		
    		if ((this.echoResponse.getCommandField().getIntElementData() == 32816) && 
				(this.echoResponse.getStatus().getIntElementData() == 0))
    		
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
	        		int test = arr[9];
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
    
    public boolean buildStoreRequest() {
    	    	
    	boolean retval = false;
    	//this.storeRequest = new C_STORE_RQ(2, "1.2.840.10008.5.1.4.1.1.7", this.messageID, 0, "1.3.6.1.4.1.5962.99.1.2280943358.726300484.1363785608958.64.0");
    	//this.storeRequest = new C_STORE_RQ(2, "1.2.840.10008.5.1.4.1.1.2", this.messageID, 0, "1.2.826.0.1.3680043.8.1055.1.20111102150758591.03296050.69180943");
    	this.messageID = 3;
    	this.storeRequest = new C_STORE_RQ(2, "1.2.840.10008.5.1.4.1.1.4", this.messageID, 0, "0.0.0.0.1.8811.2.1.20010413115754.12432");
    	retval = true;
    	return retval;
    	
    }
    
    public boolean buildImagePayload() {
    	
    	boolean retval = false;
    	
    	byte[] arr1 = {0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x06
    			,0x20
    			,0x00
    			,0x00
    			,0x06
    			,0x1c
    			,0x01
    			,0x02

    			,0x08
    			,0x00
    			,0x08
    			,0x00
    			,0x14
    			,0x00
    			,0x00
    			,0x00
    			,0x4f
    			,0x52
    			,0x49
    			,0x47
    			,0x49
    			,0x4e
    			,0x41
    			,0x4c

    			,0x5c
    			,0x50
    			,0x52
    			,0x49
    			,0x4d
    			,0x41
    			,0x52
    			,0x59
    			,0x5c
    			,0x4d
    			,0x50
    			,0x52
    			,0x08
    			,0x00
    			,0x16
    			,0x00

    			,0x1a
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x2e
    			,0x32
    			,0x2e
    			,0x38
    			,0x34
    			,0x30
    			,0x2e
    			,0x31
    			,0x30
    			,0x30
    			,0x30

    			,0x38
    			,0x2e
    			,0x35
    			,0x2e
    			,0x31
    			,0x2e
    			,0x34
    			,0x2e
    			,0x31
    			,0x2e
    			,0x31
    			,0x2e
    			,0x34
    			,0x00
    			,0x08
    			,0x00

    			,0x18
    			,0x00
    			,0x28
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x31
    			,0x2e

    			,0x38
    			,0x38
    			,0x31
    			,0x31
    			,0x2e
    			,0x32
    			,0x2e
    			,0x31
    			,0x2e
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30
    			,0x34
    			,0x31

    			,0x33
    			,0x31
    			,0x31
    			,0x35
    			,0x37
    			,0x35
    			,0x34
    			,0x2e
    			,0x31
    			,0x32
    			,0x34
    			,0x33
    			,0x32
    			,0x00
    			,0x08
    			,0x00

    			,0x20
    			,0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30
    			,0x33
    			,0x31
    			,0x36
    			,0x08
    			,0x00

    			,0x21
    			,0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30
    			,0x33
    			,0x31
    			,0x36
    			,0x08
    			,0x00

    			,0x22
    			,0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30
    			,0x33
    			,0x31
    			,0x36
    			,0x08
    			,0x00

    			,0x23
    			,0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30
    			,0x33
    			,0x32
    			,0x33
    			,0x08
    			,0x00

    			,0x30
    			,0x00
    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x34
    			,0x33
    			,0x30
    			,0x30
    			,0x38
    			,0x08
    			,0x00
    			,0x31
    			,0x00

    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x34
    			,0x33
    			,0x34
    			,0x31
    			,0x34
    			,0x08
    			,0x00
    			,0x32
    			,0x00
    			,0x06
    			,0x00

    			,0x00
    			,0x00
    			,0x31
    			,0x34
    			,0x33
    			,0x34
    			,0x31
    			,0x35
    			,0x08
    			,0x00
    			,0x33
    			,0x00
    			,0x06
    			,0x00
    			,0x00
    			,0x00

    			,0x31
    			,0x34
    			,0x33
    			,0x30
    			,0x30
    			,0x36
    			,0x08
    			,0x00
    			,0x50
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x08
    			,0x00

    			,0x60
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x4d
    			,0x52
    			,0x08
    			,0x00
    			,0x70
    			,0x00
    			,0x12
    			,0x00
    			,0x00
    			,0x00

    			,0x47
    			,0x45
    			,0x20
    			,0x4d
    			,0x65
    			,0x64
    			,0x69
    			,0x63
    			,0x61
    			,0x6c
    			,0x20
    			,0x53
    			,0x79
    			,0x73
    			,0x74
    			,0x65

    			,0x6d
    			,0x73
    			,0x08
    			,0x00
    			,0x08
    			,0x00
    			,0x1c
    			,0x00
    			,0x00
    			,0x00
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20

    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20

    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x08
    			,0x00};
    	
    	int len1 = 0x90;
    	
    	byte[] arr2 = {
    			0x00
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x20
    			,0x20

    			,0x20
    			,0x20
    			,0x08
    			,0x00
    			,0x10
    			,0x10
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x4d
    			,0x52
    			,0x53
    			,0x51
    			,0x08
    			,0x00

    			,0x30
    			,0x10
    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x42
    			,0x52
    			,0x41
    			,0x49
    			,0x4e
    			,0x20
    			,0x08
    			,0x00
    			,0x3e
    			,0x10

    			,0x10
    			,0x00
    			,0x00
    			,0x00
    			,0x46
    			,0x53
    			,0x45
    			,0x20
    			,0x50
    			,0x44
    			,0x20
    			,0x41
    			,0x58
    			,0x49
    			,0x41
    			,0x4c

    			,0x20
    			,0x4f
    			,0x42
    			,0x4c
    			,0x08
    			,0x00
    			,0x50
    			,0x10
    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x20
    			,0x20
    			,0x20
    			,0x20

    			,0x20
    			,0x20
    			,0x08
    			,0x00
    			,0x70
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x45
    			,0x43
    			,0x08
    			,0x00};
    	
    	int len2 = 0x90;
    	
    	byte[] arr3 = {
    			0x10

    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x53
    			,0x49
    			,0x47
    			,0x4e
    			,0x41
    			,0x20
    			,0x10
    			,0x00
    			,0x10
    			,0x00
    			,0x0e
    			,0x00

    			,0x00
    			,0x00
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20
    			,0x20

    			,0x10
    			,0x00
    			,0x20
    			,0x00
    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x32
    			,0x33
    			,0x35
    			,0x36
    			,0x35
    			,0x10
    			,0x00

    			,0x30
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x10
    			,0x00
    			,0x40
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x46
    			,0x20

    			,0x10
    			,0x00
    			,0x10
    			,0x10
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x32
    			,0x38
    			,0x59
    			,0x10
    			,0x00
    			,0x30
    			,0x10

    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x36
    			,0x31
    			,0x2e
    			,0x32
    			,0x33
    			,0x35
    			,0x30
    			,0x20
    			,0x10
    			,0x00};
    	
    	int len3 = 0xb0;
    	
    	byte[] arr4 = {0x21

    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x18
    			,0x00
    			,0x20
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x53
    			,0x45
    			,0x18
    			,0x00

    			,0x21
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x53
    			,0x4b
    			,0x18
    			,0x00
    			,0x22
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00

    			,0x53
    			,0x50
    			,0x18
    			,0x00
    			,0x23
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x44
    			,0x18
    			,0x00
    			,0x24
    			,0x00

    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x66
    			,0x73
    			,0x65
    			,0x20
    			,0x18
    			,0x00
    			,0x50
    			,0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x35
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00}; 
    	
    	int len5 = 0x80;
    	
    	byte[] arr6 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x32
    			,0x33
    			,0x30
    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len4 = 0x81;
    	
    	byte[] arr5 = {
    			0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x32
    			,0x32
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len6 = 0x83;
    	
    	byte[] arr7 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x31
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len7 = 0x84;
    	
    	byte[] arr8 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x36
    			,0x33
    			,0x2e
    			,0x38
    			,0x36
    			,0x31
    			,0x35
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len8 = 0x86;
    	
    	byte[] arr9 = {0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00

    			,0x31
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len9 = 0x87;
    	
    	byte[] arr10 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x2e
    			,0x35
    			,0x30
    			,0x30
    			,0x30

    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len10 = 0x88;
    	
    	byte[] arr11 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30

    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len11 = 0x89;
    	
    	byte[] arr12 = {
    			0x00
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x35
    			,0x36
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len12 = 0x91;
    	
    	byte[] arr13 = {
    			0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x38
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len13 = 0x95;
    	
    	byte[] arr14 = {0x00
    			,0x08
    			,0x00
    			,0x00
    			,0x00

    			,0x33
    			,0x31
    			,0x2e
    			,0x32
    			,0x35
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00
    			,0x20
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00

    			,0x33
    			,0x20
    			,0x18
    			,0x00
    			,0x30
    			,0x10
    			,0x0e
    			,0x00
    			,0x00
    			,0x00
    			,0x43
    			,0x4c
    			,0x49
    			,0x4e
    			,0x49
    			,0x43

    			,0x41
    			,0x4c
    			,0x20
    			,0x42
    			,0x52
    			,0x41
    			,0x49
    			,0x4e
    			,0x18
    			,0x00};
    	
    	int len14 = 0x88;
    	
    	byte[] arr15 = {
    			0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00

    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len15 = 0x90;
    	
    	byte[] arr16 = {
    			0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x20
    			,0x18
    			,0x00};
    	
    	int len16 = 0x94;
    	
    	byte[] arr17 = {
    			0x10

    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x20
    			,0x18
    			,0x00
    			,0x00
    			,0x11
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x32

    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x20
    			,0x18
    			,0x00
    			,0x50
    			,0x12
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x48
    			,0x45

    			,0x41
    			,0x44
    			,0x18
    			,0x00
    			,0x10
    			,0x13
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x01
    			,0x00
    			,0x01

    			,0x00
    			,0x00
    			,0x18
    			,0x00
    			,0x12
    			,0x13
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x52
    			,0x4f
    			,0x57
    			,0x20
    			,0x18
    			,0x00

    			,0x14
    			,0x13
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x39
    			,0x30
    			,0x18
    			,0x00
    			,0x16
    			,0x13
    			,0x0a
    			,0x00
    			,0x00
    			,0x00

    			,0x30
    			,0x2e
    			,0x30
    			,0x33
    			,0x31
    			,0x33
    			,0x33
    			,0x30
    			,0x39
    			,0x20
    			,0x18
    			,0x00
    			,0x00
    			,0x51
    			,0x04
    			,0x00

    			,0x00
    			,0x00
    			,0x48
    			,0x46
    			,0x53
    			,0x20
    			,0x20
    			,0x00
    			,0x0d
    			,0x00
    			,0x24
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x2e

    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x20
    			,0x2e
    			,0x32
    			,0x2e
    			,0x38
    			,0x38
    			,0x31
    			,0x31
    			,0x2e
    			,0x32
    			,0x30
    			,0x30

    			,0x31
    			,0x30
    			,0x34
    			,0x31
    			,0x33
    			,0x31
    			,0x31
    			,0x35
    			,0x37
    			,0x35
    			,0x34
    			,0x2e
    			,0x31
    			,0x32
    			,0x34
    			,0x33

    			,0x32
    			,0x00
    			,0x20
    			,0x00
    			,0x0e
    			,0x00
    			,0x26
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e

    			,0x30
    			,0x2e
    			,0x33
    			,0x2e
    			,0x38
    			,0x38
    			,0x31
    			,0x31
    			,0x2e
    			,0x32
    			,0x2e
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30

    			,0x34
    			,0x31
    			,0x33
    			,0x31
    			,0x31
    			,0x31
    			,0x35
    			,0x37
    			,0x35
    			,0x34
    			,0x2e
    			,0x31
    			,0x32
    			,0x34
    			,0x33
    			,0x32
    			,0x00

    			,0x20
    			,0x00
    			,0x10
    			,0x00
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x38
    			,0x38
    			,0x31
    			,0x31
    			,0x20
    			,0x00
    			,0x11
    			,0x00

    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x32
    			,0x20
    			,0x20
    			,0x00
    			,0x12
    			,0x00
    			,0x06
    			,0x00
    			,0x00
    			,0x00
    			,0x33
    			,0x31

    			,0x37
    			,0x34
    			,0x34
    			,0x20
    			,0x20
    			,0x00
    			,0x13
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x20
    			,0x20
    			,0x00

    			,0x20
    			,0x00
    			,0x04
    			,0x00
    			,0x00
    			,0x00
    			,0x4c
    			,0x5c
    			,0x50
    			,0x48
    			,0x20
    			,0x00
    			,0x30
    			,0x00
    			,0x1a
    			,0x00

    			,0x00
    			,0x00
    			,0x2d
    			,0x31
    			,0x31
    			,0x30
    			,0x2e
    			,0x35
    			,0x30
    			,0x30
    			,0x5c
    			,0x2d
    			,0x37
    			,0x38
    			,0x2e
    			,0x33

    			,0x30
    			,0x36
    			,0x33
    			,0x5c
    			,0x2d
    			,0x37
    			,0x32
    			,0x2e
    			,0x37
    			,0x35
    			,0x37
    			,0x35
    			,0x20
    			,0x00
    			,0x32
    			,0x00

    			,0x1a
    			,0x00
    			,0x00
    			,0x00
    			,0x2d
    			,0x31
    			,0x31
    			,0x30
    			,0x2e
    			,0x35
    			,0x30
    			,0x30
    			,0x5c
    			,0x2d
    			,0x37
    			,0x38

    			,0x2e
    			,0x33
    			,0x30
    			,0x36
    			,0x33
    			,0x5c
    			,0x2d
    			,0x37
    			,0x32
    			,0x2e
    			,0x37
    			,0x35
    			,0x37
    			,0x35
    			,0x20
    			,0x00

    			,0x35
    			,0x00
    			,0x32
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e

    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e

    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e
    			,0x39
    			,0x39
    			,0x30
    			,0x39
    			,0x36
    			,0x30
    			,0x5c
    			,0x30

    			,0x2e
    			,0x31
    			,0x33
    			,0x34
    			,0x31
    			,0x35
    			,0x38
    			,0x20
    			,0x20
    			,0x00
    			,0x37
    			,0x00
    			,0x32
    			,0x00
    			,0x00
    			,0x00

    			,0x31
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c

    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x5c

    			,0x30
    			,0x2e
    			,0x39
    			,0x39
    			,0x30
    			,0x39
    			,0x36
    			,0x30
    			,0x5c
    			,0x30
    			,0x2e
    			,0x31
    			,0x33
    			,0x34
    			,0x31
    			,0x35

    			,0x38
    			,0x20
    			,0x20
    			,0x00
    			,0x52
    			,0x00
    			,0x36
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e
    			,0x30
    			,0x2e

    			,0x30
    			,0x2e
    			,0x34
    			,0x2e
    			,0x38
    			,0x38
    			,0x31
    			,0x31
    			,0x2e
    			,0x32
    			,0x2e
    			,0x32
    			,0x30
    			,0x30
    			,0x31
    			,0x30

    			,0x34
    			,0x31
    			,0x33
    			,0x31
    			,0x31
    			,0x35
    			,0x37
    			,0x35
    			,0x34
    			,0x2e
    			,0x31
    			,0x32
    			,0x34
    			,0x33
    			,0x32
    			,0x00

    			,0x20
    			,0x00
    			,0x02
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x20
    			,0x20
    			,0x00
    			,0x40
    			,0x10
    			,0x02
    			,0x00

    			,0x00
    			,0x00
    			,0x4e
    			,0x41
    			,0x20
    			,0x00
    			,0x41
    			,0x10
    			,0x08
    			,0x00
    			,0x00
    			,0x00
    			,0x2d
    			,0x35
    			,0x38
    			,0x2e

    			,0x30
    			,0x30
    			,0x30
    			,0x30
    			,0x28
    			,0x00
    			,0x02
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x01
    			,0x00
    			,0x28
    			,0x00

    			,0x04
    			,0x00
    			,0x0c
    			,0x00
    			,0x00
    			,0x00
    			,0x4d
    			,0x4f
    			,0x4e
    			,0x4f
    			,0x43
    			,0x48
    			,0x52
    			,0x4f
    			,0x4d
    			,0x45

    			,0x32
    			,0x20
    			,0x28
    			,0x00
    			,0x10
    			,0x00
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x01
    			,0x28
    			,0x00
    			,0x11
    			,0x00

    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x01
    			,0x28
    			,0x00
    			,0x30
    			,0x00
    			,0x12
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x2e

    			,0x38
    			,0x35
    			,0x39
    			,0x33
    			,0x37
    			,0x35
    			,0x5c
    			,0x30
    			,0x2e
    			,0x38
    			,0x35
    			,0x39
    			,0x33
    			,0x37
    			,0x35
    			,0x20

    			,0x28
    			,0x00
    			,0x00
    			,0x01
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x10
    			,0x00
    			,0x28
    			,0x00
    			,0x01
    			,0x01
    			,0x02
    			,0x00

    			,0x00
    			,0x00
    			,0x10
    			,0x00
    			,0x28
    			,0x00
    			,0x02
    			,0x01
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x0f
    			,0x00
    			,0x28
    			,0x00

    			,0x03
    			,0x01
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x01
    			,0x00
    			,0x28
    			,0x00
    			,0x06
    			,0x01
    			,0x02
    			,0x00
    			,0x00
    			,0x00

    			,0x00
    			,0x00
    			,0x28
    			,0x00
    			,0x07
    			,0x01
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x74
    			,0x03
    			,0x28
    			,0x00
    			,0x20
    			,0x01

    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x00
    			,0x28
    			,0x00
    			,0x50
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x20

    			,0x28
    			,0x00
    			,0x51
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x30
    			,0x20
    			,0x28
    			,0x00
    			,0x52
    			,0x10
    			,0x02
    			,0x00

    			,0x00
    			,0x00
    			,0x30
    			,0x20
    			,0x28
    			,0x00
    			,0x53
    			,0x10
    			,0x02
    			,0x00
    			,0x00
    			,0x00
    			,0x31
    			,0x20
    			,0x28
    			,0x00

    			,0x54
    			,0x10
    			,0x1c
    			,0x00
    			,0x00
    			,0x00
    			,0x53
    			,0x49
    			,0x47
    			,0x4e
    			,0x41
    			,0x4c
    			,0x20
    			,0x49
    			,0x4e
    			,0x54

    			,0x45
    			,0x4e
    			,0x53
    			,0x49
    			,0x54
    			,0x59
    			,0x20
    			,0x28
    			,0x55
    			,0x4e
    			,0x49
    			,0x54
    			,0x4c
    			,0x45
    			,0x53
    			,0x53

    			,0x29
    			,0x20};
    	
    	pl("TARGET P-DATA-TF");
    	
    	for (int a = 0; a < arr1.length; a ++)
    		
    		p("" + arr1[a]);
    	
    	p("" + len1);
    	
    	for (int a = 0; a < arr2.length; a ++)
    		
    		p("" + arr2[a]);
    	
    	pl("" + len2);

    	for (int a = 0; a < arr3.length; a ++)
    		
    		p("" + arr3[a]);
    	
    	pl("" + len3);
    	
    	for (int a = 0; a < arr4.length; a ++)
    		
    		p("" + arr4[a]);
    	
    	pl("" + len4);
    	
    	for (int a = 0; a < arr5.length; a ++)
    		
    		p("" + arr5[a]);
    	
    	pl("" + len5);
    	
    	for (int a = 0; a < arr6.length; a ++)
    		
    		p("" + arr6[a]);
    	
    	pl("" + len6);
    	
    	for (int a = 0; a < arr7.length; a ++)
    		
    		p("" + arr7[a]);
    	
    	pl("" + len7);
    	
    	for (int a = 0; a < arr8.length; a ++)
    		
    		p("" + arr8[a]);
    	
    	pl("" + len8);
    	
    	for (int a = 0; a < arr9.length; a ++)
    		
    		p("" + arr9[a]);
    	
    	pl("" + len9);
    	
    	for (int a = 0; a < arr10.length; a ++)
    		
    		p("" + arr10[a]);
    	
    	pl("" + len10);
    	
    	for (int a = 0; a < arr11.length; a ++)
    		
    		p("" + arr11[a]);
    	
    	pl("" + len11);
    	
    	for (int a = 0; a < arr12.length; a ++)
    		
    		p("" + arr12[a]);
    	
    	pl("" + len12);
    	
    	for (int a = 0; a < arr13.length; a ++)
    		
    		p("" + arr13[a]);
    	
    	pl("" + len13);
    	
    	for (int a = 0; a < arr14.length; a ++)
    		
    		p("" + arr14[a]);
    	
    	pl("" + len14);
    	
    	for (int a = 0; a < arr15.length; a ++)
    		
    		p("" + arr15[a]);
    	
    	pl("" + len15);
    	
    	for (int a = 0; a < arr16.length; a ++)
    		
    		p("" + arr16[a]);
    	
    	pl("" + len16);
    	
    	for (int a = 0; a < arr17.length; a ++)
    		
    		p("" + arr17[a]);
    	
    	byte type;
    	
    	try {

    		type = 0x04;
    		
    		this.imagePacket = new ImagePacket();
    		this.imagePacket.setPduType(type);
    		
    		DataElement dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0008);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(20);
    		dataElement.setElementData("ORIGINAL\\PRIMARY\\MPR");
    		this.imagePacket.setImageType(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0016);
    		dataElement.setValueRepresentation("UI");
    		dataElement.setElementLength(26);
    		dataElement.setElementData("1.2.840.10008.5.1.4.1.1.4");
    		this.imagePacket.setSopClassUID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0018);
    		dataElement.setValueRepresentation("UI");
    		dataElement.setElementLength(40);
    		dataElement.setElementData("0.0.0.0.1.8811.2.1.20010413115754.12432");
    		this.imagePacket.setSopInstanceUID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0020);
    		dataElement.setValueRepresentation("DA");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("20010316");
    		this.imagePacket.setStudyDate(dataElement);

    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0021);
    		dataElement.setValueRepresentation("DA");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("20010316");
    		this.imagePacket.setSeriesDate(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0022);
    		dataElement.setValueRepresentation("DA");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("20010316");
    		this.imagePacket.setAcquisitionDate(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0023);
    		dataElement.setValueRepresentation("DA");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("20010323");
    		this.imagePacket.setContentDate(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0030);
    		dataElement.setValueRepresentation("TM");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("143008");
    		this.imagePacket.setStudyTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0031);
    		dataElement.setValueRepresentation("TM");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("143414");
    		this.imagePacket.setSeriesTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0032);
    		dataElement.setValueRepresentation("TM");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("143415");
    		this.imagePacket.setAcquisitionTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0033);
    		dataElement.setValueRepresentation("TM");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("143008");
    		this.imagePacket.setContentTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0050);
    		dataElement.setValueRepresentation("TM");
    		dataElement.setElementLength(0);
    		dataElement.setElementData(null);
    		this.imagePacket.setAccesstionNumber(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0060);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("MR");
    		this.imagePacket.setModality(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0070);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(18);
    		dataElement.setElementData("GE MEDICAL SYSTEMS");
    		this.imagePacket.setManufacturer(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0080);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(28);
    		dataElement.setElementData("                    ");
    		this.imagePacket.setInstitutionName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x0090);
    		dataElement.setValueRepresentation("PN");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("    ");
    		this.imagePacket.setReferringPhysiciansName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x1010);
    		dataElement.setValueRepresentation("SH");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("MRS1");
    		this.imagePacket.setStationName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x1030);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("BRAIN");
    		this.imagePacket.setStudyDescription(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x103e);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(16);
    		dataElement.setElementData("FSE PD AXIAL OBL");
    		this.imagePacket.setSeriesDescription(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x1050);
    		dataElement.setValueRepresentation("PN");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("      ");
    		this.imagePacket.setPerformingPhysiciansName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x1070);
    		dataElement.setValueRepresentation("PN");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("EC");
    		this.imagePacket.setOperatorsName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0008);
    		dataElement.setElementNumber(0x1090);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("SIGNA ");
    		this.imagePacket.setManufacturersModelName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x0010);
    		dataElement.setValueRepresentation("PN");
    		dataElement.setElementLength(14);
    		dataElement.setElementData("              ");
    		this.imagePacket.setPatientsName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x0020);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("123565");
    		this.imagePacket.setPatientID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x0030);
    		dataElement.setValueRepresentation("DA");
    		dataElement.setElementLength(0);
    		dataElement.setElementData(null);
    		this.imagePacket.setPatientsBirthDate(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x0040);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("F ");
    		this.imagePacket.setPatientsSex(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x1010);
    		dataElement.setValueRepresentation("AS");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("028Y");
    		this.imagePacket.setPatientsAge(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x1030);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("61.2350 ");
    		this.imagePacket.setPatientsSex(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0010);
    		dataElement.setElementNumber(0x21b0);
    		dataElement.setValueRepresentation("LT");
    		dataElement.setElementLength(0);
    		dataElement.setElementData(null);
    		this.imagePacket.setAdditionalPatientHistory(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0020);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("SE");
    		this.imagePacket.setScanningSequence(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0021);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("SK");
    		this.imagePacket.setSequenceVariant(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0022);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("SP");
    		this.imagePacket.setScanOptions(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0023);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("2D");
    		this.imagePacket.setMrAcquisitionType(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0024);
    		dataElement.setValueRepresentation("SH");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("fse ");
    		this.imagePacket.setSequenceName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0050);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("5.00000 ");
    		this.imagePacket.setSliceThickness(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0080);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("2300.00 ");
    		this.imagePacket.setRepetitionTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0081);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("2200.00 ");
    		this.imagePacket.setEchoTime(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0083);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("1.00000 ");
    		this.imagePacket.setNumberOfAverages(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0084);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("63.8615 ");
    		this.imagePacket.setImagingFrequency(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0086);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("1 ");
    		this.imagePacket.setEchoNumber(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0087);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("1.50000 ");
    		this.imagePacket.setMagneticFieldStrength(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0088);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("2.00000 ");
    		this.imagePacket.setSpacingBetweenSlices(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0089);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("256 ");
    		this.imagePacket.setNumberOfPhaseEncodingSteps(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0091);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("8 ");
    		this.imagePacket.setEchoTrainLength(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x0095);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("31.2500 ");
    		this.imagePacket.setPixelBandwidth(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1020);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("3 ");
    		this.imagePacket.setSoftwareVersion(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1030);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(14);
    		dataElement.setElementData("CLINICAL BRAIN");
    		this.imagePacket.setProtocolName(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1088);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setHeartRate(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1090);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setCardiacNumberOfImages(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1094);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setTriggerWindow(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1100);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("220.000 ");
    		this.imagePacket.setReconstructionDiameter(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1250);
    		dataElement.setValueRepresentation("SH");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("HEAD");
    		this.imagePacket.setReceiveCoilName(dataElement);
    		
    		/*POTENTIAL ISSUE HERE - VALUE IS 0 BUT LENGTH IS 8*/
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1310);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(8);
    		dataElement.setIntElementData(0);
    		this.imagePacket.setAcquisitionMatrix(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1312);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("ROW ");
    		this.imagePacket.setInPhaseEncodingDirection(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1314);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("90");
    		this.imagePacket.setFlipAngle(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x1316);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(10);
    		dataElement.setElementData("0.0313309 ");
    		this.imagePacket.setSAR(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0018);
    		dataElement.setElementNumber(0x5100);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("HFS ");
    		this.imagePacket.setPatientPosition(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x000d);
    		dataElement.setValueRepresentation("UI");
    		dataElement.setElementLength(36);
    		dataElement.setElementData("0.0.0.0.2.8811.20010413115754.12432");
    		this.imagePacket.setStudyInstanceUID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x000e);
    		dataElement.setValueRepresentation("UI");
    		dataElement.setElementLength(38);
    		dataElement.setElementData("0.0.0.0.2.8811.20010413115754.12432");
    		this.imagePacket.setSeriesInstanceUID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0010);
    		dataElement.setValueRepresentation("SH");
    		dataElement.setElementLength(4);
    		dataElement.setIntElementData(8811);
    		this.imagePacket.setStudyID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0011);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("2 ");
    		this.imagePacket.setSeriesNumber(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0012);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(6);
    		dataElement.setElementData("31744 ");
    		this.imagePacket.setAcquisitionNumber(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0013);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("1 ");
    		this.imagePacket.setInstanceNumber(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0020);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(4);
    		dataElement.setElementData("L\\PH");
    		this.imagePacket.setPatientOrientation(dataElement);

    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0030);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(26);
    		dataElement.setElementData("-110.500\\-78.3063\\-72.7575");
    		this.imagePacket.setImagePosition(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0032);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(26);
    		dataElement.setElementData("-110.500\\-78.3063\\-72.7575");
    		this.imagePacket.setImagePositionPatient(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0035);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(50);
    		dataElement.setElementData("1.00000\\0.00000\\0.00000\\0.00000\\0.990960\\0.134158 ");
    		this.imagePacket.setImageOrientation(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0037);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(50);
    		dataElement.setElementData("1.00000\\0.00000\\0.00000\\0.00000\\0.990960\\0.134158 ");
    		this.imagePacket.setImageOrientationPatient(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x0052);
    		dataElement.setValueRepresentation("UI");
    		dataElement.setElementLength(38);
    		dataElement.setElementData("0.0.0.0.4.8811.2.20010413115754.12432 ");
    		this.imagePacket.setFrameOfReferenceUID(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x1002);
    		dataElement.setValueRepresentation("IS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("1 ");
    		this.imagePacket.setImagesInAcquisition(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x1040);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("NA");
    		this.imagePacket.setPositionReferenceIndicator(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0020);
    		dataElement.setElementNumber(0x1041);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(8);
    		dataElement.setElementData("-58.0000");
    		this.imagePacket.setSliceLocator(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0002);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(1);
    		this.imagePacket.setSamplesPerPixel(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0004);
    		dataElement.setValueRepresentation("CS");
    		dataElement.setElementLength(12);
    		dataElement.setElementData("MONOCHROME2 ");
    		this.imagePacket.setPhotometricInterpretation(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0010);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(256);
    		this.imagePacket.setRows(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0011);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(256);
    		this.imagePacket.setColumns(dataElement);
  
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0030);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(18);
    		dataElement.setElementData("0.859375\\0.859375 ");
    		this.imagePacket.setPixelSpacing(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0100);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(16);
    		this.imagePacket.setBitsAllocated(dataElement);
  
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0101);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(16);
    		this.imagePacket.setBitsStored(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0102);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(15);
    		this.imagePacket.setHighBit(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0103);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(1);
    		this.imagePacket.setPixelRepresentation(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0106);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(0);
    		this.imagePacket.setSmallestImagePixelValue(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0107);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(884);
    		this.imagePacket.setLargestImagePixelValue(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0120);
    		dataElement.setValueRepresentation("US");
    		dataElement.setElementLength(2);
    		dataElement.setIntElementData(0);
    		this.imagePacket.setPixelPaddingValue(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0150);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setWindowCenter(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0151);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setWindowWidth(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0152);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("0 ");
    		this.imagePacket.setRescaleIntercept(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0153);
    		dataElement.setValueRepresentation("DS");
    		dataElement.setElementLength(2);
    		dataElement.setElementData("1 ");
    		this.imagePacket.setRescaleSlope(dataElement);
    		
    		dataElement = new DataElement();
    		dataElement.setGroupNumber(0x0028);
    		dataElement.setElementNumber(0x0154);
    		dataElement.setValueRepresentation("LO");
    		dataElement.setElementLength(28);
    		dataElement.setElementData("SIGNAL INTENSITY (UNITLESS)" );
    		this.imagePacket.setRescaleType(dataElement);
    		
    		
    		
    		
    		
    		
    		
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
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
    	
    	byte[] arr1 = {0x04,
    			0x00,
    			0x00,
    			0x00,

    			0x00};
    	
    	int len1 = 0x8c;
    	
    	byte[] arr2 = {
    			0x00,
    			0x00,
    			0x00};
    	
    	int len2 = 0x88;
    	
    	byte[] arr3 = {
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

    			0x7a,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x02,
    			0x00,
    			0x1a,
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
    			0x35,
    			0x2e,
    			0x31,
    			0x2e,
    			0x34,
    			0x2e,

    			0x31,
    			0x2e,
    			0x31,
    			0x2e,
    			0x34,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x01,
    			0x02,
    			0x00,
    			0x00,
    			0x00,
    			0x01,
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
    			0x07,
    			0x02,
    			0x00,

    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x00,
    			0x08,
    			0x02,
    			0x00,
    			0x00,
    			0x00,
    			0x02,
    			0x01,
    			0x00,
    			0x00,

    			0x00,
    			0x10,
    			0x28,
    			0x00,
    			0x00,
    			0x00,
    			0x30,
    			0x2e,
    			0x30,
    			0x2e,
    			0x30,
    			0x2e,
    			0x30,
    			0x2e,
    			0x31,
    			0x2e,

    			0x38,
    			0x38,
    			0x31,
    			0x31,
    			0x2e,
    			0x32,
    			0x2e,
    			0x31,
    			0x2e,
    			0x32,
    			0x30,
    			0x30,
    			0x31,
    			0x30,
    			0x34,
    			0x31,

    			0x33,
    			0x31,
    			0x31,
    			0x35,
    			0x37,
    			0x35,
    			0x34,
    			0x2e,
    			0x31,
    			0x32,
    			0x34,
    			0x33,
    			0x32,
    			0x00};
    	
    	pl("TARGET C-STORE-RQ");
    	
    	for (int a = 0; a < arr1.length; a ++) 
    		
    		p("" + arr1[a]);
    	
    	p("" + len1);
    	
    	for (int a = 0; a < arr2.length; a ++) 
    		
    		p("" + arr2[a]);
    	
    	p("" + len2);
    	
    	for (int a = 0; a < arr3.length; a ++) 
    		
    		p("" + arr3[a]);
    	
    	boolean retval = false;
    	
    	try {
    		
    		byte type;
    		int pcID;
    		int header;
    		int limit;
    		int size;
    		int packets;
    		int count;
    		PresentationDataValue pdValue;
			ArrayList<PresentationDataValue> pdValueItems;
			PresentationContext_RQ presentationContext;
			ImagePacket imagePacket;
			byte[] arr;
			byte[] temp;
			
    		switch (n) {
    		
	    		case 1 : //C-ECHO-RQ
	    			
	    			type = 0x04;
	        		
	        		presentationContext = this.associateRequestRQ.getPresentationContexts().get(0);
	        		pcID = presentationContext.getPresentationContextID();
	        		header = 0x03;
	        		
	        		this.dataTF = new P_DATA_TF();
	        		this.dataTF.setPduType(type);
	        		
	        		if (this.buildEchoRequest()) {
	        		
	        			pdValue = new PresentationDataValue(header, pcID, this.echoRequest, "C-ECHO");
	        			pdValueItems = new ArrayList<PresentationDataValue>();
	        			pdValueItems.add(pdValue);
	        			this.dataTF.setPresentationDataValueItems(pdValueItems);
	        			this.dataTF.writeToBuffer();
	        			retval = true;
	        			
	        		}
	        		
	        		else {
	        			
	        			//Problem building Echo Request
	        			pl("C-ECHO-RQ was not built successfully");
	        			retval = false;
	        			
	        		}

	    			
	    			break;
	    			
	    		case 2 : //C-STORE-RQ 
	    			
	    			type = 0x04;
	    			
	    			header = 0x03;
	        		
	        		this.dataTF = new P_DATA_TF();
	        		this.dataTF.setPduType(type);
	    			
	        		if (this.buildStoreRequest()) {
	        			
	        			presentationContext = this.associateRequestRQ.getPresentationContexts().get(0);
		        		pcID = presentationContext.getPresentationContextID();
	        			pdValue = new PresentationDataValue(header, pcID, this.storeRequest, "C-STORE");
	        			pdValueItems = new ArrayList<PresentationDataValue>();
	        			pdValueItems.add(pdValue);
	        			this.dataTF.setPresentationDataValueItems(pdValueItems);
	        			this.dataTF.writeToBuffer();
	        			retval = true;
	        		
	        			pl();
	        			pl("CURRENT C-STORE-RQ");
	        			byte[] arr5 = this.dataTF.getBuffer().toByteArray();
	        	    	
	        	    	for (int a = 0; a < 5; a ++) 
	        	    		
	        	    		p("" + arr5[a]);
	        	    	
	        	    	p("" + this.dataTF.getPduLength());
	        			
	        	    	for (int a = 6; a < 9; a ++) 
	        	    		
	        	    		p("" + arr5[a]);
	        	    	
	        	    	p("" + this.dataTF.getPresentationDataValueItems().get(0).getItemLength());
	        	    
	        	    	for (int a = 10; a < arr5.length; a ++) 
	        	    		
	        	    		p("" + arr5[a]);
	        	    	
	        	    	System.exit(0);
	        	    	
	        		}
	        		
	        		else {
	        			
	        			//Problem building Store Request
	        			pl("C-STORE-RQ was not built successfully");
	        			retval = false;
	        			
	        		}
	        		
	    			break; 
	    			
	    		case 3 : //PDV Image Data
	    			
	    			type = 0x04;
	    			
	    			presentationContext = this.associateRequestRQ.getPresentationContexts().get(0);
	    			pcID = presentationContext.getPresentationContextID();
	        		header = 0x00;
	    			limit = this.associateRequestRQ.getUserInformation().getMaximumLengthSubItem().getMaxPDULengthReceive() - 6;

	    			if (this.filePath != null) {
	    				
	    				//Get the full byte array 
	    				this.imageGenerator.setFilePath(this.filePath);
	    				this.imageGenerator.readFile();
	    				this.imageGenerator.writeToStream();
	    				size = this.imageGenerator.getStream().size();
	    				//pl("Size of image data: " + size);
	    				arr = this.imageGenerator.getStream().toByteArray();
	    				//pl("Array length: " + arr.length);
	    				
	    				if (limit > 0) packets = size / limit;
	    				else packets = 0;
	    				
	    				//pl("Limit: " + limit);
	    				//pl("Packet numbers: " + packets);
	    				
	    				if (packets > 0 && size > 0) {
	    					
	    					this.imagePackets = new ArrayList<P_DATA_TF>();
	    					
	    					count = 0;
	    					
	    					for (int a = 0; a < packets; a ++) {
	    					
	    						//pl("Round: " + a);
	    						//pl("Count: " + count);
	    						this.dataTF = new P_DATA_TF();
	    						this.dataTF.setPduType(type);
		    					pdValueItems = new ArrayList<PresentationDataValue>();
		    					imagePacket = new ImagePacket();
	    						temp = Arrays.copyOfRange(arr, count, count + limit); 
	    						imagePacket.setPacketData(temp);
	    						pdValue = new PresentationDataValue(header, pcID, imagePacket, "IMAGE_PACKET");
	    						pdValueItems.add(pdValue);
	    						this.dataTF.setPresentationDataValueItems(pdValueItems);
	    						this.dataTF.writeToBuffer();
	    						this.imagePackets.add(this.dataTF);
	    						pl("Temp size: " + temp.length);
	    						count += limit;
	    						//pl("Count: " + count);
	    						
	    					}
	    					
	    					if (size - count > 0) {
	    						
	    						header = 0x02;
	    						//pl("Remaining: " + (size - count));
	    						this.dataTF = new P_DATA_TF();
	    						this.dataTF.setPduType(type);
		    					pdValueItems = new ArrayList<PresentationDataValue>();
		    					imagePacket = new ImagePacket();
	    						temp = Arrays.copyOfRange(arr, count, arr.length);
	    						imagePacket.setPacketData(temp);
	    						pdValue = new PresentationDataValue(header, pcID, imagePacket, "IMAGE_PACKET");
	    						pdValueItems.add(pdValue);
	    						this.dataTF.setPresentationDataValueItems(pdValueItems);
	    						this.dataTF.writeToBuffer();
	    						this.imagePackets.add(this.dataTF);
	    						//pl("Temp size: " + temp.length);
	    						retval = true;
	    						
	    					}
	    					
	    					//pl("Number of packets to send: " + this.imagePackets.size());
	    					
	    				}
	    				
	    				else {
	    					
	    					pl("Could not calculate the packet amount");
		    				System.exit(0);
	    					
	    				}
	    				
	    			} 
	    			
	    			else {
	    			
	    				pl("No file path is specified");
	    				System.exit(0);
	    				
	    			}
	    			
	    			break;
    		
	    		case 4 : //C-FIND
	    			
	    			break;
	    			
	    		case 5 : //C-MOVE
	    			
	    			break;
	    			
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
    
    public boolean sendImagePackets() {
    	
    	boolean retval = false;
    	
    	try {
        	
    		for (P_DATA_TF imagePacket : this.imagePackets) {
    			
    			this.client.write(imagePacket.getBuffer().toByteArray());
    			
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
	
	public DicomImageGenerator getImageGenerator() {
		return imageGenerator;
	}

	public void setImageGenerator(DicomImageGenerator imageGenerator) {
		this.imageGenerator = imageGenerator;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<String> getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(List<String> filePaths) {
		this.filePaths = filePaths;
	}

	private void p(String s) { System.out.print(s); }
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }

}
