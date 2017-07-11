package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.DataSet;


public class C_STORE_RQ extends PDU {

	private DataElement commandGroupLength;
	private DataElement affectedSOPClassUID;
	private DataElement commandField;
	private DataElement messageID;
	private DataElement priority;
	private DataElement dataSetType;
	private DataElement affectedSOPClassInstanceUID;
	private DataElement moveOriginatorApplicationEntityTitle;
	private DataElement moveOriginatorMessageID;
	private DataSet dataSet;
	
	public C_STORE_RQ(String sopUID, short id, short dst, String origAE, String origMessageID, DataSet dataSet) {
		
		super();
		
		this.affectedSOPClassUID = new DataElement();
		this.affectedSOPClassUID.setGroupNumber(0x0000);
		this.affectedSOPClassUID.setElementNumber(0x0002);
		this.affectedSOPClassUID.setValueRepresentation("UI");
		this.affectedSOPClassUID.setElementData(sopUID);
		
		this.commandField = new DataElement();
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		this.commandField.setElementData("0001");
		
		
		
	}
	
	public C_STORE_RQ() {}
	
}
