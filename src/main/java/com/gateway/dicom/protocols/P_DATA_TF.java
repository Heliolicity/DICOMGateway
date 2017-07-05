package com.gateway.dicom.protocols;

import java.util.List;
import java.util.ArrayList;

import com.gateway.dicom.entities.PresentationDataValue;

public class P_DATA_TF extends PDU {

	private List<PresentationDataValue> presentationDataValueItems;
	
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
	
}
