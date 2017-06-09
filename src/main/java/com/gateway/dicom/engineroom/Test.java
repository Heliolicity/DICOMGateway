package com.gateway.dicom.engineroom;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.gateway.dicom.client.Client;
import com.gateway.dicom.server.Server;

public class Test {

	public static void main(String args[]) {
		
		Client client = null;
		Engine engine = null;
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
				engine = new Engine(client);
				engine.run();
			
			}
			
			catch (Exception exc) {
				
				pl("EXCEPTION: " + exc.getMessage());
				exc.printStackTrace();
				
			}
			
		}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
