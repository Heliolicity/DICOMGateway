package com.gateway.dicom.lib;

/*
 * Author: Robert Sadleir
 * 
 * */

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class DicomPrimitiveOutputStream extends DicomByteOrderable {
	
	//protected OutputStream outputStream;
	protected DataOutputStream outputStream;
	
	public DicomPrimitiveOutputStream(OutputStream outputStream, int byteOrdering) {
		super(byteOrdering);
		this.outputStream = new DataOutputStream(outputStream);
	}
	
	public void writeVR(String VR) throws IOException {
		writeString(VR);
	}
	
	public void writeString(String string) throws IOException {
		this.outputStream.write(string.getBytes());
	}
	
	public void writeUInt8(int value) throws IOException {
		this.outputStream.write(value);
	}

	public void writeUInt16(int value) throws IOException {
		int b0 = value & 0x00ff;
		int b1 = (value & 0xff00) >> 8;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			this.outputStream.write(b0);
			this.outputStream.write(b1);
		}
		else {
			this.outputStream.write(b1);
			this.outputStream.write(b0);
		}
	}

	public void writeUInt32(int value) throws IOException {
		int b0 = value & 0x000000ff; //masking out 24 bits
		int b1 = (value & 0x0000ff00) >> 8;
		int b2 = (value & 0x00ff0000) >> 16;
		int b3 = (value & 0xff000000) >> 24;
		
		if(this.byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			this.outputStream.write(b0);
			this.outputStream.write(b1);
			this.outputStream.write(b2);
			this.outputStream.write(b3);
		}
		else {
			this.outputStream.write(b3);
			this.outputStream.write(b2);
			this.outputStream.write(b1);
			this.outputStream.write(b0);
		}
	}
	
	public void flush() throws IOException { this.outputStream.flush(); }
	
}
