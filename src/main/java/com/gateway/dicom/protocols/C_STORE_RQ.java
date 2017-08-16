package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.entities.DataSet;
import com.gateway.dicom.lib.DicomOutputBuffer;


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
	private int length;
	private DicomOutputBuffer buffer;
	private int byteOrdering;
	
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

	public DataElement getCommandGroupLength() {
		return commandGroupLength;
	}

	public void setCommandGroupLength(DataElement commandGroupLength) {
		this.commandGroupLength = commandGroupLength;
	}

	public DataElement getAffectedSOPClassUID() {
		return affectedSOPClassUID;
	}

	public void setAffectedSOPClassUID(DataElement affectedSOPClassUID) {
		this.affectedSOPClassUID = affectedSOPClassUID;
	}

	public DataElement getCommandField() {
		return commandField;
	}

	public void setCommandField(DataElement commandField) {
		this.commandField = commandField;
	}

	public DataElement getMessageID() {
		return messageID;
	}

	public void setMessageID(DataElement messageID) {
		this.messageID = messageID;
	}

	public DataElement getPriority() {
		return priority;
	}

	public void setPriority(DataElement priority) {
		this.priority = priority;
	}

	public DataElement getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType(DataElement dataSetType) {
		this.dataSetType = dataSetType;
	}

	public DataElement getAffectedSOPClassInstanceUID() {
		return affectedSOPClassInstanceUID;
	}

	public void setAffectedSOPClassInstanceUID(DataElement affectedSOPClassInstanceUID) {
		this.affectedSOPClassInstanceUID = affectedSOPClassInstanceUID;
	}

	public DataElement getMoveOriginatorApplicationEntityTitle() {
		return moveOriginatorApplicationEntityTitle;
	}

	public void setMoveOriginatorApplicationEntityTitle(DataElement moveOriginatorApplicationEntityTitle) {
		this.moveOriginatorApplicationEntityTitle = moveOriginatorApplicationEntityTitle;
	}

	public DataElement getMoveOriginatorMessageID() {
		return moveOriginatorMessageID;
	}

	public void setMoveOriginatorMessageID(DataElement moveOriginatorMessageID) {
		this.moveOriginatorMessageID = moveOriginatorMessageID;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}

	public int getByteOrdering() {
		return byteOrdering;
	}

	public void setByteOrdering(int byteOrdering) {
		this.byteOrdering = byteOrdering;
	}
	
	public void writeToBuffer() {
		
		
		
	}
	
}
