package com.gateway.dicom.engineroom;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.gateway.dicom.client.Client;
import com.gateway.dicom.imagetypes.DicomImageGenerator;
import com.gateway.dicom.server.Server;

public class TestMain {

	private static Client client = null;
	private static Engine engine = null;
	private static DicomImageGenerator generator = null;
	
	public static void main(String args[]) {
		
		boolean connected = false;
		FileInputStream fileInputStream;
		String host = null;
		Server server = null;
		Socket socket = null;
		ServerSocket serverSocket;
        Scanner keyboard = null;
		String input = "";
		int num = 0;
        
		try {
		
			host = args[0];
		
		}
		
		catch (ArrayIndexOutOfBoundsException aio) {
		
			pl("No IP address specified - default to localhost");
			host = "localhost";
			
		}
		
		if (host != null) {

			try {
				
				keyboard = new Scanner(System.in);
				
				//client = new Client(host, 5050);
				client = new Client(host, 5678);
				//client = new Client(host, 104);
				//host = "192.168.1.81";
				//client = new Client(host, 4242);
				generator = new DicomImageGenerator("CTData.raw");
				engine = new Engine(client, generator);
				engine.run();
				
				/*pl("Do you wish to connect?");
				input = keyboard.nextLine();
				
				if (input.equals("Y")) {
				
					engine.connect();
				
					sendCommands();
					
				}*/
				
				
				/*
				engine.connect();
				engine.buildAbortRequest();
				engine.sendAbortRequest();
				*/
			
			}
			
			catch (Exception exc) {
				
				pl("EXCEPTION: " + exc.getMessage());
				exc.printStackTrace();
				
			}
			
		}
		
	}
	
	private static void sendCommands() {
		
		Scanner keyboard;
		String input = "";
		int num = 0;
		byte[] receivedData = null;
		
		keyboard = new Scanner(System.in);
		
		pl("What DICOM PDU do you wish to send?");
		num = keyboard.nextInt();
		
		switch(num) {
		
			case 1: engine.buildAssociateRequest();
			engine.sendAssociateRequest();
			engine.receive();
			receivedData = engine.getReceivedData();
			
			for (int a = 0; a < receivedData.length; a ++) pl("arr[" + a + "]: " + receivedData[a]);
			
			sendCommands(); 
			break;
			
			case 2: engine.buildReleaseRequest();
			engine.sendReleaseRequest();
			engine.receive();
			receivedData = engine.getReceivedData();
			
			for (int a = 0; a < receivedData.length; a ++) pl("arr[" + a + "]: " + receivedData[a]);
			
			sendCommands(); 
			break;
			
			default: break;
		
		}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
