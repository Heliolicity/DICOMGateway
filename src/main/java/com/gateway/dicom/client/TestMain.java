package com.gateway.dicom.client;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.gateway.dicom.entities.*;
import com.gateway.dicom.imagetypes.*;
import com.gateway.dicom.lib.*;
import com.gateway.dicom.protocols.*;

public class TestMain {

	public static void main (String args []) {
		
		Client client = null;
		boolean connected = false;
		FileInputStream fileInputStream;
		String host = null;
		
		try {
		
			host = args[0];
		
		}
		
		catch (ArrayIndexOutOfBoundsException aio) {
		
			pl("No IP address specified - default to localhost");
			host = "localhost";
			
		}
		
		if (host != null) {
			
			client = new Client(host, 5678);
			connected = client.connectToServer();
			pl("CONNECTION STATUS: " + connected);
			
			if (connected) {
				
				pl("CONNECTION SUCCESSFUL");
				
				try {
					
					boolean result = client.sendAssociateRequest();
					
					if (result) pl("A-ASSOCIATE-RQ Request Sent");
					else pl("A-ASSOCIATE-RQ NOT Sent");

					
					/*fileInputStream = new FileInputStream("IMG_2116.JPG");
					
					while (fileInputStream.available() > 0) {
						
						pl("" + fileInputStream.read());
						
					}*/
					
				}
				
				catch (Exception exc) {
					
					pl("EXCEPTION: " + exc.getMessage());
					exc.printStackTrace();
					
				}
				
			}
			
			else {
			
				pl ("ERROR CONNECTING");
				
			}
				
		}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
