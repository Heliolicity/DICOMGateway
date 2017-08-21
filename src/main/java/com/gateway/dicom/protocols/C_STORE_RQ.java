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
	private DataElement moveOriginatorApplicationEntityTitle = null;
	private DataElement moveOriginatorMessageID = null;
	private DataSet dataSet;
	private int length;
	private DicomOutputBuffer buffer;
	private int byteOrdering = 0;
	private int command = 3;
	
	public C_STORE_RQ(int byteOrdering, String sopUID, int id, String origAE, int origMessageID, int pri, String sopInstanceUID) {
		
		super();
		
		this.byteOrdering = byteOrdering;
		
		this.affectedSOPClassUID = new DataElement();
		this.affectedSOPClassUID.setGroupNumber(0x0000);
		this.affectedSOPClassUID.setElementNumber(0x0002);
		this.affectedSOPClassUID.setValueRepresentation("UI");
		this.affectedSOPClassUID.setElementData(sopUID);
		
		this.commandField = new DataElement();
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		this.commandField.setIntElementData(0x0001);
		
		this.messageID = new DataElement();
		this.messageID.setGroupNumber(0x0000);
		this.messageID.setElementNumber(0x0110);
		this.messageID.setValueRepresentation("US");
		this.messageID.setIntElementData(id);
		
		this.priority = new DataElement();
		this.priority.setGroupNumber(0x0000);
		this.priority.setElementNumber(0x0700);
		this.priority.setValueRepresentation("US");
		this.priority.setIntElementData(pri);
		
		this.dataSetType = new DataElement();
		this.dataSetType.setGroupNumber(0x0000);
		this.dataSetType.setElementNumber(0x0800);
		this.dataSetType.setValueRepresentation("US");
		this.dataSetType.setIntElementData(258);
		
		this.affectedSOPClassInstanceUID = new DataElement();
		this.affectedSOPClassInstanceUID.setGroupNumber(0x0000);
		this.affectedSOPClassInstanceUID.setElementNumber(0x1000);
		this.affectedSOPClassInstanceUID.setValueRepresentation("UI");
		this.affectedSOPClassInstanceUID.setElementData(sopInstanceUID);
		
		this.moveOriginatorApplicationEntityTitle = new DataElement();
		this.moveOriginatorApplicationEntityTitle.setGroupNumber(0x0000);
		this.moveOriginatorApplicationEntityTitle.setElementNumber(0x1030);
		this.moveOriginatorApplicationEntityTitle.setValueRepresentation("AE");
		this.moveOriginatorApplicationEntityTitle.setElementData(origAE);
		
		this.moveOriginatorMessageID = new DataElement();
		this.moveOriginatorMessageID.setGroupNumber(0x0000);
		this.moveOriginatorMessageID.setElementNumber(0x1031);
		this.moveOriginatorMessageID.setValueRepresentation("US");
		this.moveOriginatorMessageID.setIntElementData(origMessageID);
		
		this.commandGroupLength = new DataElement();
		this.commandGroupLength.setByteOrder(this.byteOrdering);
		this.commandGroupLength.setGroupNumber(0x0000);
		this.commandGroupLength.setElementNumber(0x0000);
		this.commandGroupLength.setValueRepresentation("UL");
		this.commandGroupLength.setElementLength(4);
		
	}
	
	public C_STORE_RQ(int byteOrdering, String sopUID, int id, int pri, String sopInstanceUID) {
		
		super();
		
		this.byteOrdering = byteOrdering;
		
		this.affectedSOPClassUID = new DataElement();
		this.affectedSOPClassUID.setGroupNumber(0x0000);
		this.affectedSOPClassUID.setElementNumber(0x0002);
		this.affectedSOPClassUID.setValueRepresentation("UI");
		this.affectedSOPClassUID.setElementData(sopUID);
		
		this.commandField = new DataElement();
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		this.commandField.setIntElementData(0x0001);
		this.commandField.setElementLength(2);
		
		this.messageID = new DataElement();
		this.messageID.setGroupNumber(0x0000);
		this.messageID.setElementNumber(0x0110);
		this.messageID.setValueRepresentation("US");
		this.messageID.setIntElementData(id);
		this.messageID.setElementLength(2);
		
		this.priority = new DataElement();
		this.priority.setGroupNumber(0x0000);
		this.priority.setElementNumber(0x0700);
		this.priority.setValueRepresentation("US");
		this.priority.setIntElementData(pri);
		this.priority.setElementLength(2);
		
		this.dataSetType = new DataElement();
		this.dataSetType.setGroupNumber(0x0000);
		this.dataSetType.setElementNumber(0x0800);
		this.dataSetType.setValueRepresentation("US");
		this.dataSetType.setIntElementData(258);
		this.dataSetType.setElementLength(2);
		
		this.affectedSOPClassInstanceUID = new DataElement();
		this.affectedSOPClassInstanceUID.setGroupNumber(0x0000);
		this.affectedSOPClassInstanceUID.setElementNumber(0x1000);
		this.affectedSOPClassInstanceUID.setValueRepresentation("UI");
		this.affectedSOPClassInstanceUID.setElementData(sopInstanceUID);
		
		this.commandGroupLength = new DataElement();
		this.commandGroupLength.setByteOrder(this.byteOrdering);
		this.commandGroupLength.setGroupNumber(0x0000);
		this.commandGroupLength.setElementNumber(0x0000);
		this.commandGroupLength.setValueRepresentation("UL");
		this.commandGroupLength.setElementLength(4);
		
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
	
	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public void writeToBuffer() {
		
		try {
			
			if (this.byteOrdering == 1) this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			else if (this.byteOrdering == 2) this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN);
			else this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			
			this.buffer.writeUInt16(this.affectedSOPClassUID.getGroupNumber());
			this.buffer.writeUInt16(this.affectedSOPClassUID.getElementNumber());
			this.affectedSOPClassUID.writeToBuffer();
			int a = this.affectedSOPClassUID.getBuffer().size();
			this.affectedSOPClassUID.setElementLength(a);
			byte[] arr = this.affectedSOPClassUID.getElementData().getBytes();
			
			if (! (a % 2 == 0)) {
			
				byte[] arr1 = new byte[arr.length + 1];
				
				for (int b = 0; b < arr.length; b ++)
					
					arr1[b] = arr[b];
					
				arr1[arr1.length - 1] = 0;
				
				a ++;
			
				this.affectedSOPClassUID.setElementLength(this.getAffectedSOPClassUID().getElementLength() + 1);
				this.buffer.writeUInt32(a);
				this.buffer.write(arr1);
				
			}
			
			else {
				
				this.buffer.writeUInt32(a);
				this.buffer.write(arr);
				
			}
			
			//pl("Affected SOP Class Group Number: " + this.affectedSOPClassUID.getGroupNumber());
			//pl("Affected SOP Class Element Number: " + this.affectedSOPClassUID.getElementNumber());
			//pl("Affected SOP Class Element Length: " + this.affectedSOPClassUID.getElementLength());
			//pl("Affected SOP Class Element Data: " + this.affectedSOPClassUID.getElementData());
			
			this.buffer.writeUInt16(this.commandField.getGroupNumber());
			this.buffer.writeUInt16(this.commandField.getElementNumber());
			this.buffer.writeUInt32(this.commandField.getElementLength());
			this.buffer.writeUInt16(this.commandField.getIntElementData());
			
			//pl("Command Field Group Number: " + this.commandField.getGroupNumber());
			//pl("Command Field Element Number: " + this.commandField.getElementNumber());
			//pl("Command Field Element Length: " + this.commandField.getElementLength());
			//pl("Command Field Element Data: " + this.commandField.getIntElementData());
			
			this.buffer.writeUInt16(this.messageID.getGroupNumber());
			this.buffer.writeUInt16(this.messageID.getElementNumber());
			this.buffer.writeUInt32(this.messageID.getElementLength());
			this.buffer.writeUInt16(this.messageID.getIntElementData());
		
			this.buffer.writeUInt16(this.priority.getGroupNumber());
			this.buffer.writeUInt16(this.priority.getElementNumber());
			this.buffer.writeUInt32(this.priority.getElementLength());
			this.buffer.writeUInt16(this.priority.getIntElementData());
			
			//pl("Priority Group Number: " + this.priority.getGroupNumber());
			//pl("Priority Element Number: " + this.priority.getElementNumber());
			//pl("Priority Element Length: " + this.priority.getElementLength());
			//pl("Priority Element Data: " + this.priority.getIntElementData());
			
			this.buffer.writeUInt16(this.dataSetType.getGroupNumber());
			this.buffer.writeUInt16(this.dataSetType.getElementNumber());
			this.buffer.writeUInt32(this.dataSetType.getElementLength());
			this.buffer.writeUInt16(this.dataSetType.getIntElementData());
			//this.buffer.writeUInt16(258);
			
			this.buffer.writeUInt16(this.affectedSOPClassInstanceUID.getGroupNumber());
			this.buffer.writeUInt16(this.affectedSOPClassInstanceUID.getElementNumber());
			this.affectedSOPClassInstanceUID.writeToBuffer();
			a = this.affectedSOPClassInstanceUID.getBuffer().size();
			this.affectedSOPClassInstanceUID.setElementLength(a);
			arr = this.affectedSOPClassInstanceUID.getElementData().getBytes();
			
			if (! (a % 2 == 0)) {
			
				byte[] arr1 = new byte[arr.length + 1];
				
				for (int b = 0; b < arr.length; b ++)
					
					arr1[b] = arr[b];
					
				arr1[arr1.length - 1] = 0;
				
				a ++;
				this.affectedSOPClassInstanceUID.setElementLength(this.affectedSOPClassInstanceUID.getElementLength() + 1);
				
				this.buffer.writeUInt32(a);
				this.buffer.write(arr1);
				
			}
			
			else {
				
				this.buffer.writeUInt32(a);
				this.buffer.write(arr);
				
			}
			
			//pl("Affected SOP Class Instance UID Group Number: " + this.affectedSOPClassInstanceUID.getGroupNumber());
			//pl("Affected SOP Class Instance UID Element Number: " + this.affectedSOPClassInstanceUID.getElementNumber());
			//pl("Affected SOP Class Instance UID Element Length: " + this.affectedSOPClassInstanceUID.getElementLength());
			//pl("Affected SOP Class Instance UID Element Data: " + this.affectedSOPClassInstanceUID.getElementData());
			
			/*
			this.buffer.writeUInt16(this.moveOriginatorApplicationEntityTitle.getGroupNumber());
			this.buffer.writeUInt16(this.moveOriginatorApplicationEntityTitle.getElementNumber());
			this.moveOriginatorApplicationEntityTitle.writeToBuffer();
			a = this.moveOriginatorApplicationEntityTitle.getBuffer().size();
			arr = this.moveOriginatorApplicationEntityTitle.getElementData().getBytes();
			
			if (! (a % 2 == 0)) {
			
				byte[] arr1 = new byte[arr.length + 1];
				
				for (int b = 0; b < arr.length; b ++)
					
					arr1[b] = arr[b];
					
				arr1[arr1.length - 1] = 0;
				
				a ++;
			
				this.buffer.writeUInt32(a);
				this.buffer.write(arr1);
				
			}
			
			else {
				
				this.buffer.writeUInt32(a);
				this.buffer.write(arr);
				
			}
			
			this.buffer.writeUInt16(this.moveOriginatorMessageID.getGroupNumber());
			this.buffer.writeUInt16(this.moveOriginatorMessageID.getElementNumber());
			this.buffer.writeUInt32(this.moveOriginatorMessageID.getElementLength());
			this.buffer.writeUInt16(this.moveOriginatorMessageID.getIntElementData());
			*/
			
			//pl("");
			//pl("Now check buffer");
			
			/*arr = this.buffer.toByteArray();
			
			for (int b = 0; b < arr.length; b ++) 
				
				p("" + arr[b]);
			*/			
			int size = this.buffer.size();
			
			if (size % 2 != 0) size ++; 
				
			this.commandGroupLength.setIntElementData(size);
			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void p(String s) { System.out.print(s); }
	
	private void pl(String s) { System.out.println(s); }

	
}
