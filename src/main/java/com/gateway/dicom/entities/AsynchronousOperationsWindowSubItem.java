package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class AsynchronousOperationsWindowSubItem extends DICOMItem {

	protected int maximumNumberOperationsInvoked;
	protected int maximumNumberOperationsPerformed;
	
	public AsynchronousOperationsWindowSubItem(byte itemType, 
			int maximumNumberOperationsInvoked, 
			int maximumNumberOperationsPerformed) {
		
		super();
		this.itemType = itemType;
		this.itemLength = 0x00000004;
		//this.itemLength = this.convertDecToBin(this.itemLength);
		this.maximumNumberOperationsInvoked = maximumNumberOperationsInvoked;
		this.maximumNumberOperationsPerformed = maximumNumberOperationsPerformed;
		
	}
	
	public AsynchronousOperationsWindowSubItem() { super(); }

	public int getMaximumNumberOperationsInvoked() {
		return maximumNumberOperationsInvoked;
	}

	public void setMaximumNumberOperationsInvoked(int maximumNumberOperationsInvoked) {
		this.maximumNumberOperationsInvoked = maximumNumberOperationsInvoked;
	}

	public int getMaximumNumberOperationsPerformed() {
		return maximumNumberOperationsPerformed;
	}

	public void setMaximumNumberOperationsPerformed(int maximumNumberOperationsPerformed) {
		this.maximumNumberOperationsPerformed = maximumNumberOperationsPerformed;
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.maximumNumberOperationsInvoked);
			this.stream.write(this.maximumNumberOperationsPerformed);
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void writeToBuffer() {

		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.itemType);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt16(this.itemLength);
			this.buffer.writeUInt16(this.maximumNumberOperationsInvoked);
			this.buffer.writeUInt16(this.maximumNumberOperationsPerformed);
		
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
