package com.gateway.dicom.entities;

import com.gateway.dicom.lib.DicomOutputBuffer;
import com.gateway.dicom.protocols.C_ECHO_RQ;
import com.gateway.dicom.protocols.C_STORE_RQ;
import com.gateway.dicom.protocols.PDU;

public class PresentationDataValue extends DICOMItem {

	private int messageControlHeader;
	private int presentationContextID;
	private PDU pdvData;
	private DicomOutputBuffer buffer;
	private String dimse;
	
	public PresentationDataValue(int messageControlHeader, int presentationContextID, PDU pdvData, String dimse) {
		super();
		this.messageControlHeader = messageControlHeader;
		this.presentationContextID = presentationContextID;
		this.pdvData = pdvData;
		this.dimse = dimse;
	}
	
	public PresentationDataValue(int messageControlHeader, int presentationContextID, PDU pdvData) {
		super();
		this.messageControlHeader = messageControlHeader;
		this.presentationContextID = presentationContextID;
		this.pdvData = pdvData;
	}
	
	public PresentationDataValue() {}

	public int getMessageControlHeader() {
		return messageControlHeader;
	}

	public void setMessageControlHeader(int messageControlHeader) {
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
	
	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}

	public String getDimse() {
		return dimse;
	}

	public void setDimse(String dimse) {
		this.dimse = dimse;
	}

	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			pl("PRESENTATION CONTEXT ID: " + this.presentationContextID);
			this.buffer.writeUInt8(this.presentationContextID);
			
			C_ECHO_RQ echo;
			C_STORE_RQ store;
			
			switch(this.dimse) {
			
				case "C-ECHO" : echo = (C_ECHO_RQ) this.pdvData;
					pl("COMMAND: " + echo.getCommand());
					this.buffer.writeUInt8(echo.getCommand());
					pl(this.dimse);
					pl("GROUP NUMBER");
					this.buffer.writeUInt16(echo.getCommandGroupLength().getGroupNumber());
					pl("ELEMENT NUMBER");
					this.buffer.writeUInt16(echo.getCommandGroupLength().getElementNumber());
					//int a = Integer.parseInt(echo.getCommandField().getElementData());
					//this.buffer.writeUInt32(a);
					pl("ELEMENT LENGTH");
					echo.writeToBuffer();
					pl("SHOULD BE: " + echo.getCommandGroupLength().getIntElementData());
					//this.buffer.writeUInt32(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, echo.getCommandGroupLength().getIntElementData());
					this.buffer.write(echo.getBuffer().toByteArray());
					this.itemLength = this.buffer.size();
					//this.buffer.writeUInt32(this.itemLength);
					break;
				case "C-STORE" : break;
				case "C-FIND" : break;
				default : break;
			
			}
				
			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}


}
