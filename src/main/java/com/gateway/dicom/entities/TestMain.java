package com.gateway.dicom.entities;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MessageControlHeader test = new MessageControlHeader(1, 0);
		//pl("BYTE: " + test.header);
		
		byte b = 1;
		//pl("" + b);
		
		int value = 8;
		
		int b0 = value & 0x00ff;
		int b1 = (value & 0xff00) >> 8;
		
		pl("" + b0);
		pl("" + b1);
		
	}
	
	private static void pl(String s) { System.out.println(s); }

}
