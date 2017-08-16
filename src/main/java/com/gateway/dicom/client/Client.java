package com.gateway.dicom.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;

import com.gateway.dicom.protocols.A_ASSOCIATE_AC;
import com.gateway.dicom.protocols.A_ASSOCIATE_RJ;
import com.gateway.dicom.protocols.A_ASSOCIATE_RQ;
import com.gateway.dicom.protocols.C_ECHO_RQ;
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
    private int skip;
    
    public Client(String ipAddress, int port) {
    	this.ipAddress = ipAddress;
    	this.port = port;
    	this.byteOrdering = BYTE_ORDERING_BIG_ENDIAN;
    	this.skip = 0;
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
    		pl("AVAILABLE: " + size);
    		
    		if (size > 0) {
    		
    			pl("Data received from PACS");
    			retval = true;
	        	arr = new byte[size];
	        	
	        	//if (this.skip > 0) this.dataInputStream.skip(this.skip);
	        	
	        	this.dataInputStream.readFully(arr);
	        	
	        	for (int a = 0; a < arr.length; a ++) 
	        		
	        		pl("arr[" + a + "]: " + arr[a]);
	        	
	        	this.receivedData = arr;
	        
    		}
    		
    		else {
    			
    			pl("No data received from PACS");
    			
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

    public void writeByte(byte b) {
    	
    	try { 
    	
    		//pl("" + b);
    		this.dataOutputStream.writeByte(b);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }

    public void writeByte(byte b, DataOutputStream dos) {
    	
    	try { 
    	
    		//pl("" + b);
    		dos.writeByte(b);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }
    
    public void writeByte(int i) {
    	
    	try { 
        	
    		//pl("" + i);
    		this.dataOutputStream.writeByte(i);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void writeByte(int i, DataOutputStream dos) {
    	
    	try { 
        	
    		//pl("" + i);
    		dos.writeByte(i);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	
    }

    public void writeInt(int i) {
    	
    	try { 
        	
    		//pl("" + i);
    		this.dataOutputStream.writeInt(i);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }

    public void writeInt(int i, DataOutputStream dos) {
    	
    	try { 
        	
    		//pl("" + i);
    		dos.writeInt(i);
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }
    
    public void write(byte[] b) {
    	
    	try { 
        
    		//p("Writing Bytes:");
    		
    		/*for (int i = 0; i < b.length ; i ++) 
    			
    			p(b[i] + " ");
			*/
    		
    		this.dataOutputStream.write(b);
    		//pl("");
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }

    public void write(byte[] b, DataOutputStream dos) {
    	
    	try { 
        
    		//p("Writing Bytes:");
    		
    		/*for (int i = 0; i < b.length ; i ++) 
    			
    			p(b[i] + " ");
			*/
    		
    		dos.write(b);
    		//pl("");
    		//pl("Size: " + this.dataOutputStream.size());
    		
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
    }

    public void writeUInt16(int value) throws IOException {
    	
		int b0 = value & 0x00ff;
		int b1 = (value & 0xff00) >> 8;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			//pl("" + b0);
			this.dataOutputStream.write(b0);
			//pl("" + b1);
			this.dataOutputStream.write(b1);
			
		}
		
		else {
		
			//pl("" + b1);
			this.dataOutputStream.write(b1);
			//pl("" + b0);
			this.dataOutputStream.write(b0);
			
		}
		
		//pl("Size: " + this.dataOutputStream.size());
		
	}
    
    public void writeUInt16(int value, DataOutputStream dos) throws IOException {
    	
		int b0 = value & 0x00ff;
		int b1 = (value & 0xff00) >> 8;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			//pl("" + b0);
			dos.write(b0);
			//pl("" + b1);
			dos.write(b1);
			
		}
		
		else {
		
			//pl("" + b1);
			dos.write(b1);
			//pl("" + b0);
			dos.write(b0);
			
		}
		
		//pl("Size: " + this.dataOutputStream.size());
		
	}
    
	public void writeUInt32(int value) throws IOException {
		
		int b0 = value & 0x000000ff; //masking out 24 bits
		int b1 = (value & 0x0000ff00) >> 8;
		int b2 = (value & 0x00ff0000) >> 16;
		int b3 = (value & 0xff000000) >> 24;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			//pl("" + b0);
			this.dataOutputStream.write(b0);
			//pl("" + b1);
			this.dataOutputStream.write(b1);
			//pl("" + b2);
			this.dataOutputStream.write(b2);
			//pl("" + b3);
			this.dataOutputStream.write(b3);
			
		}
		
		else {
			
			//pl("" + b3);
			this.dataOutputStream.write(b3);
			//pl("" + b2);
			this.dataOutputStream.write(b2);
			//pl("" + b1);
			this.dataOutputStream.write(b1);
			//pl("" + b0);
			this.dataOutputStream.write(b0);
			
		}
		
		//pl("Size: " + this.dataOutputStream.size());
		
	}
	
	public void writeUInt32(int value, DataOutputStream dos) throws IOException {
		
		int b0 = value & 0x000000ff; //masking out 24 bits
		int b1 = (value & 0x0000ff00) >> 8;
		int b2 = (value & 0x00ff0000) >> 16;
		int b3 = (value & 0xff000000) >> 24;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			
			//pl("" + b0);
			dos.write(b0);
			//pl("" + b1);
			dos.write(b1);
			//pl("" + b2);
			dos.write(b2);
			//pl("" + b3);
			dos.write(b3);
			
		}
		
		else {
			
			//pl("" + b3);
			dos.write(b3);
			//pl("" + b2);
			dos.write(b2);
			//pl("" + b1);
			dos.write(b1);
			//pl("" + b0);
			dos.write(b0);
			
		}
		
		//pl("Size: " + this.dataOutputStream.size());
		
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
	
    public String readString(int length, DataInputStream dis) throws IOException {
		
    	byte[] byteArray = readByteArray(length, dis);
		
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
	
	public byte[] readByteArray(int length, DataInputStream dis) throws IOException {
		
		byte[] buffer = new byte[length];
		
		int totalBytesRead = 0;
		
		while(totalBytesRead != length) {
		
			totalBytesRead += dis.read(buffer, 
					totalBytesRead, 
					length - totalBytesRead);
		
		}	
		
		return buffer;
		
	}
	
	public byte[] readByteArray(int length, int start) throws IOException {
		
		byte[] buffer = new byte[length];
		
		int totalBytesRead = start;
		
		if (length > start) {
		
			while(totalBytesRead != length) {
			
				totalBytesRead += this.dataInputStream.read(buffer, 
						totalBytesRead, 
						length - totalBytesRead);
			
			}	
		
		}
		
		return buffer;
		
	}
	
	public byte[] readByteArray(int length, int start, DataInputStream dis) throws IOException {
		
		byte[] buffer = new byte[length];
		
		int totalBytesRead = start;
		
		if (length > start) {
		
			while(totalBytesRead != length) {
			
				totalBytesRead += dis.read(buffer, 
						totalBytesRead, 
						length - totalBytesRead);
			
			}	
		
		}
		
		return buffer;
		
	}
	
	public byte readByte() throws IOException {
		
		return this.dataInputStream.readByte();
		
	}
	
	public int readInt() throws IOException {
		
		return this.dataInputStream.readInt();
		
	}
	
	public void skip(long l) throws IOException {
		
		this.dataInputStream.skip(l);
		
	}
	
	public void skipBytes(int n) throws IOException {
		
		this.dataInputStream.skipBytes(n);
		
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

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	private void pl(String s) { System.out.println(s); }
	
	private void p(String s) { System.out.print(s); }
    
}
