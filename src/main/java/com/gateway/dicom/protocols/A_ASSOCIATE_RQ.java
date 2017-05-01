package com.gateway.dicom.protocols;

import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.UserInformation;

public class A_ASSOCIATE_RQ {

	private byte pduType = 0x01;
	private byte reserved = 0x00;
	private int pduLength; //4 bytes in length
	private int protocolVersion = 1; //2 bytes in length
	private String calledAE; //16 bytes in length
	private String callingAE; //16 bytes in length
	private ApplicationContext applicationContext;
	private PresentationContext_RQ presentationContext;
	private UserInformation userInformation;
	
	public A_ASSOCIATE_RQ(String calledAE, String callingAE, ApplicationContext applicationContext,
			PresentationContext_RQ presentationContext, UserInformation userInformation) {
		super();
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

	private void pl(String s) { System.out.println(s); }
	
}
