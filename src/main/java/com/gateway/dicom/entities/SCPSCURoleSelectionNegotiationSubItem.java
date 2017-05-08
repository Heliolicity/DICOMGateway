package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class SCPSCURoleSelectionNegotiationSubItem extends DICOMItem {

	protected int uidLength;
	protected String sopClassUID;
	protected int scpRole;
	protected int scuRole;
	
	public SCPSCURoleSelectionNegotiationSubItem(byte itemType, String sopClassUID, 
			int scpRole, int scuRole) {
		
		super();
		this.itemType = itemType;
		this.sopClassUID = sopClassUID;
		byte[] bytes = this.sopClassUID.getBytes();
		this.uidLength = bytes.length;
		this.scpRole = scpRole;
		this.scuRole = scuRole;
		this.itemLength = 2 + this.uidLength + 2 + 2;
		
	}
	
	public SCPSCURoleSelectionNegotiationSubItem() { super(); }

	public String getSopClassUID() {
		return sopClassUID;
	}

	public void setSopClassUID(String sopClassUID) {
		this.sopClassUID = sopClassUID;
	}
	
	public int getScpRole() {
		return scpRole;
	}

	public void setScpRole(int scpRole) {
		this.scpRole = scpRole;
	}

	public int getScuRole() {
		return scuRole;
	}

	public void setScuRole(int scuRole) {
		this.scuRole = scuRole;
	}

	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.uidLength);
			this.stream.write(this.sopClassUID.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
