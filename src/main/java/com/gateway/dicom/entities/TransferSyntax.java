package com.gateway.dicom.entities;

public class TransferSyntax {

	private byte itemType = 0x40;
	private byte reserved = 0x00;
	private int itemLength;
	private String transferSyntaxName;
	
	public TransferSyntax(String transferSyntaxName) {
		super();
		this.transferSyntaxName = transferSyntaxName;
		//byte[] bytes = this.transferSyntaxName.getBytes();
		//this.itemLength = bytes.length;
	}
	
	public TransferSyntax() { super(); }

	public byte getItemType() {
		return itemType;
	}

	public void setItemType(byte itemType) {
		this.itemType = itemType;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	public int getItemLength() {
		return itemLength;
	}

	public void setItemLength(int itemLength) {
		this.itemLength = itemLength;
	}

	public String getTransferSyntaxName() {
		return transferSyntaxName;
	}

	public void setTransferSyntaxName(String transferSyntaxName) {
		this.transferSyntaxName = transferSyntaxName;
	}
	
}
