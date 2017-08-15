package com.gateway.dicom.entities;

import java.util.Date;

import com.gateway.dicom.lib.DicomOutputBuffer;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class DataElement extends DICOMItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891696901455168226L;
	private int groupNumber;
	private int elementNumber;
	private String valueRepresentation;
	private int valueLength;
	private String elementData;
	private short shrElementData;
	private int intElementData;
	private int[] metaInfo;
	private String uniqueIdentifier;
	private int elementLength;
	private Date theDate;
	private DicomOutputBuffer buffer;
	private int byteOrder;
	
	public DataElement() { super(); }

	public DataElement(int groupNumber, int elementNumber) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
	}
	
	public DataElement(int groupNumber, int elementNumber, String elementData) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
		this.elementData = elementData;
	}
	
	public DataElement(int groupNumber, int elementNumber, short shrElementData) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
		this.shrElementData = shrElementData;
	}
	
	public DataElement(int groupNumber, int elementNumber, String valueRepresentation, int valueLength, String elementData) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
		this.valueRepresentation = valueRepresentation;
		this.valueLength = valueLength;
		this.elementData = elementData;
	}

	public DataElement(int groupNumber, int elementNumber, String valueRepresentation, int valueLength, short shrElementData) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
		this.valueRepresentation = valueRepresentation;
		this.valueLength = valueLength;
		this.shrElementData = shrElementData;
	}
	
	public DataElement(int groupNumber, int elementNumber, String valueRepresentation, int valueLength, int intElementData) {
		super();
		this.groupNumber = groupNumber;
		this.elementNumber = elementNumber;
		this.valueRepresentation = valueRepresentation;
		this.valueLength = valueLength;
		this.intElementData = intElementData;
	}
	
	public String generateUniqueIdentifier(String uid) {
		
		String sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
		String month = sdf.substring(0, 2);
		String day = sdf.substring(3, 5);
		String year = sdf.substring(6, 10);
		String hour = sdf.substring(11, 13);
		String minute = sdf.substring(14, 16);
		String second = sdf.substring(17, 19);
		
		this.uniqueIdentifier = uid + "." + year +
				"." + month + 
				"." + day + 
				"." + hour + 
				"." + minute + 
				"." + second;
		return this.uniqueIdentifier;
		
	}
	
	public String getCurrentTime() {
		
		String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return currentTime;
		
	}
	
	/*public int determineElementLength(int c) {
		
		int totalLength = 0;
		int glen = 2;	// Length of group number - 2 bytes
		int elen = 2;	// Length of element number - 2 bytes
		int vrlen = 2;	// Length of value representation - 2 bytes
		int vlen = 0;	// An even 16/32 bit number - 2 or 4 bytes
		int plen = 0;	// Padding byte in case of a VR of "OB" type
		int elmlen = 0;	// Length of element data - need to determine this
		
		//UL:	group (2), element (2), vr (2), vlen (2), data (whatever)
		//OB:	group (2), element (2), vr (2), pad(2), vlen (4), data (whatever - 1 byte for each element of the array)
		//UI:	group (2), element (2), vr (2), vlen(2), data (whatever)
		
		boolean check;
		//int dlen = 0;
		byte[] arr;
		
		switch(c) {
		
			case 0 :	//elementData 
				
				check = this.elementData == null;
				
				if (check == false) {
				
					arr = this.elementData.getBytes();
					elmlen = arr.length;
					
					if (this.valueRepresentation.equals("OB")) {
						
						vlen = 4;
						plen = 2;
						
					}
					
					else {
						
						vlen = 2;
						plen = 0;
						
					}
					
				}
				
				else {

					//What if it's a short, not a String?
					
				}
				
				break;
			case 1 : 	//metaInfo
				
				check = this.metaInfo == null;
				
				if (check == false) {
	
					if (this.valueRepresentation.equals("OB")) {
						
						vlen = 4;
						plen = 2;
						
					}
					
					else {
						
						vlen = 2;
						plen = 0;
						
					}
					
					for (int a = 0; a < this.metaInfo.length; a ++)
						
						elmlen += 4;
				
				}
	
				break;
			case 2 : //uniqueIdentifier
				
				//Now get length of data in bytes
				check = this.uniqueIdentifier == null;
				
				if (check == false) {
					
					vlen = 2;
					plen = 0;
					
					arr = this.uniqueIdentifier.getBytes();
					elmlen = arr.length;
					
				}
				
				//If byteLength is odd then a padding bit will be added by the code later
				//For our purposes here we will assume that it will be added so 
				//check to see if the length is odd and if so add one byte
				if (! (elmlen % 2 == 0)) elmlen ++;
				
				break;
		
		}
		
		totalLength = glen + elen + vrlen + vlen + plen + elmlen;
		
		return totalLength;
		
	}*/

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getElementNumber() {
		return elementNumber;
	}

	public void setElementNumber(int elementNumber) {
		this.elementNumber = elementNumber;
	}

	public String getValueRepresentation() {
		return valueRepresentation;
	}

	public void setValueRepresentation(String valueRepresentation) {
		this.valueRepresentation = valueRepresentation;
	}

	public int getValueLength() {
		return valueLength;
	}

	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}

	public String getElementData() {
		return elementData;
	}

	public void setElementData(String elementData) {
		this.elementData = elementData;
	}

	public short getShrElementData() {
		return shrElementData;
	}

	public void setShrElementData(short shrElementData) {
		this.shrElementData = shrElementData;
	}

	public int getIntElementData() {
		return intElementData;
	}

	public void setIntElementData(int intElementData) {
		this.intElementData = intElementData;
	}

	public int[] getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(int[] metaInfo) {
		this.metaInfo = metaInfo;
	}

	public int getElementLength() {
		return elementLength;
	}

	public void setElementLength(int elementLength) {
		this.elementLength = elementLength;
	}

	public Date getTheDate() {
		return theDate;
	}

	public void setTheDate(Date theDate) {
		this.theDate = theDate;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}
	
	public int getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(int byteOrder) {
		this.byteOrder = byteOrder;
	}

	public void writeToBuffer() {
		
		try {
			
			if (this.byteOrder == 1) this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			else this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_LITTLE_ENDIAN);
			
			if (! (this.elementData == null)) {
			
				this.buffer.write(this.elementData.getBytes());
				this.elementLength = this.buffer.size();
			
			}
			
		}
		
		catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
