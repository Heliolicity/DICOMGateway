package com.gateway.dicom.protocols;

public class A_RELEASE_RQ extends PDU {

	public A_RELEASE_RQ(byte pduType) {
		
		super();
		this.pduType = pduType;
		this.pduLength = 0x00000004;
		
	}
	
	public A_RELEASE_RQ() {}
	
}
