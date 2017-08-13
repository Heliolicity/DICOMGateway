package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.gateway.dicom.entities.ApplicationContext;
import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.UserInformation;
import com.gateway.dicom.lib.DicomOutputBuffer;

public class A_ASSOCIATE_RQ extends PDU {

	//private byte pduType;
	//private byte reserved = 0x00;
	//private int pduLength; //4 bytes in length
	private int protocolVersion = 1; //2 bytes in length
	private String calledAE; //16 bytes in length
	private String callingAE; //16 bytes in length
	private ApplicationContext applicationContext;
	private List<PresentationContext_RQ> presentationContexts;
	private UserInformation userInformation;
	private ByteArrayOutputStream stream;
	private DicomOutputBuffer buffer;
	
	public A_ASSOCIATE_RQ(byte pduType, int protocolVersion, String calledAE, String callingAE, ApplicationContext applicationContext,
			List<PresentationContext_RQ> presentationContexts, UserInformation userInformation) {
		super();
		this.pduType = pduType;
		this.protocolVersion = protocolVersion;
		this.calledAE = calledAE;
		this.callingAE = callingAE;
		this.applicationContext = applicationContext;
		this.presentationContexts = presentationContexts;
		this.userInformation = userInformation;
	}

	public A_ASSOCIATE_RQ(String calledAE, String callingAE) {
		super();
		this.calledAE = calledAE;
		this.callingAE = callingAE;
	}
		
	public A_ASSOCIATE_RQ() { super(); }

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

	public List<PresentationContext_RQ> getPresentationContexts() {
		return presentationContexts;
	}

	public void setPresentationContexts(List<PresentationContext_RQ> presentationContexts) {
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

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}

	public void calculateLength() {
		
		//L = 2 (Protocol Version) + 2 (Reserved) + 16 (Called AE Title) + 
		//16 (Calling AE Title) + 32 (Reserved) + L of (Application Context) + 
		//L of (Presentation Contexts) + L of (User Information)
		
		int protocolVersionLength = 2;
		byte[] bytes = this.calledAE.getBytes();
		int calledAELength = bytes.length;
		bytes = this.callingAE.getBytes();
		int callingAELength = bytes.length;
		
		this.applicationContext.writeToBuffer();
		int applicationContextLength = this.applicationContext.getBuffer().size();
		
		int presentationContextLength = 0;
		
		for (PresentationContext_RQ presentationContext : this.presentationContexts) {
		
			presentationContext.writeToBuffer();
			presentationContextLength += presentationContext.getBuffer().size();
		
		}
		
		this.userInformation.writeToBuffer();
		int userInformationLength = this.userInformation.getBuffer().size();
		
		this.pduLength = protocolVersionLength;
		this.pduLength += 2;
		this.pduLength += calledAELength;
		this.pduLength += callingAELength;
		this.pduLength += 32;
		this.pduLength += applicationContextLength;
		this.pduLength += presentationContextLength;
		this.pduLength += userInformationLength;
		
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			//this.buffer.writeUInt16(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, this.protocolVersion);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt8(this.protocolVersion);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeStringWithTrailingSpaces(this.calledAE, 16);
			this.buffer.writeStringWithTrailingSpaces(this.callingAE, 16);
			
			for (int a = 0; a < 32; a ++) 
				
				this.buffer.writeUInt8(this.reserved);
			
			this.applicationContext.writeToBuffer();
			this.buffer.write(this.applicationContext.getBuffer().toByteArray());
			
			for (PresentationContext_RQ presentationContext : this.presentationContexts) {
			
				presentationContext.writeToBuffer();
				this.buffer.write(presentationContext.getBuffer().toByteArray());
				
			}
			
			this.userInformation.writeToBuffer();
			this.buffer.write(this.userInformation.getBuffer().toByteArray());
		
			this.pduLength = this.buffer.size();
			
			/*DicomOutputBuffer test = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			test = this.buffer;
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.pduType);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt32(this.pduLength);
			this.buffer.write(test.toByteArray());*/
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}

	private void pl(String s) { System.out.println(s); }
	
}
