package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class ApplicationContext extends DICOMItem {

	//private byte itemType = 0x10;
	private String applicationContextName;
	
	public ApplicationContext(byte itemType, String applicationContextName) {
		super();
		this.itemType = itemType;
		this.applicationContextName = applicationContextName;
		byte[] bytes = this.applicationContextName.getBytes();
		this.itemLength = this.convertDecToHex(bytes.length);
	}
	
	public ApplicationContext() { super(); }

	public String getApplicationContextName() {
		return applicationContextName;
	}

	public void setApplicationContextName(String applicationContextName) {
		this.applicationContextName = applicationContextName;
	}

	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.applicationContextName.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
