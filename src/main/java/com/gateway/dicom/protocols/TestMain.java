package com.gateway.dicom.protocols;

public class TestMain {

	public static void main (String args []) {
		
		//A_ASSOCIATE_RQ request = new A_ASSOCIATE_RQ("1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1", "1.2.840.10008.3.1.1.1");
		
		int i = 21;
				String hex = Integer.toHexString(i);
				System.out.println("Hex value is " + hex);
	
				String hexNumber = "21";
						int decimal = Integer.parseInt(hexNumber, 16);
						System.out.println("Hex value is " + decimal);
				
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
