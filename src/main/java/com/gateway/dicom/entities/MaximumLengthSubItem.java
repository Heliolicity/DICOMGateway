package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class MaximumLengthSubItem extends DICOMItem {

	//private byte itemType = 0x51;
	protected int maxPDULengthReceive;
	
	public MaximumLengthSubItem(byte itemType, int maxPDULengthReceive) {
		super();
		this.itemType = itemType;
		this.itemLength = 0x00000004;
		this.maxPDULengthReceive = maxPDULengthReceive;
	}
	
	public MaximumLengthSubItem() { super(); }

	public int getMaxPDULengthReceive() {
		return maxPDULengthReceive;
	}

	public void setMaxPDULengthReceive(int maxPDULengthReceive) {
		this.maxPDULengthReceive = maxPDULengthReceive;
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.maxPDULengthReceive);
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
