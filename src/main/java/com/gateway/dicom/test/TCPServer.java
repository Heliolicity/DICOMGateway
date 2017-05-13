package com.gateway.dicom.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {

    final static int TCP_SERVER_PORT = 9701;
    private Socket socket;

    public TCPServer(Socket sock) {
        socket = sock;
    }

    public void run() {
        System.out.println(this.socket.getPort() + " working or sleeping for 5 seconds");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInputStream clientinp;
        DataOutputStream clientout;

        try {
            clientinp = new DataInputStream(socket.getInputStream());
            clientout = new DataOutputStream(socket.getOutputStream());

            while (true) {
                System.out.println("reading...");
                String sentence = clientinp.readUTF();
                System.out.printf("read: %s", sentence);
                clientout.writeUTF(String.format("answer: %s", sentence));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws IOException {
        ServerSocket serversocket;
        serversocket = new ServerSocket(TCP_SERVER_PORT);
        while (true) {
            Socket clientsocket = serversocket.accept();
            new TCPServer(clientsocket).start();
        }
    }
}