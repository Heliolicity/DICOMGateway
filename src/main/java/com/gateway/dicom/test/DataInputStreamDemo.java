package com.gateway.dicom.test;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DataInputStreamDemo {

   public static void main(String[] args) throws IOException {
      InputStream is = null;
      DataInputStream dis = null;
      
      try {
      
         // create input stream from file input stream
         is = new FileInputStream("c:\\temp\\test.csv");
         
         // create data input stream
         dis = new DataInputStream(is);
         
         // count the available bytes form the input stream
         int count = is.available();
         
         // create buffer
         byte[] bs = new byte[count];
         
         // read data into buffer
         dis.read(bs);
         
         // for each byte in the buffer
         for (byte b:bs) {
         
            // convert byte into character
            char c = (char)b;
            
            // print the character
            System.out.print(c+" ");
         }
         
      } catch(Exception e) {
      
         // if any I/O error occurs
         e.printStackTrace();
      } finally {
         
         // releases any associated system files with this stream
         if(is!=null)
            is.close();
         if(dis!=null)
            dis.close();
      }   
   }
}
