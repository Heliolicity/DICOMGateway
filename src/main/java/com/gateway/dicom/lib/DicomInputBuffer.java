package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DicomInputBuffer extends DicomInputStream {

	public DicomInputBuffer(byte[] bytes, int byteOrdering) {
		super(new ByteArrayInputStream(bytes), byteOrdering);
	}
	
	/*public int available() throws IOException {
		return inputStream.available();
	}*/
	
	public boolean hasData() throws IOException {
		if(inputStream.available() == 0)
			return false;
		else 
			return true;
	}
}
