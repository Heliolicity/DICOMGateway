package com.gateway.dicom.protocols;

public class PDU {

	protected byte pduType;
	protected byte reserved = 0x00;
	protected int pduLength; 
	
	public PDU(byte pduType, int pduLength) {
		super();
		this.pduType = pduType;
		this.pduLength = pduLength;
	}

	public PDU(byte pduType) {
		super();
		this.pduType = pduType;
	}
	
	public PDU() {}

	public byte getPduType() {
		return pduType;
	}

	public void setPduType(byte pduType) {
		this.pduType = pduType;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	public int getPduLength() {
		return pduLength;
	}

	public void setPduLength(int pduLength) {
		this.pduLength = pduLength;
	}
	
}
