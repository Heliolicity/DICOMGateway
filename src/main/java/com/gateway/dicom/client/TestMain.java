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
		
		/*Client client = null;
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
					
					boolean result = client.sendAssociationRequest();
					
					boolean result = client.sendEchoRequest();
					
					if (result) pl("Echo Request Sent");
					else pl("Echo Request NOT Sent");
					
					fileInputStream = new FileInputStream("IMG_2116.JPG");
					
					while (fileInputStream.available() > 0) {
						
						pl("" + fileInputStream.read());
						
					}
					
				}
				
				catch (Exception exc) {
					
					pl("EXCEPTION: " + exc.getMessage());
					exc.printStackTrace();
					
				}
				
			}
			
			else {
			
				pl ("ERROR CONNECTING");
				
			}
				
		}*/
		
		try {
        	
    		//Create a byte stream for re-use
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		byte[] arr;
    		
    		//Build Application Context
    		ApplicationContext applicationContext = new ApplicationContext("1.2.840.10008.3.1.1.1");
    		arr = applicationContext.getApplicationContextName().getBytes();
    		applicationContext.setItemLength(arr.length);
    		
    		//Build User Information
    		UserInformation userInformation = new UserInformation("1.2.840.10008.3.1.1.1");
    		arr = userInformation.getUserData().getBytes();
    		userInformation.setItemLength(arr.length);
    		
    		//Build Transfer Syntax
    		TransferSyntax transferSyntax = new TransferSyntax("1.2.840.10008.3.1.1.1");
    		arr = transferSyntax.getTransferSyntaxName().getBytes();
    		transferSyntax.setItemLength(arr.length);
    		
    		//Build Abstract Syntax
    		AbstractSyntax abstractSyntax = new AbstractSyntax("1.2.840.10008.3.1.1.1");
    		arr = abstractSyntax.getAbstractSyntaxName().getBytes();
    		abstractSyntax.setItemLength(arr.length);
    		
    		byte a = applicationContext.getItemType();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		a = applicationContext.getReserved();
    		stream.write(a);
    		pl("BUFFER SIZE: " + stream.size());
    		int b = applicationContext.getItemType();
    		stream.write(b);
    		pl("BUFFER SIZE: " + stream.size());
    		arr = applicationContext.getApplicationContextName().getBytes();
    		stream.write(arr);
    		pl("BUFFER SIZE: " + stream.size());
        
        } 
    	
        catch (Exception e) {   
        	
        	pl("EXCEPTION: " + e.getMessage());
        	e.printStackTrace();
            
        }
    
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
