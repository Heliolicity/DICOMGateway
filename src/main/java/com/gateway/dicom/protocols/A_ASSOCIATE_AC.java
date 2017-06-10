package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_AC;
import com.gateway.dicom.entities.UserInformation;

public class A_ASSOCIATE_AC extends PDU {

	//private byte pduType;
	//private byte reserved = 0x00;
	//private int pduLength; //4 bytes in length
	private int protocolVersion = 0x0000; //2 bytes in length
	private ApplicationContext applicationContext;
	private PresentationContext_AC presentationContext;
	private UserInformation userInformation;
	private ByteArrayOutputStream stream;
	
	public A_ASSOCIATE_AC() {}
	
	public A_ASSOCIATE_AC(byte pduType, ApplicationContext applicationContext, 
			PresentationContext_AC presentationContext, UserInformation userInformation) {
		super();
		this.pduType = pduType;
		this.applicationContext = applicationContext;
		this.presentationContext = presentationContext;
		this.userInformation = userInformation;
	}

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

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public PresentationContext_AC getPresentationContext() {
		return presentationContext;
	}

	public void setPresentationContext(PresentationContext_AC presentationContext) {
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
	
}
