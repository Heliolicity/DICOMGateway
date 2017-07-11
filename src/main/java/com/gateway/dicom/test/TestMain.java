package com.gateway.dicom.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestMain {

	public static void main (String args []) {
		
		Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
		String srcDir = "";
		String dstDir = "C:\\temp\\";
		String srcFile = "";
		String dstFile = "";
		
		for (Path name: dirs) {
		
			pl(name.toString());
			srcDir = name.toString();
			
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(name, "*.{png}")) {
			    
				for (Path file: stream) {
					
					pl("\t" + file.getFileName().toString());
					srcFile = file.getFileName().toString();
					dstFile = srcFile;
					File afile = new File(srcDir + srcFile);
			        boolean b = afile.renameTo(new File(dstDir + afile.getName()));
					
			    }
				
			} 
			
			catch (IOException | DirectoryIteratorException x) {
			    // IOException can never be thrown by the iteration.
			    // In this snippet, it can only be thrown by newDirectoryStream.
			    //System.err.println(x);
			}
			
						
			//getFiles(name);
			
		}
		
	}
	
	private static void getFiles(Path name) {
		
		pl(name.getFileName().toString());
		
		try (DirectoryStream<Path> stream =
			     Files.newDirectoryStream(name, "*.{png}")) {
			    for (Path entry: stream) {
			        
			    	
			    	System.out.println(entry.getFileName());
			    	//File afile = new File("C:\\temp\\Afile.txt");
			        //boolean b = afile.renameTo(new File("C:\\folderB\\" + afile.getName()));
			    	
			    }
			} catch (IOException x) {
			    // IOException can never be thrown by the iteration.
			    // In this snippet, it can // only be thrown by newDirectoryStream.
			    System.err.println(x);
			}
		
	}
	
	private static void pl(String s) { System.out.println(s); }
	
}
