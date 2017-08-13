package com.gateway.dicom.lib;

/*
 * Author:	Robert Sadleir
 * 
 * */

import java.io.IOException;

import com.gateway.dicom.lib.DicomOutputStream;

public interface Writable {
	public void write(DicomOutputStream outputStream) throws IOException;
}
