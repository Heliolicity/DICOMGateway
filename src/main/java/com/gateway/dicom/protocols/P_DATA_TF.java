package com.gateway.dicom.protocols;

import java.util.List;
import java.util.ArrayList;

import com.gateway.dicom.entities.PresentationContext_RQ;
import com.gateway.dicom.entities.PresentationDataValue;
import com.gateway.dicom.lib.DicomOutputBuffer;

public class P_DATA_TF extends PDU {

	private List<PresentationDataValue> presentationDataValueItems;
	private DicomOutputBuffer buffer;
	
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

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
