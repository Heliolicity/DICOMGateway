package com.gateway.dicom.entities;

public class AbstractSyntax {

	private byte itemType = 0x30;
	private byte reserved = 0x00;
	private int itemLength;
	private String abstractSyntaxName;
	
	public AbstractSyntax(String abstractSyntaxName) {
		super();
		this.abstractSyntaxName = abstractSyntaxName;
		byte[] bytes = this.abstractSyntaxName.getBytes();
		this.itemLength = bytes.length;
	}
	
	public AbstractSyntax() { super(); }

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

	public String getAbstractSyntaxName() {
		return abstractSyntaxName;
	}

	public void setAbstractSyntaxName(String abstractSyntaxName) {
		this.abstractSyntaxName = abstractSyntaxName;
	}
	
}
