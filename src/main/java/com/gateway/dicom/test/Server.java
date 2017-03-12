package com.gateway.dicom.test;

/*
 * Based on code provided by David Molloy
 * 
 * */

import java.net.*;
import java.io.*;

public class Server {

	private int port;
	private String ipAddress;
	private boolean active;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ConnectionHandler connectionHandler;
	
	public Server() {}

	public Server(int port, String ipAddress, boolean active, ServerSocket serverSocket) {
		
		super();
		this.port = port;
		this.ipAddress = ipAddress;
		this.active = active;
		this.serverSocket = serverSocket;
	
	}
	
	public void activate() {
		
		this.active = true;
		
		try {
            
			this.serverSocket = new ServerSocket(this.port);
            pl("Server has started listening on port: " + this.port);
            
        } 
		
        catch (IOException e) {
        	
            pl("Cannot listen on port: " + this.port);
            pl(e.getMessage());
            System.exit(1);
            
        }
		
		while (this.active) {
			
			try {
				
            	pl("**. Listening for a connection...");
                this.clientSocket = this.serverSocket.accept();
                pl("00. <- Accepted socket connection from a client: ");
                pl("    <- with address: " + this.clientSocket.getInetAddress().toString());
                pl("    <- and port number: " + this.clientSocket.getPort());
                
            } 
			
            catch (IOException e) {
            	
                pl("XX. Accept failed: " + this.port + e);
                this.active = false;   
                
            }
			
			this.connectionHandler = new ConnectionHandler(this.clientSocket);
            this.connectionHandler.start(); 
            pl("02. -- Finished communicating with client:" + clientSocket.getInetAddress().toString());
			
		}
		
		try {
			
            pl("04. -- Closing down the server socket gracefully.");
            this.serverSocket.close();
            
        } 
		
        catch (IOException e) {
            
        	System.err.println("XX. Could not close server socket. " + e.getMessage());
        
        }
		
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }
	
}
