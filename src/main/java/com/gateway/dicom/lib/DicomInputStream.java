package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

import java.io.IOException;
import java.io.InputStream;

public class DicomInputStream extends DicomByteOrder {
	
	protected InputStream inputStream;

	public DicomInputStream(InputStream inputStream, int byteOrdering) {
		super(byteOrdering);
		this.inputStream = inputStream;
	}
	
	public int read() throws IOException {
		return inputStream.read();
	}
	
	public int readUInt8() throws IOException {
		return inputStream.read();
	}
	
	public int readUInt16() throws IOException {
		return readUInt16(getByteOrdering());
	}
	
	public int readUInt16(int byteOrder) throws IOException {
		if(byteOrder == BYTE_ORDERING_LITTLE_ENDIAN) {
			int b0 = inputStream.read();
			int b1 = inputStream.read();
			return (b1<<8) | b0;
		} else {
			int b1 = inputStream.read();
			int b0 = inputStream.read();
			return (b1<<8) | b0;
		}
	}
	
	public int readUInt32() throws IOException {
		return readUInt32(getByteOrdering());
	}
	
	public int readUInt32(int byteOrder) throws IOException {
		if(byteOrder == BYTE_ORDERING_LITTLE_ENDIAN) {
			int b3 = inputStream.read();
			int b2 = inputStream.read();
			int b1 = inputStream.read();
			int b0 = inputStream.read();
			return (b0<<24) | (b1<<16) | (b2<<8) | b3;	
		}
		else {
			int b0 = inputStream.read();
			int b1 = inputStream.read();
			int b2 = inputStream.read();
			int b3 = inputStream.read();
			return (b0<<24) | (b1<<16) | (b2<<8) | b3;
		}
	}
	
	public String readUid(int length) throws IOException {
		return readString(length);
	}
	
	public String readString(int length) throws IOException {
		return new String(readByteArray(length));
	}
	
	public String readString(int length, boolean trim) throws IOException {
		String string = readString(length);
		if(trim)
			string = string.trim();
		return string;
	}
	
	public byte[] readByteArray(int length) throws IOException {
		byte[] buffer = new byte[length];
		int totalBytesRead = 0;
		while(totalBytesRead != length) {
			totalBytesRead += inputStream.read(buffer, 
					totalBytesRead, 
					length - totalBytesRead);
		}	
		return buffer;
	}
	
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}
	
	public int readUL() throws IOException {
		return readUInt32();
	}
	
	public String readUI(int length) throws IOException {
		return readUid(length);
	}
	
	public int readUS() throws IOException {
		return readUInt16();
	}
	
}
