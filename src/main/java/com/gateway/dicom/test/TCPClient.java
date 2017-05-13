package com.gateway.dicom.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    public TCPClient() {
    }

    public static void main(String args[]) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", 9701);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        boolean stop = false;
        while (!stop) {
            System.out.println("client->server: hello...");
            output.writeUTF("hello");

            System.out.println("client: waiting...");
            String response = input.readUTF();
            System.out.printf("client: got response: %s\n", response);
        }
        socket.close();
    }
}