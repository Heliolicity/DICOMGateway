package com.gateway.dicom.test;

public class TestMain {

	public static void main (String args []) {
		
		int dec = 24;
		String hex = Integer.toHexString(dec);
		System.out.println(hex);
		int test = Integer.parseInt(hex);
		System.out.println(test);
		
		String binaryIntInStr = Integer.toBinaryString(8);
		System.out.println(binaryIntInStr);
		test = Integer.parseInt(binaryIntInStr);
		System.out.println(test);
		
	}
	
}
