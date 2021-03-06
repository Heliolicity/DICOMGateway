package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class ImplementationClassUIDSubItem extends DICOMItem {

	protected String implementationClassUID;
	
	public ImplementationClassUIDSubItem(byte itemType, String implementationClassUID) {
		super();
		this.itemType = itemType;
		this.implementationClassUID = implementationClassUID;
		byte[] bytes = this.implementationClassUID.getBytes();
		this.itemLength = bytes.length;
	}
	
	public ImplementationClassUIDSubItem() { super(); }

	public String getImplementationClassUID() {
		return implementationClassUID;
	}

	public void setImplementationClassUID(String implementationClassUID) {
		this.implementationClassUID = implementationClassUID;
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.implementationClassUID.getBytes());
		
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
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt8(this.itemLength);
			this.buffer.write(this.implementationClassUID.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}		
	}
	
}
