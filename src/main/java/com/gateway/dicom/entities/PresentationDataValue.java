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
			this.buffer.writeUInt8(this.presentationContextID);
			pl("Presentation Context: " + this.presentationContextID);
			
			C_ECHO_RQ echo;
			C_STORE_RQ store;
			
			switch(this.dimse) {
			
				case "C-ECHO" : echo = (C_ECHO_RQ) this.pdvData;
					this.buffer.writeUInt8(echo.getCommand());
					this.buffer.writeUInt16(echo.getCommandGroupLength().getGroupNumber());
					this.buffer.writeUInt16(echo.getCommandGroupLength().getElementNumber());
					this.buffer.writeUInt32(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, echo.getCommandGroupLength().getElementLength());
					echo.writeToBuffer();
					this.buffer.writeUInt32(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, echo.getCommandGroupLength().getIntElementData());
					this.buffer.write(echo.getBuffer().toByteArray());
					this.itemLength = this.buffer.size();
					
					break;
					
				case "C-STORE" : store = (C_STORE_RQ) this.pdvData;
					pl("Command: " + store.getCommand());
					this.buffer.writeUInt8(store.getCommand());
					pl("Command Group Length Group Number: " + store.getCommandGroupLength().getGroupNumber());
					this.buffer.writeUInt16(store.getCommandGroupLength().getGroupNumber());
					pl("Command Group Length Element Number: " + store.getCommandGroupLength().getElementNumber());
					this.buffer.writeUInt16(store.getCommandGroupLength().getElementNumber());
					pl("Command Group Length Element Length: " + store.getCommandGroupLength().getElementLength());
					this.buffer.writeUInt32(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, store.getCommandGroupLength().getElementLength());
					store.writeToBuffer();
					pl("Command Group Length Element Data: " + store.getCommandGroupLength().getIntElementData());
					this.buffer.writeUInt32(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN, store.getCommandGroupLength().getIntElementData());
					this.buffer.write(store.getBuffer().toByteArray());
					byte[] arr = store.getBuffer().toByteArray();
					
					/*for (int a = 0; a < arr.length; a ++)
						
						pl("" + arr[a]);
					*/
					this.itemLength = this.buffer.size();
					
					break;
					
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
