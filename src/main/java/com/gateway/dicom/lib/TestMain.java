package com.gateway.dicom.lib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.gateway.dicom.lib.DicomValueRepresentationOutputStream;
import com.gateway.dicom.imagetypes.CTImageGenerator;

public class TestMain {

	public static void main(String args []) throws IOException {
		
		FileInputStream fileInputStream = new FileInputStream("IMG_2116.JPG");
		
		while (true) {
			
			pl("" + fileInputStream.read());
			
			if (fileInputStream.available() == 0) {
				
				pl("Take that for data!");
				break;
				
			}
			
			else {
				
				pl("There's more data");
				
			}
			
		}
		
		try {
			
			DicomValueRepresentationOutputStream outputStream = 
					new DicomValueRepresentationOutputStream(new FileOutputStream("TEST.dcm"), 
					DicomValueRepresentationOutputStream.BYTE_ORDERING_LITTLE_ENDIAN);
		
			CTImageGenerator imageGenerator = new CTImageGenerator("1.3.372.12212354");
			
			//1. Preamble
			byte[] preamble = imageGenerator.getPreamble();
			
			for (int a = 0; a < preamble.length; a ++) outputStream.writeUInt8(preamble[a]);
			
			pl("DONE!");
			
		}
		
		catch (Exception exc) {
			
			pl("EXCEPTION: " + exc.getMessage());
			exc.printStackTrace();
			
		}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
