package com.gateway.dicom.imagetypes;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DicomImageGenerator {

	private final String prefix = "DICM";
	private String filePath = null;
	private int width;
	private int height;
	private int[][] pixelData;
	private RandomAccessFile file;
	private ByteArrayOutputStream stream;
	
	public DicomImageGenerator(String filePath) {
		this.filePath = filePath;
	}
	
	public DicomImageGenerator() {}
	
	public void readFile() {
		
		try {
			
			if (this.height > 0 && this.width > 0 && this.filePath != null) {
			
				this.pixelData = new int[this.height][this.width];
				this.file = new RandomAccessFile(this.filePath, "r");
				
				for(int y = 0; y < this.height; y ++)
					
					for(int x = 0; x < this.width; x ++)
						
						this.pixelData[x][y] = this.file.readShort();
				
			
			}
			
			else {
				
				pl("Error generating pixel data.  Possible reasons:\n\nNo file path specified\nImage height not specified\nImage width not specified");
				
			}
			
		}
		
		catch (FileNotFoundException fnfe) {
			
			pl(this.filePath + " not found. Make sure it is in the root directory of the project");
			fnfe.printStackTrace();
			
		}
		
		catch (IOException ioe) {
		
			pl("Error reading file");
			ioe.printStackTrace();
			
		}
		
	}
	
	public void writeToStream() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			
			for(int y = 0; y < this.height; y ++)
				
				for(int x = 0; x < this.width; x ++)
					
					this.stream.write(this.pixelData[y][x]);
			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public byte[] getPreamble() {
		
		byte[] preamble = new byte[128];

		for (int a = 0; a < preamble.length; a ++) 
			
			preamble[a] = 0x0000;
		
		return preamble;
		
	}
	
	public String getPrefix() { return this.prefix; }

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[][] getPixelData() {
		return pixelData;
	}

	public void setPixelData(int[][] pixelData) {
		this.pixelData = pixelData;
	}

	public RandomAccessFile getFile() {
		return file;
	}

	public void setFile(RandomAccessFile file) {
		this.file = file;
	}
	
	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public void setStream(ByteArrayOutputStream stream) {
		this.stream = stream;
	}

	private void p(String s) { System.out.print(s); }
	
	private void pl(String s) { System.out.println(s); }
	
}
