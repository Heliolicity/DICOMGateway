package com.gateway.dicom.protocols;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.lib.DicomOutputBuffer;

public class C_ECHO_RQ extends PDU implements Serializable {

	private static final long serialVersionUID = -3427299234912651720L;
	private DataElement commandGroupLength;
	private DataElement affectedServiceClassUID;
	private DataElement commandField;
	private DataElement messageID;
	private DataElement dataSetType;
	private String sopID;
	private ByteArrayOutputStream stream;
	private int length;
	private DicomOutputBuffer buffer;
	private int byteOrdering;
	private int command = 3;

	public C_ECHO_RQ(int id, String sopID) {
		
		this.sopID = sopID;
		
		this.affectedServiceClassUID = new DataElement();
		this.affectedServiceClassUID.setByteOrder(this.byteOrdering);
		this.affectedServiceClassUID.setGroupNumber(0x0000);
		this.affectedServiceClassUID.setElementNumber(0x0002);
		this.affectedServiceClassUID.setValueRepresentation("UI");
		this.affectedServiceClassUID.setElementData(this.sopID);
		this.affectedServiceClassUID.writeToBuffer();
		
		this.commandField = new DataElement();
		this.commandField.setByteOrder(this.byteOrdering);
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		//this.commandField.setElementData("0030");
		this.commandField.setIntElementData(0x0030);
		this.commandField.setElementLength(2);
		//this.commandField.writeToBuffer();
		
		this.messageID = new DataElement();
		this.messageID.setByteOrder(this.byteOrdering);
		this.messageID.setGroupNumber(0x0000);
		this.messageID.setElementNumber(0x0110);
		this.messageID.setValueRepresentation("US");
		//this.messageID.setElementData("" + id);
		//this.messageID.setShrElementData(id);
		this.messageID.setIntElementData(id);
		this.messageID.setElementLength(2);
		//this.messageID.writeToBuffer();
		
		this.dataSetType = new DataElement();
		this.dataSetType.setByteOrder(this.byteOrdering);
		this.dataSetType.setGroupNumber(0x0000);
		this.dataSetType.setElementNumber(0x0800);
		this.dataSetType.setValueRepresentation("US");
		//this.dataSetType.setElementData("0101");
		this.dataSetType.setIntElementData(0x0101);
		this.dataSetType.setElementLength(2);
		//this.dataSetType.writeToBuffer();
		
		this.commandGroupLength = new DataElement();
		this.commandGroupLength.setByteOrder(this.byteOrdering);
		this.commandGroupLength.setGroupNumber(0x0000);
		this.commandGroupLength.setElementNumber(0x0000);
		this.commandGroupLength.setValueRepresentation("UL");
		this.commandGroupLength.setElementLength(4);
		
	} 

	
	public C_ECHO_RQ(int id) {
		
		this.affectedServiceClassUID = new DataElement();
		this.affectedServiceClassUID.setByteOrder(this.byteOrdering);
		this.affectedServiceClassUID.setGroupNumber(0x0000);
		this.affectedServiceClassUID.setElementNumber(0x0002);
		this.affectedServiceClassUID.setValueRepresentation("UI");
		this.affectedServiceClassUID.setElementData("1.2.840.10008.1.1");
		this.affectedServiceClassUID.writeToBuffer();
		
		this.commandField = new DataElement();
		this.commandField.setByteOrder(this.byteOrdering);
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		//this.commandField.setElementData("0030");
		this.commandField.setIntElementData(0x0030);
		this.commandField.setElementLength(2);
		//this.commandField.writeToBuffer();
		
		this.messageID = new DataElement();
		this.messageID.setByteOrder(this.byteOrdering);
		this.messageID.setGroupNumber(0x0000);
		this.messageID.setElementNumber(0x0110);
		this.messageID.setValueRepresentation("US");
		//this.messageID.setElementData("" + id);
		//this.messageID.setShrElementData(id);
		pl("MESSAGE ID: " + id);
		this.messageID.setIntElementData(id);
		this.messageID.setElementLength(2);
		//this.messageID.writeToBuffer();
		
		this.dataSetType = new DataElement();
		this.dataSetType.setByteOrder(this.byteOrdering);
		this.dataSetType.setGroupNumber(0x0000);
		this.dataSetType.setElementNumber(0x0800);
		this.dataSetType.setValueRepresentation("US");
		//this.dataSetType.setElementData("0101");
		this.dataSetType.setIntElementData(0x0101);
		this.dataSetType.setElementLength(2);
		//this.dataSetType.writeToBuffer();
		
		this.commandGroupLength = new DataElement();
		this.commandGroupLength.setByteOrder(this.byteOrdering);
		this.commandGroupLength.setGroupNumber(0x0000);
		this.commandGroupLength.setElementNumber(0x0000);
		this.commandGroupLength.setValueRepresentation("UL");
		this.commandGroupLength.setElementLength(4);
		
	} 
	
	public C_ECHO_RQ() {}
	
	public DataElement getCommandGroupLength() {
		return commandGroupLength;
	}

	public void setCommandGroupLength(DataElement commandGroupLength) {
		this.commandGroupLength = commandGroupLength;
	}

	public DataElement getAffectedServiceClassUID() {
		return affectedServiceClassUID;
	}

	public void setAffectedServiceClassUID(DataElement affectedServiceClassUID) {
		this.affectedServiceClassUID = affectedServiceClassUID;
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

	public DataElement getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType(DataElement dataSetType) {
		this.dataSetType = dataSetType;
	}

	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public void setStream(ByteArrayOutputStream stream) {
		this.stream = stream;
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

	public String getSopID() {
		return sopID;
	}


	public void setSopID(String sopID) {
		this.sopID = sopID;
	}


	public int getCommand() {
		return command;
	}


	public void setCommand(int command) {
		this.command = command;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void writeToStream() {
		
		try {
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void clearStream() { if (! this.stream.equals(null)) this.stream.reset(); }
	
	public void writeToBuffer() {
		
		try {
			
			if (this.byteOrdering == 1) this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			else this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN);
			
			this.buffer.writeUInt16(this.affectedServiceClassUID.getGroupNumber());
			this.buffer.writeUInt16(this.affectedServiceClassUID.getElementNumber());
			this.affectedServiceClassUID.writeToBuffer();
			int a = this.affectedServiceClassUID.getBuffer().size();
			byte[] arr = this.affectedServiceClassUID.getElementData().getBytes();
			
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
			
			this.buffer.writeUInt16(this.commandField.getGroupNumber());
			this.buffer.writeUInt16(this.commandField.getElementNumber());
			this.buffer.writeUInt32(this.commandField.getElementLength());
			this.buffer.writeUInt16(this.commandField.getIntElementData());
			
			this.buffer.writeUInt16(this.messageID.getGroupNumber());
			this.buffer.writeUInt16(this.messageID.getElementNumber());
			this.buffer.writeUInt32(this.messageID.getElementLength());
			this.buffer.writeUInt16(this.messageID.getIntElementData());
			
			this.buffer.writeUInt16(this.dataSetType.getGroupNumber());
			this.buffer.writeUInt16(this.dataSetType.getElementNumber());
			this.buffer.writeUInt32(this.dataSetType.getElementLength());
			this.buffer.writeUInt16(this.dataSetType.getIntElementData());
			
			this.commandGroupLength.setIntElementData(this.buffer.size());
				
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}

		
	}
	
	public void clearBuffer() {
		
	}
	
	private void p(String s) { System.out.print(s); }
	
	private void pl(String s) { System.out.println(s); }
	
}
