package com.gateway.dicom.protocols;

public class TestMain {

	public static void main (String args []) {
		
		A_ASSOCIATE_RQ request = new A_ASSOCIATE_RQ("1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1");
		request.buildProtocolVersion();
		request.buildCallingAE();
		request.buildCalledAE();
		request.buildApplicationContext();
		request.buildPresentationContext();
		request.buildUserInformation();
		request.buildPduLength();
		request.buildRequest();
		
		byte[] arr = request.getRequest();
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
