package com.gateway.dicom.protocols;

import com.gateway.dicom.entities.DataElement;
import com.gateway.dicom.lib.DicomOutputBuffer;

public class ImagePacket extends PDU {

	private DataElement imageType;
	private DataElement sopClassUID;
	private DataElement sopInstanceUID;
	private DataElement studyDate;
	private DataElement seriesDate;
	private DataElement acquisitionDate;
	private DataElement contentDate;
	private DataElement studyTime;
	private DataElement seriesTime;
	private DataElement acquisitionTime;
	private DataElement contentTime;
	private DataElement accesstionNumber;
	private DataElement modality;
	private DataElement manufacturer;
	private DataElement institutionName;
	private DataElement referringPhysiciansName;
	private DataElement stationName;
	private DataElement studyDescription;
	private DataElement seriesDescription;
	private DataElement performingPhysiciansName;
	private DataElement operatorsName;
	private DataElement manufacturersModelName;
	private DataElement patientsName;
	private DataElement patientID;
	private DataElement patientsBirthDate;
	private DataElement patientsSex;
	private DataElement patientsAge;
	private DataElement patientsWeight;
	private DataElement additionalPatientHistory;
	private DataElement scanningSequence;
	private DataElement sequenceVariant;
	private DataElement scanOptions;
	private DataElement mrAcquisitionType;
	private DataElement sequenceName;
	private DataElement sliceThickness;
	private DataElement repetitionTime;
	private DataElement echoTime;
	private DataElement numberOfAverages;
	private DataElement imagingFrequency;
	private DataElement echoNumber;
	private DataElement magneticFieldStrength;
	private DataElement spacingBetweenSlices;
	private DataElement numberOfPhaseEncodingSteps;
	private DataElement echoTrainLength;
	private DataElement pixelBandwidth;
	private DataElement softwareVersion;
	private DataElement protocolName;
	private DataElement heartRate;
	private DataElement cardiacNumberOfImages;
	private DataElement triggerWindow;
	private DataElement reconstructionDiameter;
	private DataElement receiveCoilName;
	private DataElement acquisitionMatrix;
	private DataElement inPhaseEncodingDirection;
	private DataElement flipAngle;
	private DataElement SAR;
	private DataElement patientPosition;
	private DataElement studyInstanceUID;
	private DataElement seriesInstanceUID;
	private DataElement studyID;
	private DataElement seriesNumber;
	private DataElement acquisitionNumber;
	private DataElement instanceNumber;
	private DataElement patientOrientation;
	private DataElement imagePosition;
	private DataElement imagePositionPatient;
	private DataElement imageOrientation;
	private DataElement imageOrientationPatient;
	private DataElement frameOfReferenceUID;
	private DataElement imagesInAcquisition;
	private DataElement positionReferenceIndicator;
	private DataElement sliceLocator;
	private DataElement samplesPerPixel;
	private DataElement photometricInterpretation;
	private DataElement rows;
	private DataElement columns;
	private DataElement pixelSpacing;
	private DataElement bitsAllocated;
	private DataElement bitsStored;
	private DataElement highBit;
	private DataElement pixelRepresentation;
	private DataElement smallestImagePixelValue;
	private DataElement largestImagePixelValue;
	private DataElement pixelPaddingValue;
	private DataElement windowCenter;
	private DataElement windowWidth;
	private DataElement rescaleIntercept;
	private DataElement rescaleSlope;
	private DataElement rescaleType;
	private DataElement pixelData;
	
	private byte[] packetData;
	private int command;
	private DicomOutputBuffer buffer;
	
	public ImagePacket(byte[] packetData) {
		this.packetData = packetData;
	}
	
	public ImagePacket() {}

	public byte[] getPacketData() {
		return packetData;
	}

	public void setPacketData(byte[] packetData) {
		this.packetData = packetData;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public DicomOutputBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(DicomOutputBuffer buffer) {
		this.buffer = buffer;
	}

	public void writeToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			
				
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public DataElement getImageType() {
		return imageType;
	}

	public void setImageType(DataElement imageType) {
		this.imageType = imageType;
	}

	public DataElement getSopClassUID() {
		return sopClassUID;
	}

	public void setSopClassUID(DataElement sopClassUID) {
		this.sopClassUID = sopClassUID;
	}

	public DataElement getSopInstanceUID() {
		return sopInstanceUID;
	}

	public void setSopInstanceUID(DataElement sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}

	public DataElement getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(DataElement studyDate) {
		this.studyDate = studyDate;
	}

	public DataElement getSeriesDate() {
		return seriesDate;
	}

	public void setSeriesDate(DataElement seriesDate) {
		this.seriesDate = seriesDate;
	}

	public DataElement getAcquisitionDate() {
		return acquisitionDate;
	}

	public void setAcquisitionDate(DataElement acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public DataElement getContentDate() {
		return contentDate;
	}

	public void setContentDate(DataElement contentDate) {
		this.contentDate = contentDate;
	}

	public DataElement getStudyTime() {
		return studyTime;
	}

	public void setStudyTime(DataElement studyTime) {
		this.studyTime = studyTime;
	}

	public DataElement getSeriesTime() {
		return seriesTime;
	}

	public void setSeriesTime(DataElement seriesTime) {
		this.seriesTime = seriesTime;
	}

	public DataElement getAcquisitionTime() {
		return acquisitionTime;
	}

	public void setAcquisitionTime(DataElement acquisitionTime) {
		this.acquisitionTime = acquisitionTime;
	}

	public DataElement getContentTime() {
		return contentTime;
	}

	public void setContentTime(DataElement contentTime) {
		this.contentTime = contentTime;
	}

	public DataElement getAccesstionNumber() {
		return accesstionNumber;
	}

	public void setAccesstionNumber(DataElement accesstionNumber) {
		this.accesstionNumber = accesstionNumber;
	}

	public DataElement getModality() {
		return modality;
	}

	public void setModality(DataElement modality) {
		this.modality = modality;
	}

	public DataElement getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(DataElement manufacturer) {
		this.manufacturer = manufacturer;
	}

	public DataElement getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(DataElement institutionName) {
		this.institutionName = institutionName;
	}

	public DataElement getReferringPhysiciansName() {
		return referringPhysiciansName;
	}

	public void setReferringPhysiciansName(DataElement referringPhysiciansName) {
		this.referringPhysiciansName = referringPhysiciansName;
	}

	public DataElement getStationName() {
		return stationName;
	}

	public void setStationName(DataElement stationName) {
		this.stationName = stationName;
	}

	public DataElement getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(DataElement studyDescription) {
		this.studyDescription = studyDescription;
	}

	public DataElement getSeriesDescription() {
		return seriesDescription;
	}

	public void setSeriesDescription(DataElement seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public DataElement getPerformingPhysiciansName() {
		return performingPhysiciansName;
	}

	public void setPerformingPhysiciansName(DataElement performingPhysiciansName) {
		this.performingPhysiciansName = performingPhysiciansName;
	}

	public DataElement getOperatorsName() {
		return operatorsName;
	}

	public void setOperatorsName(DataElement operatorsName) {
		this.operatorsName = operatorsName;
	}

	public DataElement getManufacturersModelName() {
		return manufacturersModelName;
	}

	public void setManufacturersModelName(DataElement manufacturersModelName) {
		this.manufacturersModelName = manufacturersModelName;
	}

	public DataElement getPatientsName() {
		return patientsName;
	}

	public void setPatientsName(DataElement patientsName) {
		this.patientsName = patientsName;
	}

	public DataElement getPatientID() {
		return patientID;
	}

	public void setPatientID(DataElement patientID) {
		this.patientID = patientID;
	}

	public DataElement getPatientsBirthDate() {
		return patientsBirthDate;
	}

	public void setPatientsBirthDate(DataElement patientsBirthDate) {
		this.patientsBirthDate = patientsBirthDate;
	}

	public DataElement getPatientsSex() {
		return patientsSex;
	}

	public void setPatientsSex(DataElement patientsSex) {
		this.patientsSex = patientsSex;
	}

	public DataElement getPatientsAge() {
		return patientsAge;
	}

	public void setPatientsAge(DataElement patientsAge) {
		this.patientsAge = patientsAge;
	}

	public DataElement getPatientsWeight() {
		return patientsWeight;
	}

	public void setPatientsWeight(DataElement patientsWeight) {
		this.patientsWeight = patientsWeight;
	}

	public DataElement getAdditionalPatientHistory() {
		return additionalPatientHistory;
	}

	public void setAdditionalPatientHistory(DataElement additionalPatientHistory) {
		this.additionalPatientHistory = additionalPatientHistory;
	}

	public DataElement getScanningSequence() {
		return scanningSequence;
	}

	public void setScanningSequence(DataElement scanningSequence) {
		this.scanningSequence = scanningSequence;
	}

	public DataElement getSequenceVariant() {
		return sequenceVariant;
	}

	public void setSequenceVariant(DataElement sequenceVariant) {
		this.sequenceVariant = sequenceVariant;
	}

	public DataElement getScanOptions() {
		return scanOptions;
	}

	public void setScanOptions(DataElement scanOptions) {
		this.scanOptions = scanOptions;
	}

	public DataElement getMrAcquisitionType() {
		return mrAcquisitionType;
	}

	public void setMrAcquisitionType(DataElement mrAcquisitionType) {
		this.mrAcquisitionType = mrAcquisitionType;
	}

	public DataElement getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(DataElement sequenceName) {
		this.sequenceName = sequenceName;
	}

	public DataElement getSliceThickness() {
		return sliceThickness;
	}

	public void setSliceThickness(DataElement sliceThickness) {
		this.sliceThickness = sliceThickness;
	}

	public DataElement getRepetitionTime() {
		return repetitionTime;
	}

	public void setRepetitionTime(DataElement repetitionTime) {
		this.repetitionTime = repetitionTime;
	}

	public DataElement getEchoTime() {
		return echoTime;
	}

	public void setEchoTime(DataElement echoTime) {
		this.echoTime = echoTime;
	}

	public DataElement getNumberOfAverages() {
		return numberOfAverages;
	}

	public void setNumberOfAverages(DataElement numberOfAverages) {
		this.numberOfAverages = numberOfAverages;
	}

	public DataElement getImagingFrequency() {
		return imagingFrequency;
	}

	public void setImagingFrequency(DataElement imagingFrequency) {
		this.imagingFrequency = imagingFrequency;
	}

	public DataElement getEchoNumber() {
		return echoNumber;
	}

	public void setEchoNumber(DataElement echoNumber) {
		this.echoNumber = echoNumber;
	}

	public DataElement getMagneticFieldStrength() {
		return magneticFieldStrength;
	}

	public void setMagneticFieldStrength(DataElement magneticFieldStrength) {
		this.magneticFieldStrength = magneticFieldStrength;
	}

	public DataElement getSpacingBetweenSlices() {
		return spacingBetweenSlices;
	}

	public void setSpacingBetweenSlices(DataElement spacingBetweenSlices) {
		this.spacingBetweenSlices = spacingBetweenSlices;
	}

	public DataElement getNumberOfPhaseEncodingSteps() {
		return numberOfPhaseEncodingSteps;
	}

	public void setNumberOfPhaseEncodingSteps(DataElement numberOfPhaseEncodingSteps) {
		this.numberOfPhaseEncodingSteps = numberOfPhaseEncodingSteps;
	}

	public DataElement getEchoTrainLength() {
		return echoTrainLength;
	}

	public void setEchoTrainLength(DataElement echoTrainLength) {
		this.echoTrainLength = echoTrainLength;
	}

	public DataElement getPixelBandwidth() {
		return pixelBandwidth;
	}

	public void setPixelBandwidth(DataElement pixelBandwidth) {
		this.pixelBandwidth = pixelBandwidth;
	}

	public DataElement getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(DataElement softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public DataElement getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(DataElement protocolName) {
		this.protocolName = protocolName;
	}

	public DataElement getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(DataElement heartRate) {
		this.heartRate = heartRate;
	}

	public DataElement getCardiacNumberOfImages() {
		return cardiacNumberOfImages;
	}

	public void setCardiacNumberOfImages(DataElement cardiacNumberOfImages) {
		this.cardiacNumberOfImages = cardiacNumberOfImages;
	}

	public DataElement getTriggerWindow() {
		return triggerWindow;
	}

	public void setTriggerWindow(DataElement triggerWindow) {
		this.triggerWindow = triggerWindow;
	}

	public DataElement getReconstructionDiameter() {
		return reconstructionDiameter;
	}

	public void setReconstructionDiameter(DataElement reconstructionDiameter) {
		this.reconstructionDiameter = reconstructionDiameter;
	}

	public DataElement getReceiveCoilName() {
		return receiveCoilName;
	}

	public void setReceiveCoilName(DataElement receiveCoilName) {
		this.receiveCoilName = receiveCoilName;
	}

	public DataElement getAcquisitionMatrix() {
		return acquisitionMatrix;
	}

	public void setAcquisitionMatrix(DataElement acquisitionMatrix) {
		this.acquisitionMatrix = acquisitionMatrix;
	}

	public DataElement getInPhaseEncodingDirection() {
		return inPhaseEncodingDirection;
	}

	public void setInPhaseEncodingDirection(DataElement inPhaseEncodingDirection) {
		this.inPhaseEncodingDirection = inPhaseEncodingDirection;
	}

	public DataElement getFlipAngle() {
		return flipAngle;
	}

	public void setFlipAngle(DataElement flipAngle) {
		this.flipAngle = flipAngle;
	}

	public DataElement getSAR() {
		return SAR;
	}

	public void setSAR(DataElement sAR) {
		SAR = sAR;
	}

	public DataElement getPatientPosition() {
		return patientPosition;
	}

	public void setPatientPosition(DataElement patientPosition) {
		this.patientPosition = patientPosition;
	}

	public DataElement getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public void setStudyInstanceUID(DataElement studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}

	public DataElement getSeriesInstanceUID() {
		return seriesInstanceUID;
	}

	public void setSeriesInstanceUID(DataElement seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}

	public DataElement getStudyID() {
		return studyID;
	}

	public void setStudyID(DataElement studyID) {
		this.studyID = studyID;
	}

	public DataElement getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(DataElement seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public DataElement getAcquisitionNumber() {
		return acquisitionNumber;
	}

	public void setAcquisitionNumber(DataElement acquisitionNumber) {
		this.acquisitionNumber = acquisitionNumber;
	}

	public DataElement getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(DataElement instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public DataElement getPatientOrientation() {
		return patientOrientation;
	}

	public void setPatientOrientation(DataElement patientOrientation) {
		this.patientOrientation = patientOrientation;
	}

	public DataElement getImagePosition() {
		return imagePosition;
	}

	public void setImagePosition(DataElement imagePosition) {
		this.imagePosition = imagePosition;
	}

	public DataElement getImagePositionPatient() {
		return imagePositionPatient;
	}

	public void setImagePositionPatient(DataElement imagePositionPatient) {
		this.imagePositionPatient = imagePositionPatient;
	}

	public DataElement getImageOrientation() {
		return imageOrientation;
	}

	public void setImageOrientation(DataElement imageOrientation) {
		this.imageOrientation = imageOrientation;
	}

	public DataElement getImageOrientationPatient() {
		return imageOrientationPatient;
	}

	public void setImageOrientationPatient(DataElement imageOrientationPatient) {
		this.imageOrientationPatient = imageOrientationPatient;
	}

	public DataElement getFrameOfReferenceUID() {
		return frameOfReferenceUID;
	}

	public void setFrameOfReferenceUID(DataElement frameOfReferenceUID) {
		this.frameOfReferenceUID = frameOfReferenceUID;
	}

	public DataElement getImagesInAcquisition() {
		return imagesInAcquisition;
	}

	public void setImagesInAcquisition(DataElement imagesInAcquisition) {
		this.imagesInAcquisition = imagesInAcquisition;
	}

	public DataElement getPositionReferenceIndicator() {
		return positionReferenceIndicator;
	}

	public void setPositionReferenceIndicator(DataElement positionReferenceIndicator) {
		this.positionReferenceIndicator = positionReferenceIndicator;
	}

	public DataElement getSliceLocator() {
		return sliceLocator;
	}

	public void setSliceLocator(DataElement sliceLocator) {
		this.sliceLocator = sliceLocator;
	}

	public DataElement getSamplesPerPixel() {
		return samplesPerPixel;
	}

	public void setSamplesPerPixel(DataElement samplesPerPixel) {
		this.samplesPerPixel = samplesPerPixel;
	}

	public DataElement getPhotometricInterpretation() {
		return photometricInterpretation;
	}

	public void setPhotometricInterpretation(DataElement photometricInterpretation) {
		this.photometricInterpretation = photometricInterpretation;
	}

	public DataElement getRows() {
		return rows;
	}

	public void setRows(DataElement rows) {
		this.rows = rows;
	}

	public DataElement getColumns() {
		return columns;
	}

	public void setColumns(DataElement columns) {
		this.columns = columns;
	}

	public DataElement getPixelSpacing() {
		return pixelSpacing;
	}

	public void setPixelSpacing(DataElement pixelSpacing) {
		this.pixelSpacing = pixelSpacing;
	}

	public DataElement getBitsAllocated() {
		return bitsAllocated;
	}

	public void setBitsAllocated(DataElement bitsAllocated) {
		this.bitsAllocated = bitsAllocated;
	}

	public DataElement getBitsStored() {
		return bitsStored;
	}

	public void setBitsStored(DataElement bitsStored) {
		this.bitsStored = bitsStored;
	}

	public DataElement getHighBit() {
		return highBit;
	}

	public void setHighBit(DataElement highBit) {
		this.highBit = highBit;
	}

	public DataElement getPixelRepresentation() {
		return pixelRepresentation;
	}

	public void setPixelRepresentation(DataElement pixelRepresentation) {
		this.pixelRepresentation = pixelRepresentation;
	}

	public DataElement getSmallestImagePixelValue() {
		return smallestImagePixelValue;
	}

	public void setSmallestImagePixelValue(DataElement smallestImagePixelValue) {
		this.smallestImagePixelValue = smallestImagePixelValue;
	}

	public DataElement getLargestImagePixelValue() {
		return largestImagePixelValue;
	}

	public void setLargestImagePixelValue(DataElement largestImagePixelValue) {
		this.largestImagePixelValue = largestImagePixelValue;
	}

	public DataElement getPixelPaddingValue() {
		return pixelPaddingValue;
	}

	public void setPixelPaddingValue(DataElement pixelPaddingValue) {
		this.pixelPaddingValue = pixelPaddingValue;
	}

	public DataElement getWindowCenter() {
		return windowCenter;
	}

	public void setWindowCenter(DataElement windowCenter) {
		this.windowCenter = windowCenter;
	}

	public DataElement getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(DataElement windowWidth) {
		this.windowWidth = windowWidth;
	}

	public DataElement getRescaleIntercept() {
		return rescaleIntercept;
	}

	public void setRescaleIntercept(DataElement rescaleIntercept) {
		this.rescaleIntercept = rescaleIntercept;
	}

	public DataElement getRescaleSlope() {
		return rescaleSlope;
	}

	public void setRescaleSlope(DataElement rescaleSlope) {
		this.rescaleSlope = rescaleSlope;
	}

	public DataElement getRescaleType() {
		return rescaleType;
	}

	public void setRescaleType(DataElement rescaleType) {
		this.rescaleType = rescaleType;
	}

	public DataElement getPixelData() {
		return pixelData;
	}

	public void setPixelData(DataElement pixelData) {
		this.pixelData = pixelData;
	}

	private void p(String s) { System.out.print(s); }
	
	private void pl() { System.out.println(); }
	
	private void pl(String s) { System.out.println(s); }
	
}
