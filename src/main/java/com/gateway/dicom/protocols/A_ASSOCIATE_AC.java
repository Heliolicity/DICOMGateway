package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_AC;
import com.gateway.dicom.entities.UserInformation;

public class A_ASSOCIATE_AC extends PDU {

	//private byte pduType;
	//private byte reserved = 0x00;
	//private int pduLength; //4 bytes in length
	private int protocolVersion = 0x0000; //2 bytes in length
	private ApplicationContext applicationContext;
	private List<PresentationContext_AC> presentationContexts;
	private UserInformation userInformation;
	private ByteArrayOutputStream stream;
	
	public A_ASSOCIATE_AC() {}
	
	public A_ASSOCIATE_AC(byte pduType, ApplicationContext applicationContext, 
			List<PresentationContext_AC> presentationContexts, UserInformation userInformation) {
		super();
		this.pduType = pduType;
		this.applicationContext = applicationContext;
		this.presentationContexts = presentationContexts;
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

	public List<PresentationContext_AC> getPresentationContexts() {
		return presentationContexts;
	}

	public void setPresentationContexts(List<PresentationContext_AC> presentationContexts) {
		this.presentationContexts = presentationContexts;
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
