package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

public class DicomByteOrder {
	
	public static final int BYTE_ORDERING_LITTLE_ENDIAN = 1;
	public static final int BYTE_ORDERING_BIG_ENDIAN = 2;
	protected int byteOrdering;
	
	public DicomByteOrder(int byteOrdering) {
		this.byteOrdering = byteOrdering;
	}
	
	public void setByteOrdering(int byteOrdering) {
		this.byteOrdering = byteOrdering;
	}
	
	public int getByteOrdering() {
		return this.byteOrdering;
	}
}
