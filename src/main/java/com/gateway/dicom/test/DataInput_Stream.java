package com.gateway.dicom.test;

import java.io.*;
public class DataInput_Stream {

   public static void main(String args[])throws IOException {

      // writing string to a file encoded as modified UTF-8
      DataOutputStream dataOut = new DataOutputStream(new FileOutputStream("C:\\temp\\Test.csv"));
      dataOut.writeUTF("hello");

      // Reading data from the same file
      DataInputStream dataIn = new DataInputStream(new FileInputStream("C:\\temp\\Test.csv"));

      while(dataIn.available()>0) {
         String k = dataIn.readUTF();
         System.out.print(k+" ");
      }
   }
}