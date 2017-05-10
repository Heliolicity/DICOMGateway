package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class ImplementationClassUIDSubItem extends DICOMItem {

	protected String implementationClassUID;
	
	public ImplementationClassUIDSubItem(byte itemType, String implementationClassUID) {
		super();
		this.itemType = itemType;
		this.implementationClassUID = implementationClassUID;
		byte[] bytes = this.implementationClassUID.getBytes();
		this.itemLength = bytes.length;
		//this.itemLength = this.convertDecToHex(bytes.length);
		//this.itemLength = this.convertDecToBin(this.itemLength);
	}
	
	public ImplementationClassUIDSubItem() { super(); }

	public String getImplementationClassUID() {
		return implementationClassUID;
	}

	public void setImplementationClassUID(String implementationClassUID) {
		this.implementationClassUID = implementationClassUID;
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.implementationClassUID.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
