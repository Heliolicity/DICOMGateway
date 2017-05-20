package com.gateway.dicom.lib;

/*
 * Author: Robert Sadleir
 * 
 * */

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class DicomPrimitiveInputStream extends DicomByteOrderable {

	//private InputStream inputStream;
	private InputStream is;
	private DataInputStream inputStream;
	private byte[] buffer;
	
	public DicomPrimitiveInputStream(InputStream inputStream, int byteOrdering) {
		super(byteOrdering);
		this.is = inputStream;
		this.inputStream = new DataInputStream(inputStream);
	}
	
	public DataInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(DataInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public void skip(int numberOfBytes) throws IOException {
		this.inputStream.skip(numberOfBytes);
	}
	
	public String readVR() throws IOException {
		return readString(2);
	}
	
	public int readUInt16() throws IOException {
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			int b0 = this.inputStream.read();
			int b1 = this.inputStream.read();
			return (b1<<8) | b0;
		} else {
			int b1 = this.inputStream.read();
			int b0 = this.inputStream.read();
			return (b1<<8) | b0;
		}
	}
	
	public int readUInt32() throws IOException {
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			int b0 = this.inputStream.read();
			int b1 = this.inputStream.read();
			int b2 = this.inputStream.read();
			int b3 = this.inputStream.read();
			return (b3<<24) | (b2<<16) | (b1<<8) | b0;
		} else {
			int b3 = this.inputStream.read();
			int b2 = this.inputStream.read();
			int b1 = this.inputStream.read();
			int b0 = this.inputStream.read();
			return (b3<<24) | (b2<<16) | (b1<<8) | b0;
		}
	}
	
	/*public String readString(int length) throws IOException {
		return new String(readByteArray(length));
	}*/
	
	public String readString(int length) throws IOException {
		byte[] byteArray = readByteArray(length);
		if(byteArray[byteArray.length-1] == 0x00) {
			return new String(byteArray, 0, byteArray.length-1);
		}
		else {
			return new String(byteArray);
		}	
	}
	
	public byte[] readByteArray(int length) throws IOException {
		byte[] buffer = new byte[length];
		int totalBytesRead = 0;
		while(totalBytesRead != length) {
			totalBytesRead += this.inputStream.read(buffer, 
					totalBytesRead, 
					length - totalBytesRead);
		}	
		return buffer;
	}
	
	public boolean hasMoreData() throws IOException {
		if(this.inputStream.available() == 0)
			return false;
		else 
			return true;
	}
	
	public String readLine() throws IOException { return this.inputStream.readLine(); }
	
	public String readUTF() throws IOException { return this.inputStream.readUTF(); }
	
	public int read() { 
		
		int i = 0;
		
		try {
		
			pl("HERE A1");
			pl("HERE A2");
			
			this.buffer = new byte[is.available()];
			
			pl("BUFFER LENGTH BEFORE: " + this.buffer.length);
			pl("HERE A3");
			i = this.inputStream.read(this.buffer);
			pl("i: " + i);
			//return this.inputStream.read(this.buffer); 
			pl("BUFFER LENGTH AFTER: " + this.buffer.length);
		
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
		return i;
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
