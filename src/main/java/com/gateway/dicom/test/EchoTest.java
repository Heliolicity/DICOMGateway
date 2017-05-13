package com.gateway.dicom.test;

import java.io.*;
import java.net.*;

public class EchoTest {
    public static void main(String[] args) {
        Socket echoSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
	DataInputStream stdIn = new DataInputStream(System.in);

        try {
            echoSocket = new Socket("localhost", 7);
            os = new DataOutputStream(echoSocket.getOutputStream());
            is = new DataInputStream(echoSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis");
            e.printStackTrace();
        }

        if (echoSocket != null && os != null && is != null) {
            try {
                String userInput;

                while ((userInput = stdIn.readLine()) != null) {
                    os.writeBytes(userInput);
                    os.writeByte('\n');
                    System.out.println("echo: " + is.readLine());
                }
                os.close();
                is.close();
                echoSocket.close();
            } catch (IOException e) {
                System.err.println("I/O failed on the connection to: taranis");
            }
        }
    }
}