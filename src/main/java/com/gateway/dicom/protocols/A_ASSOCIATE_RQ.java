package com.gateway.dicom.protocols;

public class A_ASSOCIATE_RQ {

	private String pduType = "01H"; //1 byte in length
	private byte bytPduType = 0x01;
	private String reserved = "00H"; //1 byte in length
	private byte bytReserved = 0x00;
	private int pduLength; //4 bytes in length
	private byte[] bytPduLength;
	private String protocolVersion; //2 bytes in length
	private byte[] bytProtocolVersion;
	private String calledAE; //16 bytes in length
	private byte[] bytCalledAE;
	private String callingAE; //16 bytes in length
	private byte[] bytCallingAE;
	private String applicationContext;
	private byte[] bytApplicationContext;
	private String presentationContext;
	private byte[] bytPresentationContext;
	private String userInformation;
	private byte[] bytUserInformation;
	private byte[] request;
	
	public A_ASSOCIATE_RQ(String protocolVersion, String calledAE, String callingAE) {
		super();
		this.protocolVersion = protocolVersion;
		this.calledAE = calledAE;
		this.callingAE = callingAE;
	}
	
	public A_ASSOCIATE_RQ(String protocolVersion, String calledAE, String callingAE, String applicationContext, String presentationContext, String userInformation) {
		super();
		this.protocolVersion = protocolVersion;
		this.calledAE = calledAE;
		this.callingAE = callingAE;
		this.applicationContext = applicationContext;
		this.presentationContext = presentationContext;
		this.userInformation = userInformation;
	}
	
	public A_ASSOCIATE_RQ() { super(); }

	public void buildRequest() {
		
		pl("BYTE ARRAY LENGTH: " + (8 + this.pduLength));
		
		this.request = new byte[8 + this.pduLength];
		this.request[0] = this.bytPduType;
		this.request[1] = this.bytReserved;
		int pointer = 2;
		
		for (int a = 0; a < this.bytPduLength.length; a ++) { 
			
			this.request[pointer] = this.bytPduLength[a];
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int b = 0; b < this.bytProtocolVersion.length; b ++) { 
			
			this.request[pointer] = this.bytProtocolVersion[b];
			pointer ++;
			
		}
		
		this.request[pointer] = this.bytReserved;
		pointer ++;
		this.request[pointer] = this.bytReserved;
		pointer ++;
		
		for (int c = 0; c < this.bytCalledAE.length; c ++) { 
			
			this.request[pointer] = this.bytCalledAE[c];
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int d = 0; d < this.bytCallingAE.length; d ++) { 
			
			this.request[pointer] = this.bytCallingAE[d];
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int e = 0; e < 32; e ++) {
			
			this.request[pointer] = this.bytReserved;
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int f = 0; f < this.bytApplicationContext.length; f ++) {
			
			this.request[pointer] = this.bytApplicationContext[f];
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int g = 0; g < this.bytPresentationContext.length; g ++) {
			
			this.request[pointer] = this.bytPresentationContext[g];
			pointer ++;
			
		}
		
		pointer ++;
		
		for (int h = 0; h < this.bytUserInformation.length; h ++) {
			
			this.request[pointer] = this.bytUserInformation[h];
			pointer ++;
			
		}
		
	}
	
	public void buildPduLength() {
		
		this.pduLength = this.bytProtocolVersion.length;
		this.pduLength += 2; //reserved
		this.pduLength += this.bytCallingAE.length;
		this.pduLength += this.bytCalledAE.length;
		this.pduLength += 32; //reserved
		this.pduLength += this.bytApplicationContext.length;
		this.pduLength += this.bytPresentationContext.length;
		this.pduLength += this.bytUserInformation.length;
		
		String binaryRepresentation = Integer.toBinaryString(this.pduLength);
		
		this.bytPduLength = binaryRepresentation.getBytes();
		
		if (this.bytPduLength.length < 4) 
			
			for (int i = this.bytPduLength.length; i < 4; i ++) 
				
				this.bytPduLength[i] = 0x00;
			
		
		
	}
	
	public void buildProtocolVersion() {
		
		this.bytProtocolVersion = new byte[2];
		byte a = 0x00;
		this.bytProtocolVersion[0] = a;
		
		if (this.protocolVersion.equals("1")) this.bytProtocolVersion[1] = 0x01;
		
	}
	
	public void buildCallingAE() {
		
		this.bytCallingAE = this.callingAE.getBytes();
		byte[] b = new byte[16];
		
		if (this.bytCallingAE.length < 16) {
			
			for (int i = 0; i < this.bytCallingAE.length; i ++) 
				
				b[i] = this.bytCallingAE[i];
				
			for (int j = this.bytCallingAE.length - 1; j < 16; j ++) 
				
				b[j] = 0x20;
				
			this.bytCallingAE = b;
			
		}
		
	}
	
	public void buildCalledAE() {
		
		this.bytCalledAE = this.calledAE.getBytes();
		byte[] b = new byte[16];
		
		if (this.bytCalledAE.length < 16) {
			
			for (int i = 0; i < this.bytCalledAE.length; i ++) 
				
				b[i] = this.bytCalledAE[i];
				
			for (int j = this.bytCalledAE.length - 1; j < 16; j ++) 
				
				b[j] = 0x20;
				
			this.bytCalledAE = b;
			
		}
		
	}
	
	public void buildApplicationContext() {
		
		this.bytApplicationContext = this.applicationContext.getBytes();
		
	}
	
	public void buildPresentationContext() {
		
		this.bytPresentationContext = this.presentationContext.getBytes();
		
	}
	
	public void buildUserInformation() {
	
		this.bytUserInformation = this.userInformation.getBytes();
		
	}
	
	public String getPduType() {
		return pduType;
	}

	public void setPduType(String pduType) {
		this.pduType = pduType;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public int getPduLength() {
		return pduLength;
	}

	public void setPduLength(int pduLength) {
		this.pduLength = pduLength;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getCalledAE() {
		return calledAE;
	}

	public void setCalledAE(String calledAE) {
		this.calledAE = calledAE;
	}

	public String getCallingAE() {
		return callingAE;
	}

	public void setCallingAE(String callingAE) {
		this.callingAE = callingAE;
	}

	public String getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(String applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getPresentationContext() {
		return presentationContext;
	}

	public void setPresentationContext(String presentationContext) {
		this.presentationContext = presentationContext;
	}

	public String getUserInformation() {
		return userInformation;
	}

	public void setUserInformation(String userInformation) {
		this.userInformation = userInformation;
	}

	public byte[] getRequest() {
		return request;
	}

	public void setRequest(byte[] request) {
		this.request = request;
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
