package com.gateway.dicom.usb;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.joda.time.DateTime;

public class USBReader {

	private Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
	private String srcDir = "";
	private String dstDir = "C:\\temp\\";
	private String srcFile = "";
	private String dstFile = "";
	private String imageType = "";
	
	public USBReader(Iterable<Path> dirs, String srcDir, String dstDir, String srcFile, String dstFile, String imageType) {
		super();
		this.dirs = dirs;
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.srcFile = srcFile;
		this.dstFile = dstFile;
		this.imageType = imageType;
	}

	public USBReader() {}
	
	public void run () {
		
		this.dirs = FileSystems.getDefault().getRootDirectories();
		
		for (Path name: this.dirs) {
		
			pl(name.toString());
			this.srcDir = name.toString();
			
			pl("Scanning connected USB device at: " + this.systemTimeInDate(System.currentTimeMillis()));
			
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(name, "*.{png,jpg,dcm}")) {
			/*try (DirectoryStream<Path> stream = 
					Files.newDirectoryStream(name, 
							"*.{" + this.imageType + "}")) 
			{*/
			    
				for (Path file: stream) {
					
					pl("\t" + file.getFileName().toString());
					this.srcFile = file.getFileName().toString();
					this.dstFile = this.srcFile;
					File afile = new File(this.srcDir + this.srcFile);
					pl("Transferring file at: " + this.systemTimeInDate(System.currentTimeMillis()));
			        boolean b = afile.renameTo(new File(this.dstDir + afile.getName()));
			        pl("Transferred file at: " + this.systemTimeInDate(System.currentTimeMillis()));
				
			        if (! b) 
			        	
			        	pl("There was an error moving file: " + this.dstDir + afile.getName());
			        
			    }
				
			} 
			
			catch (Exception e) {

				pl(e.getMessage());
				e.printStackTrace();
			
			}
			
					
		}
		
	}
		
	public Iterable<Path> getDirs() {
		return dirs;
	}

	public void setDirs(Iterable<Path> dirs) {
		this.dirs = dirs;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getDstDir() {
		return dstDir;
	}

	public void setDstDir(String dstDir) {
		this.dstDir = dstDir;
	}

	public String getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(String srcFile) {
		this.srcFile = srcFile;
	}

	public String getDstFile() {
		return dstFile;
	}

	public void setDstFile(String dstFile) {
		this.dstFile = dstFile;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	private String systemTimeInDate(long ms) {
		
		DateTime date = new DateTime(ms);
		String strDate = date.toString();
		return strDate;
		
	}
	
	private void pl(String s) { System.out.println(s); }
	
}
