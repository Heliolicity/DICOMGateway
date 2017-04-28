package com.gateway.dicom.test;

public class TestMain {

	public static void main (String args []) {
		
		String hexNumber = "0030H";
		int decimal = Integer.parseInt(hexNumber, 16);
		System.out.println("Hex value is " + decimal);
		
		int test = 0x0030;
		
	}
	
}
