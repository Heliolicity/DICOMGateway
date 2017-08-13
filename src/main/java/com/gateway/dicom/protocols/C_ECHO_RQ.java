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
	private ByteArrayOutputStream stream;
	private int length;
	private DicomOutputBuffer buffer;
	
	public C_ECHO_RQ(short id) {
		
		this.affectedServiceClassUID = new DataElement();
		this.affectedServiceClassUID.setGroupNumber(0x0000);
		this.affectedServiceClassUID.setElementNumber(0x0002);
		this.affectedServiceClassUID.setValueRepresentation("UI");
		this.affectedServiceClassUID.setElementData("1.2.840.10008.1.1");
		
		this.commandField = new DataElement();
		this.commandField.setGroupNumber(0x0000);
		this.commandField.setElementNumber(0x0100);
		this.commandField.setValueRepresentation("US");
		this.commandField.setElementData("0030");
		
		this.messageID = new DataElement();
		this.messageID.setGroupNumber(0x0000);
		this.messageID.setElementNumber(0x0110);
		this.messageID.setValueRepresentation("US");
		this.messageID.setElementData("" + id);
		
		this.dataSetType = new DataElement();
		this.dataSetType.setGroupNumber(0x0000);
		this.dataSetType.setElementNumber(0x0800);
		this.dataSetType.setValueRepresentation("US");
		this.dataSetType.setElementData("0101");
		
		this.writeToBuffer();
		this.length = this.stream.size();
		this.clearBuffer();
		
		this.commandGroupLength = new DataElement();
		this.commandGroupLength.setGroupNumber(0x0000);
		this.commandGroupLength.setElementNumber(0x0000);
		this.commandGroupLength.setValueRepresentation("UL");
		
		try {
		
			this.stream.write(this.commandGroupLength.getValueRepresentation().getBytes());
			this.stream.write(this.length);
			this.length += this.stream.size();
			this.commandGroupLength.setElementData("" + this.length);
			this.clearBuffer();
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void writeToStream() {
		
		try {
		
			this.stream = new ByteArrayOutputStream();
			
			this.stream.write(this.affectedServiceClassUID.getGroupNumber());
			this.stream.write(this.affectedServiceClassUID.getElementNumber());
			this.stream.write(this.affectedServiceClassUID.getValueRepresentation().getBytes());
			this.stream.write(this.affectedServiceClassUID.getElementData().getBytes());
			
			this.stream.write(this.commandField.getGroupNumber());
			this.stream.write(this.commandField.getElementNumber());
			this.stream.write(this.commandField.getValueRepresentation().getBytes());
			this.stream.write(this.commandField.getElementData().getBytes());
			
			this.stream.write(this.messageID.getGroupNumber());
			this.stream.write(this.messageID.getElementNumber());
			this.stream.write(this.messageID.getValueRepresentation().getBytes());
			this.stream.write(this.messageID.getElementData().getBytes());
			
			this.stream.write(this.dataSetType.getGroupNumber());
			this.stream.write(this.dataSetType.getElementNumber());
			this.stream.write(this.dataSetType.getValueRepresentation().getBytes());
			this.stream.write(this.dataSetType.getElementData().getBytes());
			
			
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void clearStream() { if (! this.stream.equals(null)) this.stream.reset(); }
	
	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			
				
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}

		
	}
	
	public void clearBuffer() {
		
	}
	
	private void pl(String s) { System.out.print(s); }
	
}
