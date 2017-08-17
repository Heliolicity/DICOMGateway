package com.gateway.dicom.protocols;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class A_RELEASE_RQ extends PDU {

	private DicomOutputBuffer buffer;
	
	public A_RELEASE_RQ(byte pduType) {
		
		super();
		this.pduType = pduType;
		this.pduLength = 4;
		
	}
	
	public A_RELEASE_RQ() {}
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt32(pduLength);
			
		}
		
		catch(Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
