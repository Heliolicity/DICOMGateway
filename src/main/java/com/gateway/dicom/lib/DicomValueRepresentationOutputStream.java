package com.gateway.dicom.lib;

/*
 * Author: Robert Sadleir
 * 
 * */


import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class DicomValueRepresentationOutputStream extends DicomPrimitiveOutputStream {

	public DicomValueRepresentationOutputStream(OutputStream outputStream, int byteOrdering) {
		super(outputStream, byteOrdering);
	}
	
	public void writeExplicitUL(int groupNumber, int elementNumber, int unsignedLong) throws IOException {
		writeUInt16(groupNumber);	// 2 bytes
		writeUInt16(elementNumber);	// 2 bytes
		writeString("UL");			// 2 bytes
		writeUInt16(4);				// 2 bytes
		writeUInt32(unsignedLong);
	}
	
	public void writeExplicitOB(int groupNumber, int elementNumber, int[] otherByteString) throws IOException {
		writeUInt16(groupNumber);	// 2 bytes
		writeUInt16(elementNumber);	// 2 bytes
		writeString("OB");			// 2 bytes
		writeUInt16(0x0000);		// 2 bytes
		writeUInt32(otherByteString.length);	// 4 bytes
		for(int i=0; i<otherByteString.length; i++) 
			writeUInt8(otherByteString[i]);
	}
	
	public void writeExplicitUI(int groupNumber, int elementNumber, String uniqueIdentifier) throws IOException {
		writeUInt16(groupNumber);	// 2 bytes
		writeUInt16(elementNumber);	// 2 bytes
		writeVR("UI");				// 2 bytes
		int length = uniqueIdentifier.length();	
		if(length % 2 == 0) { // even	
			writeUInt16(length);	// 2 bytes
			writeString(uniqueIdentifier);	
		}
		else {
			writeUInt16(length + 1);	// 2 bytes
			writeString(uniqueIdentifier);	
			writeUInt8(0x00);			// 1 byte
		}
	}
	
	public void writeImplicitUS(int groupNumber, int elementNumber, int unsignedShort) throws IOException {
		writeUInt16(groupNumber);
		writeUInt16(elementNumber);	
		writeUInt32(0x0002);
		writeUInt16(unsignedShort);
	}
	
	public void writeImplicitIS(int groupNumber, int elementNumber, String integerString) throws IOException {
		writeImplicitString(groupNumber, elementNumber, integerString);
	}
	
	public void writeImplicitPN(int groupNumber, int elementNumber, String personName) throws IOException {
		writeImplicitString(groupNumber, elementNumber, personName);
	}
	
	public void writeImplicitLO(int groupNumber, int elementNumber, String longString) throws IOException {
		writeImplicitString(groupNumber, elementNumber, longString);
	}
	
	public void writeImplicitDA(int groupNumber, int elementNumber, String date) throws IOException {
		writeImplicitString(groupNumber, elementNumber, date);
	}
	
	public void writeImplicitCS(int groupNumber, int elementNumber, String codeString) throws IOException {
		writeImplicitString(groupNumber, elementNumber, codeString);
	}
	
	public void writeImplicitDS(int groupNumber, int elementNumber, String decimalString) throws IOException {
		writeImplicitString(groupNumber, elementNumber, decimalString);
	}
	
	public void writeImplicitTM(int groupNumber, int elementNumber, String time) throws IOException {
		writeImplicitString(groupNumber, elementNumber, time);
	}
	
	public void writeImplicitSH(int groupNumber, int elementNumber, String shortString) throws IOException {
		writeImplicitString(groupNumber, elementNumber, shortString);
	}
	
	public void writeImplicitString(int groupNumber, int elementNumber, String stringValue) throws IOException {
		writeUInt16(groupNumber);
		writeUInt16(elementNumber);
		int length = stringValue.length();
		if(length % 2 == 0) { // even
			writeUInt32(length);
			writeString(stringValue);	
		}
		else {
			writeUInt32(length + 1);
			writeString(stringValue + " ");	
		}
	}
	
	public void writeImplicitUI(int groupNumber, int elementNumber, String uniqueIdentifier) throws IOException {
		writeUInt16(groupNumber);
		writeUInt16(elementNumber);
		int length = uniqueIdentifier.length();
		if(length % 2 == 0) { // even
			writeUInt32(length);
			writeString(uniqueIdentifier);	
		}
		else {
			writeUInt32(length + 1);
			writeString(uniqueIdentifier);	
			writeUInt8(0x00);	
		}	
	}
	
	public void writeImplicitOW(int groupNumber, int elementNumber, int[] otherWordString) throws IOException {
		writeUInt16(groupNumber);
		writeUInt16(elementNumber);
		int numberOfWords = otherWordString.length;
		writeUInt32(numberOfWords / 2);
		for(int i=0; i<numberOfWords; i++) {
			writeUInt16(otherWordString[i]);
		}
	}
	
	public void close() throws IOException {
		outputStream.close();
	}

	public void flush() throws IOException { this.outputStream.flush(); }
	
}
