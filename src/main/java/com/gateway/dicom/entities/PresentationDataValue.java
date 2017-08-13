package com.gateway.dicom.entities;

import com.gateway.dicom.lib.DicomOutputBuffer;
import com.gateway.dicom.protocols.PDU;

public class PresentationDataValue extends DICOMItem {

	private MessageControlHeader messageControlHeader;
	private int presentationContextID;
	private PDU pdvData;
	private DicomOutputBuffer buffer;
	
	public PresentationDataValue(MessageControlHeader messageControlHeader, int presentationContextID, PDU pdvData) {
		super();
		this.messageControlHeader = messageControlHeader;
		this.presentationContextID = presentationContextID;
		this.pdvData = pdvData;
	}
	
	public PresentationDataValue() {}

	public MessageControlHeader getMessageControlHeader() {
		return messageControlHeader;
	}

	public void setMessageControlHeader(MessageControlHeader messageControlHeader) {
		this.messageControlHeader = messageControlHeader;
	}

	public int getPresentationContextID() {
		return presentationContextID;
	}

	public void setPresentationContextID(int presentationContextID) {
		this.presentationContextID = presentationContextID;
	}

	public PDU getPdvData() {
		return pdvData;
	}

	public void setPdvData(PDU pdvData) {
		this.pdvData = pdvData;
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.presentationContextID);
			
			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}


}
