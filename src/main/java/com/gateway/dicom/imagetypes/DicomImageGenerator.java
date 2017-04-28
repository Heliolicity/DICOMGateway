package com.gateway.dicom.imagetypes;

public class DicomImageGenerator {

	private final String prefix = "DICM";

	public DicomImageGenerator() {}
	
	public byte[] getPreamble() {
		
		byte[] preamble = new byte[128];

		for (int a = 0; a < preamble.length; a ++) 
			
			preamble[a] = 0x0000;
		
		return preamble;
		
	}
	
	public String getPrefix() { return this.prefix; }
	
}
