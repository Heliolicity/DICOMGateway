package com.gateway.dicom.protocols;

import java.util.List;
import java.util.ArrayList;

import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.PresentationDataValue;
import com.gateway.dicom.lib.DicomOutputBuffer;

public class P_DATA_TF extends PDU {

	private List<PresentationDataValue> presentationDataValueItems;
	private DicomOutputBuffer buffer;
	private String dimse;
	private int byteOrder;
	
	public P_DATA_TF(byte pduType, List<PresentationDataValue> presentationDataValueItems, String dimse) {
		super();
		this.pduType = pduType;
		this.presentationDataValueItems = presentationDataValueItems;
		this.dimse = dimse;
	}
	
	public P_DATA_TF(byte pduType, List<PresentationDataValue> presentationDataValueItems) {
		super();
		this.pduType = pduType;
		this.presentationDataValueItems = presentationDataValueItems;
	}
	
	public P_DATA_TF(List<PresentationDataValue> presentationDataValueItems) {
		super();
		this.presentationDataValueItems = presentationDataValueItems;
	}
	
	public P_DATA_TF() {}

	public List<PresentationDataValue> getPresentationDataValueItems() {
		return presentationDataValueItems;
	}

	public void setPresentationDataValueItems(List<PresentationDataValue> presentationDataValueItems) {
		this.presentationDataValueItems = presentationDataValueItems;
	}

	public String getDimse() {
		return dimse;
	}

	public void setDimse(String dimse) {
		this.dimse = dimse;
	}

	public int getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(int byteOrder) {
		this.byteOrder = byteOrder;
	}

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}
	
	public void writeToBuffer() {
		
		this.pduLength = 0;
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			//this.buffer.writeUInt8(this.pduType);
			//this.buffer.writeUInt8(this.reserved);
			
			for (PresentationDataValue pdv : this.presentationDataValueItems) {
				
				pdv.writeToBuffer();
				this.pduLength += pdv.getBuffer().size();
				
			}
			
			this.buffer.writeUInt32(this.pduLength);
			
			for (PresentationDataValue pdv : this.presentationDataValueItems) 
				
				this.buffer.write(pdv.getBuffer().toByteArray());
				
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
