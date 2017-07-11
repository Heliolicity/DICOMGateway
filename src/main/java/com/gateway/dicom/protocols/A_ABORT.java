package com.gateway.dicom.protocols;

public class A_ABORT extends PDU {

	/*
	 * This Source field shall contain an integer value encoded as an unsigned binary number. One of the following values shall be used:

	0 - DICOM UL service-user (initiated abort)
	
	1 - reserved
	
	2 - DICOM UL service-provider (initiated abort)*/
	protected byte source;
	
	/*
	 * This field shall contain an integer value encoded as an unsigned binary number. If the Source field has the value (2) "DICOM UL service-provider", it shall take one of the following:

	0 - reason-not-specified1 - unrecognized-PDU
	
	2 - unexpected-PDU
	
	3 - reserved
	
	4 - unrecognized-PDU parameter
	
	5 - unexpected-PDU parameter
	
	6 - invalid-PDU-parameter value
	
	If the Source field has the value (0) "DICOM UL service-user", this reason field shall not be significant. It shall be sent with a value 00H but not tested to this value when received.*/
	protected byte reason;
	
	public A_ABORT(byte pduType, byte source, byte reason) {
		
		super();
		this.pduType = pduType;
		this.source = source;
		this.reason = reason;
		this.pduLength = 0x00000004;
		
	}
	
	public A_ABORT() {}

	public byte getSource() {
		return source;
	}

	public void setSource(byte source) {
		this.source = source;
	}

	public byte getReason() {
		return reason;
	}

	public void setReason(byte reason) {
		this.reason = reason;
	}
	
}
