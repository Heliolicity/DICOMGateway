package com.gateway.dicom.entities;

public class ApplicationContext {

	private byte itemType = 0x10;
	private byte reserved = 0x00;
	private int itemLength;
	private String applicationContextName;
	
	public ApplicationContext(String applicationContextName) {
		super();
		this.applicationContextName = applicationContextName;
		//byte[] bytes = this.applicationContextName.getBytes();
		//this.itemLength = bytes.length;
	}
	
	public ApplicationContext() { super(); }

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

	public String getApplicationContextName() {
		return applicationContextName;
	}

	public void setApplicationContextName(String applicationContextName) {
		this.applicationContextName = applicationContextName;
	}

	
}
