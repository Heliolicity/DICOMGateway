package com.gateway.dicom.lib;

/*
 * Author: Robert Sadleir
 * 
 * */

import java.io.IOException;
import java.io.InputStream;

public class DicomPrimitiveInputStream extends DicomByteOrderable {

	private InputStream inputStream;
	
	public DicomPrimitiveInputStream(InputStream inputStream, int byteOrdering) {
		super(byteOrdering);
		this.inputStream = inputStream;
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
}
