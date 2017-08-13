package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class ExtendedNegotiationSubItem extends DICOMItem {

	protected int sopClassUIDLength;
	protected String sopClassUID;
	protected String serviceClassApplicationInformation;
	
	public ExtendedNegotiationSubItem(byte itemType, String sopClassUID, 
			String serviceClassApplicationInformation) {
		
		super();
		this.itemType = itemType;
		this.sopClassUID = sopClassUID;
		byte[] bytes = this.sopClassUID.getBytes();
		this.sopClassUIDLength = bytes.length;
		this.serviceClassApplicationInformation = serviceClassApplicationInformation;
		bytes = this.serviceClassApplicationInformation.getBytes();
		this.itemLength = 2 + this.sopClassUIDLength + bytes.length;
		//this.itemLength = this.convertDecToHex(this.itemLength);
		//this.itemLength = this.convertDecToBin(this.itemLength);
		
	}
	
	public ExtendedNegotiationSubItem() { super(); }

	public int getSopClassUIDLength() {
		return sopClassUIDLength;
	}

	public void setSopClassUIDLength(int sopClassUIDLength) {
		this.sopClassUIDLength = sopClassUIDLength;
	}

	public String getSopClassUID() {
		return sopClassUID;
	}

	public void setSopClassUID(String sopClassUID) {
		this.sopClassUID = sopClassUID;
	}

	public String getServiceClassApplicationInformation() {
		return serviceClassApplicationInformation;
	}

	public void setServiceClassApplicationInformation(String serviceClassApplicationInformation) {
		this.serviceClassApplicationInformation = serviceClassApplicationInformation;
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.sopClassUIDLength);
			this.stream.write(this.sopClassUID.getBytes());
			this.stream.write(this.serviceClassApplicationInformation.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.itemType);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt16(this.itemLength);
			this.buffer.writeUInt16(this.sopClassUIDLength);
			this.buffer.write(this.sopClassUID.getBytes());
			this.buffer.write(this.serviceClassApplicationInformation.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
