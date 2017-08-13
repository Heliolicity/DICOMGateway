package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputStream;

public class DicomOutputBuffer extends DicomOutputStream {

	public DicomOutputBuffer(int byteOrdering) {
		super(new ByteArrayOutputStream(), byteOrdering);
	}
	
	public int size() {
		return ((ByteArrayOutputStream)outputStream).size();
	}
	
	public byte[] toByteArray() {
		return ((ByteArrayOutputStream)outputStream).toByteArray();
	}
}
