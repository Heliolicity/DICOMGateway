package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class TransferSyntax extends DICOMItem {

	//private byte itemType = 0x40;
	private String transferSyntaxName;
	
	public TransferSyntax(byte itemType, String transferSyntaxName) {
		super();
		this.itemType = itemType;
		this.transferSyntaxName = transferSyntaxName;
		byte[] bytes = this.transferSyntaxName.getBytes();
		this.itemLength = bytes.length;
		//this.itemLength = this.convertDecToHex(bytes.length);
		//this.itemLength = this.convertDecToBin(this.itemLength);
	}
	
	public TransferSyntax() { super(); }

	public String getTransferSyntaxName() {
		return transferSyntaxName;
	}

	public void setTransferSyntaxName(String transferSyntaxName) {
		this.transferSyntaxName = transferSyntaxName;
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.transferSyntaxName.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
