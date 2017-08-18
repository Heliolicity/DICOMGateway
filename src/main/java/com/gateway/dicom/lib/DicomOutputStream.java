package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DicomOutputStream extends DicomByteOrder {
	
	protected OutputStream outputStream;
	
	public DicomOutputStream(OutputStream outputStream, int byteOrdering) {
		super(byteOrdering);
		this.outputStream = outputStream;
	}
	
	public DicomOutputBuffer getBufferedVersion() {
		return new DicomOutputBuffer(getByteOrdering());
	}
	
	public void write(Writable writable) throws IOException {
		if(writable != null)
			writable.write(this);
	}
	
	public void write(ArrayList<?> arrayList) throws IOException {
		if(arrayList != null)
			for(Object object : arrayList)
				this.write((Writable)object);
	}
	
	public void write(byte[] byteArray) throws IOException {
		outputStream.write(byteArray);
	}
	
	public void writeUid(String uid, boolean pad) throws IOException {
		outputStream.write(uid.getBytes());
		if(uid.length()%2 == 0 && pad)
			outputStream.write(0x00);
	}
	
	public void writeUInt8(int value) throws IOException {
		pl("" + value);
		outputStream.write(value);
	}
	
	public void writeUInt16(int value) throws IOException {
		writeUInt16(byteOrdering, value);
	}
	
	public void writeUInt16(int byteOrdering, int value) throws IOException {
		if(byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			pl("HERE 1");
			outputStream.write(value&0xff);
			pl("" + (value&0xff));
			outputStream.write((value>>8)&0xff);
			pl("" + ((value>>8)&0xff));
		}
		else {
			pl("HERE 2");
			outputStream.write((value>>24)&0xff);
			pl("" + ((value>>24)&0xff));
			outputStream.write((value>>16)&0xff);
			pl("" + ((value>>16)&0xff));
		}
	}
	
	public void writeUInt32(int byteOrdering, int value) throws IOException {
		if(byteOrdering == BYTE_ORDERING_LITTLE_ENDIAN) {
			pl("HERE 1");
			outputStream.write(value&0xff);
			pl("" + (value&0xff));
			outputStream.write((value>>8)&0xff);
			pl("" + ((value>>8)&0xff));
			outputStream.write((value>>16)&0xff);
			pl("" + ((value>>16)&0xff));
			outputStream.write((value>>24)&0xff);
			pl("" + ((value>>24)&0xff));
		} else {
			pl("HERE 2");
			outputStream.write((value>>24)&0xff);
			pl("" + ((value>>24)&0xff));
			outputStream.write((value>>16)&0xff);
			pl("" + ((value>>16)&0xff));
			outputStream.write((value>>8)&0xff);
			pl("" + ((value>>8)&0xff));
			outputStream.write(value&0xff);
			pl("" + (value&0xff));
		}
	}
	
	public void writeUInt32(int value) throws IOException {
		writeUInt32(byteOrdering, value);
	}
	
	public void writeString(String string) throws IOException {
		outputStream.write(string.getBytes());
	}
	
	// writeFixedLengthStringWithTrailingSpacesIfNeeded
	public void writeStringWithTrailingSpaces(String string, int totalNumberOfCharactersToWrite) throws IOException {
		outputStream.write(string.getBytes());
		for(int j=0; j<totalNumberOfCharactersToWrite-string.length(); j++)
			outputStream.write(0x20); // Trailing spaces
	}
	
	public void writeZeros(int count) throws IOException {
		for(int i=0; i<count; i++)
			outputStream.write(0x00);
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
