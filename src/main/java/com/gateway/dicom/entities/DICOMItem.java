package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.gateway.dicom.lib.DicomOutputBuffer;

public class DICOMItem {

	protected byte itemType;
	protected byte reserved = 0x00;
	protected int itemLength;
	protected ByteArrayOutputStream stream;
	protected DicomOutputBuffer buffer;
	
	public DICOMItem(byte itemType, byte reserved, int itemLength) {
		super();
		this.itemType = itemType;
		this.reserved = reserved;
		this.itemLength = itemLength;
	}
	
	public DICOMItem() {
		super();
	}

	public byte getItemType() {
		return itemType;
	}

	public void setItemType(byte itemType) {
		this.itemType = itemType;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	public int getItemLength() {
		return itemLength;
	}

	public void setItemLength(int itemLength) {
		this.itemLength = itemLength;
	}
	
	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public void setStream(ByteArrayOutputStream stream) {
		this.stream = stream;
	}

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}

	public int convertDecToHex(int dec) {
		
		String hex = Integer.toHexString(dec);
		return Integer.parseInt(hex);
		
	}
	
	public int convertDecToBin(int dec) {
		
		String bin = Integer.toBinaryString(dec);
		return Integer.parseInt(bin);
		
	}
	
	public void writeToStream() {
		
		this.stream = new ByteArrayOutputStream();
		this.stream.write(this.itemType);
		this.stream.write(this.reserved);
		
	}
	
	public void clearStream() { if (! this.stream.equals(null)) this.stream.reset(); }
	
	public void writeToBuffer() throws IOException {
		
		this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
		this.buffer.writeUInt8(this.itemType);
		this.buffer.writeUInt8(this.reserved);
		
	}
	
	public void clearBuffer() {
		
		if (! (this.buffer.equals(null)))
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
		
	}
	
	protected void pl(String s) { System.out.println(s); }
	
}
