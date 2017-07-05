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

public class Client extends DicomByteOrderable {

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
    
    public Client(String ipAddress, int port) {
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
    
    /*public boolean receive() {
    	
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
    		
    		}
            
    	} 
    	
        catch (Exception e) {   
        	
        	retval = false;
        	pl(e.getMessage());
        	e.printStackTrace();
            return retval;
            
        }
    
    	return retval;
    	
    }*/

    public void writeByte(byte b) {
    	
    	try { 
    	
    		this.dataOutputStream.writeByte(b);
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void writeByte(int i) {
    	
    	try { 
        	
    		this.dataOutputStream.writeByte(i);
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void writeInt(int i) {
    	
    	try { 
        	
    		this.dataOutputStream.writeInt(i);
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void write(byte[] b) {
    	
    	try { 
        
    		this.dataOutputStream.write(b);
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
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
    
	public void writeUInt32(int value) throws IOException {
		
		int b0 = value & 0x000000ff; //masking out 24 bits
		int b1 = (value & 0x0000ff00) >> 8;
		int b2 = (value & 0x00ff0000) >> 16;
		int b3 = (value & 0xff000000) >> 24;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			this.dataOutputStream.write(b0);
			this.dataOutputStream.write(b1);
			this.dataOutputStream.write(b2);
			this.dataOutputStream.write(b3);
			
		}
		
		else {
			
			this.dataOutputStream.write(b3);
			this.dataOutputStream.write(b2);
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
	
	public void flush() {
		
		try { 
	        
    		this.dataOutputStream.flush();
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
		
	}
    	
    public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}

	public void setDataOutputStream(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	public DataInputStream getDataInputStream() {
		return dataInputStream;
	}

	public void setDataInputStream(DataInputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
	}

	public DicomValueRepresentationInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(DicomValueRepresentationInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public DicomValueRepresentationOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(DicomValueRepresentationOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public ByteArrayOutputStream getByteOutputStream() {
		return byteOutputStream;
	}

	public void setByteOutputStream(ByteArrayOutputStream byteOutputStream) {
		this.byteOutputStream = byteOutputStream;
	}

	public ByteArrayInputStream getByteInputStream() {
		return byteInputStream;
	}

	public void setByteInputStream(ByteArrayInputStream byteInputStream) {
		this.byteInputStream = byteInputStream;
	}

	public ObjectInputStream getObjectInputStream() {
		return objectInputStream;
	}

	public void setObjectInputStream(ObjectInputStream objectInputStream) {
		this.objectInputStream = objectInputStream;
	}

	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}

	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		this.objectOutputStream = objectOutputStream;
	}

	public C_ECHO_RQ getEchoRequest() {
		return echoRequest;
	}

	public void setEchoRequest(C_ECHO_RQ echoRequest) {
		this.echoRequest = echoRequest;
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

	private void pl(String s) { System.out.println(s); }
    
}
