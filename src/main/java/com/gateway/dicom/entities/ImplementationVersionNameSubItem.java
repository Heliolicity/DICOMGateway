package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class ImplementationVersionNameSubItem extends DICOMItem {

	protected String implementationVersionName;
	
	public ImplementationVersionNameSubItem(byte itemType, String implementationVersionName) {
		super();
		this.itemType = itemType;
		this.implementationVersionName = implementationVersionName;
		byte[] bytes = this.implementationVersionName.getBytes();
		this.itemLength = bytes.length;
		//this.itemLength = this.convertDecToHex(bytes.length);
		//this.itemLength = this.convertDecToBin(this.itemLength);
	}

	public ImplementationVersionNameSubItem() { super(); }

	public String getImplementationVersionName() {
		return implementationVersionName;
	}

	public void setImplementationVersionName(String implementationVersionName) {
		this.implementationVersionName = implementationVersionName;
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.implementationVersionName.getBytes());
		
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
			this.buffer.write(this.implementationVersionName.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}		
		
	}
	
}
