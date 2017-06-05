package com.gateway.dicom.server;

/*
 * Based on code provided by David Molloy
 * 
 * */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private BufferedReader keyboardInput;
	private int port;

	public Server() {}
	
	public Server(int port) { this.port = port; }
	
	public void run() {
		
		pl("Setting up Server");
		
		try {
			
			this.serverSocket = new ServerSocket(this.port);
			pl("Server Socket established");
			this.clientSocket = this.serverSocket.accept();
			pl("Client Socket accepted");
			this.inputStream = new DataInputStream(this.clientSocket.getInputStream());
			pl("Input Stream established");
			this.outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
			pl("Output Stream established");
			this.keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			pl("Keyboard Input established");
			
			String input = "";
			String output = "";
			int bytes;
			int byteCount = 0;
			
			pl("Listening for Client communications");
				
			while (true) {
				
				bytes = this.inputStream.readByte();
				pl("Received this from Client: " + bytes);
				
				byteCount ++;
				pl("Number of bytes received so far: " + byteCount);
				
				
			}
			
		}
		
		catch (IOException ioe) {
			
			//pl("IOExceptiom");
			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
		finally {
			
			try {
			
				this.inputStream.close();
				pl("Server Data Input Stream closed");
				this.outputStream.close();
				pl("Server Data Output Stream closed");
				this.clientSocket.close();
				pl("Server Client Socket closed");
				this.serverSocket.close();
				pl("Server Socket closed");
			
			}

			catch (IOException ioe) {
				
				pl(ioe.getMessage());
				ioe.printStackTrace();
				
			}

		}
		
		
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public DataInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(DataInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(DataOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public BufferedReader getKeyboardInput() {
		return keyboardInput;
	}

	public void setKeyboardInput(BufferedReader keyboardInput) {
		this.keyboardInput = keyboardInput;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }
	
}

/*public class Server {

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
            e.printStackTrace();
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
                e.printStackTrace();
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
        	e.printStackTrace();
        	
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
	
}*/
