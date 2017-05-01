package com.gateway.dicom.entities;

public class UserInformation {

	private byte itemType = 0x50;
	private byte reserved = 0x00;
	private int itemLength;
	private String userData;
	
	public UserInformation(String userData) {
		super();
		this.userData = userData;
	}
	
	public UserInformation() { super(); }

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

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}
	
}
