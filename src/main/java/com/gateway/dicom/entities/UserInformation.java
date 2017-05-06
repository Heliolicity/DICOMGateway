package com.gateway.dicom.entities;

public class UserInformation extends DICOMItem {

	//private byte itemType = 0x50;
	private String userData;
	
	public UserInformation(String userData) {
		super();
		this.userData = userData;
	}
	
	public UserInformation() { super(); }

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}
	
}
