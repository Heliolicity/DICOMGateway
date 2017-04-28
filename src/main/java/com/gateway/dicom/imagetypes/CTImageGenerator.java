package com.gateway.dicom.imagetypes;

import java.util.List;
import java.util.ArrayList;

import com.gateway.dicom.entities.DataElement;

public class CTImageGenerator extends DicomImageGenerator {

	private String UIDBasis;
	private List<DataElement> metaInfoAttributes;
	private List<DataElement> patientModule;
	private List<DataElement> generalStudyModule;
	private List<DataElement> generalSeriesModule;
	private List<DataElement> frameOfReferenceModule;
	private List<DataElement> generalEquipmentModule;
	private List<DataElement> generalImageModule;
	private List<DataElement> imagePlaneModule;
	private List<DataElement> imagePixelModule;
	private List<DataElement> sopCommonModule;
	
	public CTImageGenerator() {}
	
	public CTImageGenerator(String UIDBasis) { this.UIDBasis = UIDBasis; }
	
	public int[] getMetaInfoVersion() { 
		
		int[] metaInfoVersion = new int[]{0x00, 0x01}; 
		return metaInfoVersion;
		
	}
	
	public List<DataElement> getMetaInfoAttributes() {
		
		this.metaInfoAttributes = new ArrayList<DataElement>();
		int numberOfBytes = 0;
		
		//0x0002, 0x0000, metaInfoVersionLength
		DataElement metaInfoGroupLength = new DataElement(0x0002, 0x0000, "UL", 2, ""); // set data element to blank
		
		//0x0002, 0x0001, metaInfoVersion
		DataElement metaInfoVersion = new DataElement(0x0002, 0x0001, "OB", 2, "");
		metaInfoVersion.setMetaInfo(this.getMetaInfoVersion());
		metaInfoVersion.setElementLength(metaInfoVersion.determineElementLength(1));
		numberOfBytes += metaInfoVersion.getElementLength();
		
		//0x0002, 0x0002, sopClassUID
		DataElement sopClassUID = new DataElement(0x0002, 0x0002, "UI", 2, "1.2.840.10008.5.1.4.1.1.2");
		sopClassUID.setElementLength(sopClassUID.determineElementLength(0));
		numberOfBytes += sopClassUID.getElementLength();
		
		//0x0002, 0x0003, sopInstanceUID
		DataElement sopInstanceUID = new DataElement(0x0002, 0x0003, "UI", 2, "");
		sopInstanceUID.generateUniqueIdentifier(this.UIDBasis);
		//pl(s);
		sopInstanceUID.setElementLength(sopInstanceUID.determineElementLength(2));
		numberOfBytes += sopInstanceUID.getElementLength(); 
		
		//0x0002, 0x0010, transferSyntaxUID
		DataElement transferSyntaxUID = new DataElement(0x0002, 0x0010, "UI", 2, "1.2.840.10008.1.2");
		transferSyntaxUID.setElementLength(transferSyntaxUID.determineElementLength(0));
		numberOfBytes += transferSyntaxUID.getElementLength();
		
		//0x0002, 0x0012, implementationClassUID
		DataElement implementationClassUID = new DataElement(0x0002, 0x0012, "UI", 2, "1.2.40.0.13.1.1");
		implementationClassUID.setElementLength(implementationClassUID.determineElementLength(0));
		numberOfBytes += implementationClassUID.getElementLength();
		
		metaInfoGroupLength.setElementLength(numberOfBytes); // Store the length of the meta data here for simplicity
		
		this.metaInfoAttributes.add(0, metaInfoGroupLength);
		this.metaInfoAttributes.add(1, metaInfoVersion);
		this.metaInfoAttributes.add(2, sopClassUID);
		this.metaInfoAttributes.add(3, sopInstanceUID);
		this.metaInfoAttributes.add(4, transferSyntaxUID);
		this.metaInfoAttributes.add(5, implementationClassUID);
		
		return this.metaInfoAttributes;
		
	}
	
	public List<DataElement> getPatientModule() {
		
		this.patientModule = new ArrayList<DataElement>();
		
		//Patient's Name	(0010,0010)	2					Patient's full name.
		DataElement patientName = new DataElement(0x0010, 0x0010, "Bob Hickson");
		this.patientModule.add(0, patientName);
		
		//Patient ID		
		DataElement patientID = new DataElement(0x0010, 0x0020);
		patientID.generateUniqueIdentifier(this.UIDBasis);
		this.patientModule.add(1, patientID);
		
		//Patient DOB
		DataElement patientDOB = new DataElement(0x0010, 0x0030, "19810622");
		this.patientModule.add(2, patientDOB);
		
		//Patient Sex
		DataElement patientSex = new DataElement(0x0010, 0x0040, "M");
		this.patientModule.add(3, patientSex);
		
		return this.patientModule;
		
	}
	
	public List<DataElement> getGeneralStudyModule() {
		
		this.generalStudyModule = new ArrayList<DataElement>();
		
		//Study Instance UID
		DataElement studyInstanceUID = new DataElement(0x0020, 0x000D);
		studyInstanceUID.generateUniqueIdentifier(this.UIDBasis);
		this.generalStudyModule.add(0, studyInstanceUID);
		
		//Study Date
		DataElement studyDate = new DataElement(0x0008, 0x0020, "20160315");
		this.generalStudyModule.add(1, studyDate);
		
		//Study Time
		DataElement studyTime = new DataElement(0x0008, 0x0030);
		studyTime.setElementData(studyTime.getCurrentTime());
		this.generalStudyModule.add(2, studyTime);
		
		DataElement referringPhysician = new DataElement(0x0008, 0x0009, "Dr John Smith MB BAO BCH");
		this.generalStudyModule.add(3, referringPhysician);
		
		//Study ID
		DataElement studyID = new DataElement(0x0020, 0x0010);
		studyID.generateUniqueIdentifier(this.UIDBasis);
		this.generalStudyModule.add(4, studyID);
		
		//Accession Number
		DataElement accessionNumber = new DataElement(0x0008, 0x0050);
		accessionNumber.generateUniqueIdentifier(this.UIDBasis);
		this.generalStudyModule.add(5, accessionNumber);
		
		return this.generalStudyModule;
		
	}
	
	public List<DataElement> getGeneralSeriesModule() {
		
		this.generalSeriesModule = new ArrayList<DataElement>();
		
		DataElement modality = new DataElement(0x0008, 0x0060, "CT");
		this.generalSeriesModule.add(0, modality);
		
		DataElement seriesInstanceUID = new DataElement(0x0020, 0x000E);
		seriesInstanceUID.generateUniqueIdentifier(this.UIDBasis);
		this.generalSeriesModule.add(1, seriesInstanceUID);
		
		DataElement seriesNumber = new DataElement(0x0020, 0x0011);
		seriesNumber.generateUniqueIdentifier(this.UIDBasis);
		this.generalSeriesModule.add(2, seriesNumber);
		
		return this.generalSeriesModule;
		
	}
	
	public List<DataElement> getFrameOfReferenceModule() {
		
		this.frameOfReferenceModule = new ArrayList<DataElement>();
		
		//Frame of Reference UID
		DataElement frameOfReferenceUID = new DataElement(0x0020, 0x0052);
		frameOfReferenceUID.generateUniqueIdentifier(this.UIDBasis);
		this.frameOfReferenceModule.add(0, frameOfReferenceUID);
		
		//DataElement positionReferenceIndicator = new DataElement(0x0020, 0x1040, "SLIDE_CORNER");
		//this.frameOfReferenceModule.add(1, positionReferenceIndicator);
		
		return this.frameOfReferenceModule;
		
	}
	
	public List<DataElement> getGeneralEquipmentModule() {
		
		this.generalEquipmentModule = new ArrayList<DataElement>();
		
		//Manufacturer
		DataElement manufacturer = new DataElement(0x0008, 0x0070, "Siemens");
		this.generalEquipmentModule.add(0, manufacturer);
		
		return this.generalEquipmentModule;
		
	}

	public List<DataElement> getGeneralImageModule() {
		
		this.generalImageModule = new ArrayList<DataElement>();
		
		DataElement instanceNumberUID = new DataElement(0x0020, 0x0013);
		instanceNumberUID.generateUniqueIdentifier(this.UIDBasis);
		this.generalImageModule.add(0, instanceNumberUID);
		
		return this.generalImageModule;
		
	}

	public List<DataElement> getImagePlaneModule() {
		
		this.imagePlaneModule = new ArrayList<DataElement>();
		
		//Pixel Spacing
		DataElement pixelSpacing = new DataElement(0x0028, 0x0030, "0.76171875 0.76171875");
		this.imagePlaneModule.add(0, pixelSpacing);
		
		//Image Orientation
		DataElement imageOrientation = new DataElement(0x0020, 0x0037, "1\\0\\0\\0\\1\\0");
		this.imagePlaneModule.add(1, imageOrientation);
		
		//Image Position
		DataElement imagePosition = new DataElement(0x0028, 0x0030, "-167.61914\\-401.61914\\-39");
		this.imagePlaneModule.add(2, imagePosition);
		
		//Slice Thickness
		DataElement sliceThickness = new DataElement(0x0018, 0x0050, "1");
		this.imagePlaneModule.add(3, sliceThickness);
		
		return this.imagePlaneModule;
		
	}
	
	public List<DataElement> getImagePixelModule() {
		
		this.imagePixelModule = new ArrayList<DataElement>();
		
		//Image Type
		DataElement imageType = new DataElement(0x0008, 0x0008, "ORIGINAL\\PRIMARY\\AXIAL\\CT_SOM5 SPI");
		this.imagePixelModule.add(0, imageType);
		
		//Samples Per Pixel
		DataElement samplesPerPixel = new DataElement(0x0028, 0x0002, "1");
		this.imagePixelModule.add(1, samplesPerPixel);
		
		//Photometric Interpretation
		DataElement photometricInterpretation = new DataElement(0x0028, 0x0004, "MONOCHROME2");
		this.imagePixelModule.add(2, photometricInterpretation);
		
		//Rows
		DataElement rows = new DataElement(0x0028, 0x0010, "512");
		this.imagePixelModule.add(3, rows);
		
		//Columns
		DataElement columns = new DataElement(0x0028, 0x0011, "512");
		this.imagePixelModule.add(4, columns);
		
		//Bits Allocated
		DataElement bitsAllocated = new DataElement(0x0028, 0x0100, "16");
		this.imagePixelModule.add(5, bitsAllocated);
		
		//Bits Stored
		DataElement bitsStored = new DataElement(0x0028, 0x0101, "12");
		this.imagePixelModule.add(6, bitsStored);
		
		//High Bit
		DataElement highBit = new DataElement(0x0028, 0x0102, "11");
		this.imagePixelModule.add(7, highBit);
		
		//Rescale Intercept
		DataElement rescaleIntercept = new DataElement(0x0028, 0x1052, "-1024");
		this.imagePixelModule.add(8, rescaleIntercept);
		
		//Rescale Slope
		DataElement rescaleSlope = new DataElement(0x0028, 0x1053, "1");
		this.imagePixelModule.add(9, rescaleSlope);
		
		return this.imagePixelModule;
		
	}
	
	public List<DataElement> getSopCommonModule() {
		
		this.sopCommonModule = new ArrayList<DataElement>();
		
		//SOP Class UID
		DataElement sopClassUID = new DataElement(0x0008, 0x0016, "1.2.840.10008.5.1.4.1.1.2");
		this.sopCommonModule.add(0, sopClassUID);
		
		//SOP Instance UID
		DataElement sopInstanceUID = new DataElement(0x0008, 0x0018);
		sopInstanceUID.generateUniqueIdentifier(this.UIDBasis);
		this.sopCommonModule.add(1, sopInstanceUID);
		
		return this.sopCommonModule;
		
	}
	
	//private void pl(String s) { System.out.println(s); }
	
}
