package com.gateway.dicom.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

	private long port;
	private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
	
	public Connection(long port, ObjectInputStream inputStream, ObjectOutputStream outputStream, Socket socket) {
		super();
		this.port = port;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.socket = socket;
	}

	public Connection() { super(); }
	
}
