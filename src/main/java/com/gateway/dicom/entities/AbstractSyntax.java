package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class AbstractSyntax extends DICOMItem {

	protected String abstractSyntaxName;
	
	public AbstractSyntax(byte itemType, String abstractSyntaxName) {
		super();
		this.itemType = itemType;
		this.abstractSyntaxName = abstractSyntaxName;
		byte[] bytes = this.abstractSyntaxName.getBytes();
		this.itemLength = bytes.length;		
	}
	
	public AbstractSyntax() { super(); }

	public String getAbstractSyntaxName() {
		return abstractSyntaxName;
	}

	public void setAbstractSyntaxName(String abstractSyntaxName) {
		this.abstractSyntaxName = abstractSyntaxName;
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.abstractSyntaxName.getBytes());
		
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
			this.buffer.write(this.abstractSyntaxName.getBytes());
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
