package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.UserInformation;

public class A_ASSOCIATE_RQ extends PDU {

	//private byte pduType;
	//private byte reserved = 0x00;
	//private int pduLength; //4 bytes in length
	private int protocolVersion = 0x0000; //2 bytes in length
	private String calledAE; //16 bytes in length
	private String callingAE; //16 bytes in length
	private ApplicationContext applicationContext;
	private PresentationContext_RQ presentationContext;
	private UserInformation userInformation;
	private ByteArrayOutputStream stream;
	
	public A_ASSOCIATE_RQ(byte pduType, String calledAE, String callingAE, ApplicationContext applicationContext,
			PresentationContext_RQ presentationContext, UserInformation userInformation) {
		super();
		this.pduType = pduType;
		this.calledAE = calledAE;
		this.callingAE = callingAE;
		this.applicationContext = applicationContext;
		this.presentationContext = presentationContext;
		this.userInformation = userInformation;
	}

	public A_ASSOCIATE_RQ(String calledAE, String callingAE) {
		super();
		this.calledAE = calledAE;
		this.callingAE = callingAE;
	}
		
	public A_ASSOCIATE_RQ() { super(); }

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

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(int protocolVersion) {
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

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public PresentationContext_RQ getPresentationContext() {
		return presentationContext;
	}

	public void setPresentationContext(PresentationContext_RQ presentationContext) {
		this.presentationContext = presentationContext;
	}

	public UserInformation getUserInformation() {
		return userInformation;
	}

	public void setUserInformation(UserInformation userInformation) {
		this.userInformation = userInformation;
	}
	
	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public void setStream(ByteArrayOutputStream stream) {
		this.stream = stream;
	}

	public void calculateLength() {
		
		//L = 2 (Protocol Version) + 2 (Reserved) + 16 (Called AE Title) + 
		//16 (Calling AE Title) + 32 (Reserved) + L of (Application Context) + 
		//L of (Presentation Context) + L of (User Information)
		
		int protocolVersionLength = 2;
		byte[] bytes = this.calledAE.getBytes();
		int calledAELength = bytes.length;
		bytes = this.callingAE.getBytes();
		int callingAELength = bytes.length;
		
		this.applicationContext.writeToBuffer();
		int applicationContextLength = this.applicationContext.getStream().size();
		
		this.presentationContext.writeToBuffer();
		int presentationContextLength = this.presentationContext.getStream().size();
		
		this.userInformation.writeToBuffer();
		int userInformationLength = this.userInformation.getStream().size();
		
		this.pduLength = protocolVersionLength;
		this.pduLength += 2;
		this.pduLength += calledAELength;
		this.pduLength += callingAELength;
		this.pduLength += 32;
		this.pduLength += applicationContextLength;
		this.pduLength += presentationContextLength;
		this.pduLength += userInformationLength;
		
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}

	private void pl(String s) { System.out.println(s); }
	
}
