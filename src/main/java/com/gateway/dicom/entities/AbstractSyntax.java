package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class AbstractSyntax extends DICOMItem {

	protected String abstractSyntaxName;
	
	public AbstractSyntax(byte itemType, String abstractSyntaxName) {
		super();
		this.itemType = itemType;
		this.abstractSyntaxName = abstractSyntaxName;
		byte[] bytes = this.abstractSyntaxName.getBytes();
		this.itemLength = this.convertDecToHex(bytes.length);
	}
	
	public AbstractSyntax() { super(); }

	public String getAbstractSyntaxName() {
		return abstractSyntaxName;
	}

	public void setAbstractSyntaxName(String abstractSyntaxName) {
		this.abstractSyntaxName = abstractSyntaxName;
	}
	
	public void writeToBuffer() {
		
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
	
}
