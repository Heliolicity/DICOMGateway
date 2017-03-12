package com.gateway.dicom.server;

import java.io.*;
import java.net.*;

public class Server implements Runnable {

	private ServerSocket serverSocket;
	private int port;
	private Socket socket;
    private Connection[] connections;
    private boolean active;
    private int numberOfConnections;
    
    public Server(ServerSocket serverSocket, Socket socket, int port, int numberOfConnections) {
		
    	super();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.port = port;
		//this.active = false;
		this.numberOfConnections = numberOfConnections;
		this.connections = new Connection[this.numberOfConnections];
		
		try { 
			
		    this.serverSocket = new ServerSocket(this.port);
	        this.port = this.serverSocket.getLocalPort();
		    this.active = true;
		    this.run();
		    
	    }
		
		catch (IOException ioe) {  

			pl(ioe.getMessage());
			this.active = false;
			
		}
		
	}
    
	public Server(ServerSocket serverSocket, Socket socket, int port) {
		
		super();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.port = port;
		this.active = false;
		this.numberOfConnections = 10;
		this.connections = new Connection[this.numberOfConnections];
		
		try { 
			
		    this.serverSocket = new ServerSocket(this.port);
	        this.port = this.serverSocket.getLocalPort();
		    this.active = true;
		    this.run();
		    
	    }
		
		catch (IOException ioe) {  

			pl(ioe.getMessage());
			this.active = false;
			
		}
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while (this.active) {
			
			try {
			
				this.serverSocket.accept();
				
			}
			
			catch (IOException ioe) {  

				pl(ioe.getMessage());
				this.active = false;
				
			}
			
		}
		
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Connection[] getConnections() {
		return connections;
	}

	public void setConnections(Connection[] connections) {
		this.connections = connections;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getNumberOfConnections() {
		return numberOfConnections;
	}

	public void setNumberOfConnections(int numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}
	
    private void pl(String s) { System.out.println(s); }
	
}
