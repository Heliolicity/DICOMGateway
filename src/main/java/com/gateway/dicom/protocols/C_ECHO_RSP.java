package com.gateway.dicom.protocols;

import java.io.Serializable;

import com.gateway.dicom.entities.DataElement;

public class C_ECHO_RSP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3148689171189928901L;
	private DataElement commandGroupLength;
	private DataElement affectedSOPClassUID;
	private DataElement commandField;
	private DataElement messageIDBeingRespondedTo;
	private DataElement commandDataSetType;
	private DataElement status;
	private int elementLength = 0;
	
	public C_ECHO_RSP(DataElement messageIDBeingRespondedTo) {
		
		super();
		//this.commandGroupLength = new DataElement(0x0000, 0x0000, "UL", 1, "");
		this.affectedSOPClassUID = new DataElement(0x0000, 0x0002, "UI", 1, "1.2.840.10008.1.1");
		this.elementLength = this.affectedSOPClassUID.getElementLength();
		this.commandField = new DataElement(0x0000, 0x0100, "US", 1, "0030H");
		this.elementLength += this.commandField.getElementLength();
		long mid = new Long(messageIDBeingRespondedTo.getElementData()).longValue();
		this.messageIDBeingRespondedTo = new DataElement(0x0000, 0x0120, "US", 1, "" + mid);
		this.elementLength += this.messageIDBeingRespondedTo.getElementLength();
		this.commandDataSetType = new DataElement(0x0800, 0x0100, "US", 1, "0101H");
		this.elementLength += this.commandDataSetType.getElementLength();
		this.status = new DataElement(0x0000, 0x0900, "US", 1, "1");
		this.elementLength += this.status.getElementLength();
		this.commandGroupLength = new DataElement(0x0000, 0x0000, "UL", 1, "" + this.elementLength);
		
	}

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

	public DataElement getMessageIDBeingRespondedTo() {
		return messageIDBeingRespondedTo;
	}

	public void setMessageIDBeingRespondedTo(DataElement messageIDBeingRespondedTo) {
		this.messageIDBeingRespondedTo = messageIDBeingRespondedTo;
	}

	public DataElement getCommandDataSetType() {
		return commandDataSetType;
	}

	public void setCommandDataSetType(DataElement commandDataSetType) {
		this.commandDataSetType = commandDataSetType;
	}

	public DataElement getStatus() {
		return status;
	}

	public void setStatus(DataElement status) {
		this.status = status;
	}

	public int getElementLength() {
		return elementLength;
	}

	public void setElementLength(int elementLength) {
		this.elementLength = elementLength;
	}
	
	@Override
	public String toString() {
		
		return this.commandGroupLength.toString() + 
				this.affectedSOPClassUID.toString() + 
				this.commandField.toString() + 
				this.messageIDBeingRespondedTo.toString() + 
				this.commandDataSetType.toString() + 
				this.status.toString();
		
	}
	
}
