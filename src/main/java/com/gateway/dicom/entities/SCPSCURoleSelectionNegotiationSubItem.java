package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

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

	public int getUidLength() {
		return uidLength;
	}

	public void setUidLength(int uidLength) {
		this.uidLength = uidLength;
	}

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

	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.uidLength);
			this.stream.write(this.sopClassUID.getBytes());
			this.stream.write(this.scuRole);
			this.stream.write(this.scpRole);
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void writeToBuffer() {

		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.itemType);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt16(this.itemLength);
			this.buffer.writeUInt16(this.uidLength);
			this.buffer.write(this.sopClassUID.getBytes());
			this.buffer.writeUInt16(this.scuRole);
			this.buffer.writeUInt16(this.scpRole);
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
