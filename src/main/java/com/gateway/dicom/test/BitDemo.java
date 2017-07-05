package com.gateway.dicom.test;

public class BitDemo {
    
	public static void main(String[] args) {
		
		int a = 252;
		pl(Integer.toBinaryString(a));
		a = ~a;
		pl(Integer.toBinaryString(a));
		
		int b0 = a & 0x00ff;
		int b1 = (a & 0xff00) >> 16;
		pl(Integer.toBinaryString(b0));
		pl(Integer.toBinaryString(b1));
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
