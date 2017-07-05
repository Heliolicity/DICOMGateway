package com.gateway.dicom.client;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.gateway.dicom.entities.*;
import com.gateway.dicom.imagetypes.*;
import com.gateway.dicom.lib.*;
import com.gateway.dicom.protocols.*;
import com.gateway.dicom.server.*;
import com.gateway.dicom.test.TCPServer;

public class TestMain {

	public static void main (String args []) {
		
		Client client = null;
		boolean connected = false;
		FileInputStream fileInputStream;
		String host = null;
		Server server = null;
		Socket socket = null;
		ServerSocket serverSocket;
        
		try {
		
			host = args[0];
		
		}
		
		catch (ArrayIndexOutOfBoundsException aio) {
		
			pl("No IP address specified - default to localhost");
			host = "localhost";
			
		}
		
		if (host != null) {

			try {
				
				//client = new Client(host, 5050);
				client = new Client(host, 5678);
				connected = client.connectToServer();
				
				pl("CONNECTION STATUS: " + connected);
				
				if (connected) {
					
					pl("CONNECTION SUCCESSFUL");
						
												
						//fileInputStream = new FileInputStream("IMG_2116.JPG");
						
						//while (fileInputStream.available() > 0) {
							
							//pl("" + fileInputStream.read());
							
						//}
					
				}
				
				else {
				
					pl ("ERROR CONNECTING");
					
				}
				
			
			}
			
			catch (Exception exc) {
				
				pl("EXCEPTION: " + exc.getMessage());
				exc.printStackTrace();
				
			}
			
		}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
