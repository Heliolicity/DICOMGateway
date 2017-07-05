package com.gateway.dicom.entities;

public class MessageControlHeader extends DICOMItem {

	/*
	 * The Message Control Header shall be made of one byte with the 
	 * least significant bit (bit 0) taking one of the following values:

    	If bit 0 is set to 1, the following fragment shall contain Message Command information.

    	If bit 0 is set to 0, the following fragment shall contain Message Data Set information.

	The next least significant bit (bit 1) shall be defined by the following rules:

    	If bit 1 is set to 1, the following fragment shall contain the last fragment of a Message Data Set or of a Message Command.

    	If bit 1 is set to 0, the following fragment does not contain the last fragment of a Message Data Set or of a Message Command.

	Bits 2 through 7 are always set to 0 by the sender and never checked by the receiver.
	 * 
	 * */
	
	protected byte header;
	protected int firstBit;
	protected int secondBit;
	protected int intHeader;
	
	public MessageControlHeader(int firstBit, int secondBit) {
		
		super();
		this.firstBit = firstBit;
		this.secondBit = secondBit;
		
		if (this.firstBit == 1) this.header |= 1 << 1;
		else if (this.firstBit == 1) this.header |= 0 << 1;
		
		if (this.secondBit == 1) this.header |= 1 << 2;
		else if (this.secondBit == 0) this.header |= 0 << 2;
		
	}
	
	public MessageControlHeader(byte header) { this.header = header; }
	
	public MessageControlHeader(int intHeader) { this.intHeader = intHeader; }
	
	public MessageControlHeader() {}

	public byte getHeader() {
		return header;
	}

	public void setHeader(byte header) {
		this.header = header;
	}

	public int getFirstBit() {
		return firstBit;
	}

	public void setFirstBit(int firstBit) {
		this.firstBit = firstBit;
	}

	public int getSecondBit() {
		return secondBit;
	}

	public void setSecondBit(int secondBit) {
		this.secondBit = secondBit;
	}

	public int getIntHeader() {
		return intHeader;
	}

	public void setIntHeader(int intHeader) {
		this.intHeader = intHeader;
	}
	
}
