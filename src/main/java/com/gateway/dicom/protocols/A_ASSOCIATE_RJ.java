package com.gateway.dicom.protocols;

public class A_ASSOCIATE_RJ extends PDU {

	//private byte pduType;
	//private byte reserved = 0x00;
	//private int pduLength; //4 bytes in length
	private byte result;
	private byte source;
	private byte reasonDiag;
	
	public A_ASSOCIATE_RJ(byte pduType, int pduLength, byte result, byte source, byte reasonDiag) {
		super();
		this.pduType = pduType;
		this.pduLength = pduLength;
		this.result = result;
		this.source = source;
		this.reasonDiag = reasonDiag;
	}
	
	public A_ASSOCIATE_RJ() {}

	/*public byte getPduType() {
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
	}*/

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	public byte getSource() {
		return source;
	}

	public void setSource(byte source) {
		this.source = source;
	}

	public byte getReasonDiag() {
		return reasonDiag;
	}

	public void setReasonDiag(byte reasonDiag) {
		this.reasonDiag = reasonDiag;
	}
	
	
	
}
