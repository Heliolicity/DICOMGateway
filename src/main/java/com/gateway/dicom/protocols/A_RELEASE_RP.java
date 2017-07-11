package com.gateway.dicom.protocols;

public class A_RELEASE_RP extends PDU {

	public A_RELEASE_RP(byte pduType) {
		
		super();
		this.pduType = pduType;
		this.pduLength = 0x00000004;
		
	}
	
	public A_RELEASE_RP() {}
	
}
