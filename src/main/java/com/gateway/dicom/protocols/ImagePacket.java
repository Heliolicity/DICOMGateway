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
	
	public void writeHeaderToBuffer() {
		
		try {
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			
			this.buffer.writeUInt16(this.imageType.getGroupNumber());
			this.buffer.writeUInt16(this.imageType.getElementNumber());
			this.buffer.writeUInt32(this.imageType.getElementLength());
			this.buffer.write(this.imageType.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.sopClassUID.getGroupNumber());
			this.buffer.writeUInt16(this.sopClassUID.getElementNumber());
			this.buffer.writeUInt32(this.sopClassUID.getElementLength());
			this.buffer.write(this.sopClassUID.getElementData().getBytes());
				
			this.buffer.writeUInt16(this.studyDate.getGroupNumber());
			this.buffer.writeUInt16(this.studyDate.getElementNumber());
			this.buffer.writeUInt32(this.studyDate.getElementLength());
			this.buffer.write(this.studyDate.getElementData().getBytes());

			this.buffer.writeUInt16(this.seriesDate.getGroupNumber());
			this.buffer.writeUInt16(this.seriesDate.getElementNumber());
			this.buffer.writeUInt32(this.seriesDate.getElementLength());
			this.buffer.write(this.seriesDate.getElementData().getBytes());

			this.buffer.writeUInt16(this.acquisitionDate.getGroupNumber());
			this.buffer.writeUInt16(this.acquisitionDate.getElementNumber());
			this.buffer.writeUInt32(this.acquisitionDate.getElementLength());
			this.buffer.write(this.acquisitionDate.getElementData().getBytes());

			this.buffer.writeUInt16(this.contentDate.getGroupNumber());
			this.buffer.writeUInt16(this.contentDate.getElementNumber());
			this.buffer.writeUInt32(this.contentDate.getElementLength());
			this.buffer.write(this.contentDate.getElementData().getBytes());

			this.buffer.writeUInt16(this.studyTime.getGroupNumber());
			this.buffer.writeUInt16(this.studyTime.getElementNumber());
			this.buffer.writeUInt32(this.studyTime.getElementLength());
			this.buffer.write(this.studyTime.getElementData().getBytes());

			this.buffer.writeUInt16(this.seriesTime.getGroupNumber());
			this.buffer.writeUInt16(this.seriesTime.getElementNumber());
			this.buffer.writeUInt32(this.seriesTime.getElementLength());
			this.buffer.write(this.seriesTime.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.acquisitionTime.getGroupNumber());
			this.buffer.writeUInt16(this.acquisitionTime.getElementNumber());
			this.buffer.writeUInt32(this.acquisitionTime.getElementLength());
			this.buffer.write(this.acquisitionTime.getElementData().getBytes());

			this.buffer.writeUInt16(this.contentTime.getGroupNumber());
			this.buffer.writeUInt16(this.contentTime.getElementNumber());
			this.buffer.writeUInt32(this.contentTime.getElementLength());
			this.buffer.write(this.contentTime.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.accesstionNumber.getGroupNumber());
			this.buffer.writeUInt16(this.accesstionNumber.getElementNumber());
			this.buffer.writeUInt32(this.accesstionNumber.getElementLength());
			//Length for this is 0 so no data
			
			this.buffer.writeUInt16(this.modality.getGroupNumber());
			this.buffer.writeUInt16(this.modality.getElementNumber());
			this.buffer.writeUInt32(this.modality.getElementLength());
			this.buffer.write(this.modality.getElementData().getBytes());

			this.buffer.writeUInt16(this.manufacturer.getGroupNumber());
			this.buffer.writeUInt16(this.manufacturer.getElementNumber());
			this.buffer.writeUInt32(this.manufacturer.getElementLength());
			this.buffer.write(this.manufacturer.getElementData().getBytes());

			this.buffer.writeUInt16(this.institutionName.getGroupNumber());
			this.buffer.writeUInt16(this.institutionName.getElementNumber());
			this.buffer.writeUInt32(this.institutionName.getElementLength());
			this.buffer.write(this.institutionName.getElementData().getBytes());

			this.buffer.writeUInt16(this.referringPhysiciansName.getGroupNumber());
			this.buffer.writeUInt16(this.referringPhysiciansName.getElementNumber());
			this.buffer.writeUInt32(this.referringPhysiciansName.getElementLength());
			this.buffer.write(this.referringPhysiciansName.getElementData().getBytes());

			this.buffer.writeUInt16(this.stationName.getGroupNumber());
			this.buffer.writeUInt16(this.stationName.getElementNumber());
			this.buffer.writeUInt32(this.stationName.getElementLength());
			this.buffer.write(this.stationName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.studyDescription.getGroupNumber());
			this.buffer.writeUInt16(this.studyDescription.getElementNumber());
			this.buffer.writeUInt32(this.studyDescription.getElementLength());
			this.buffer.write(this.studyDescription.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.seriesDescription.getGroupNumber());
			this.buffer.writeUInt16(this.seriesDescription.getElementNumber());
			this.buffer.writeUInt32(this.seriesDescription.getElementLength());
			this.buffer.write(this.seriesDescription.getElementData().getBytes());
		
			this.buffer.writeUInt16(this.performingPhysiciansName.getGroupNumber());
			this.buffer.writeUInt16(this.performingPhysiciansName.getElementNumber());
			this.buffer.writeUInt32(this.performingPhysiciansName.getElementLength());
			this.buffer.write(this.performingPhysiciansName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.operatorsName.getGroupNumber());
			this.buffer.writeUInt16(this.operatorsName.getElementNumber());
			this.buffer.writeUInt32(this.operatorsName.getElementLength());
			this.buffer.write(this.operatorsName.getElementData().getBytes());

			this.buffer.writeUInt16(this.manufacturersModelName.getGroupNumber());
			this.buffer.writeUInt16(this.manufacturersModelName.getElementNumber());
			this.buffer.writeUInt32(this.manufacturersModelName.getElementLength());
			this.buffer.write(this.manufacturersModelName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientsName.getGroupNumber());
			this.buffer.writeUInt16(this.patientsName.getElementNumber());
			this.buffer.writeUInt32(this.patientsName.getElementLength());
			this.buffer.write(this.patientsName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientID.getGroupNumber());
			this.buffer.writeUInt16(this.patientID.getElementNumber());
			this.buffer.writeUInt32(this.patientID.getElementLength());
			this.buffer.write(this.patientID.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientsBirthDate.getGroupNumber());
			this.buffer.writeUInt16(this.patientsBirthDate.getElementNumber());
			this.buffer.writeUInt32(this.patientsBirthDate.getElementLength());
			//0
			
			this.buffer.writeUInt16(this.patientsSex.getGroupNumber());
			this.buffer.writeUInt16(this.patientsSex.getElementNumber());
			this.buffer.writeUInt32(this.patientsSex.getElementLength());
			this.buffer.write(this.patientsSex.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientsAge.getGroupNumber());
			this.buffer.writeUInt16(this.patientsAge.getElementNumber());
			this.buffer.writeUInt32(this.patientsAge.getElementLength());
			this.buffer.write(this.patientsAge.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientsWeight.getGroupNumber());
			this.buffer.writeUInt16(this.patientsWeight.getElementNumber());
			this.buffer.writeUInt32(this.patientsWeight.getElementLength());
			this.buffer.write(this.patientsWeight.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.additionalPatientHistory.getGroupNumber());
			this.buffer.writeUInt16(this.additionalPatientHistory.getElementNumber());
			this.buffer.writeUInt32(this.additionalPatientHistory.getElementLength());
			//0
			
			this.buffer.writeUInt16(this.scanningSequence.getGroupNumber());
			this.buffer.writeUInt16(this.scanningSequence.getElementNumber());
			this.buffer.writeUInt32(this.scanningSequence.getElementLength());
			this.buffer.write(this.scanningSequence.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.sequenceVariant.getGroupNumber());
			this.buffer.writeUInt16(this.sequenceVariant.getElementNumber());
			this.buffer.writeUInt32(this.sequenceVariant.getElementLength());
			this.buffer.write(this.sequenceVariant.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.scanOptions.getGroupNumber());
			this.buffer.writeUInt16(this.scanOptions.getElementNumber());
			this.buffer.writeUInt32(this.scanOptions.getElementLength());
			this.buffer.write(this.scanOptions.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.mrAcquisitionType.getGroupNumber());
			this.buffer.writeUInt16(this.mrAcquisitionType.getElementNumber());
			this.buffer.writeUInt32(this.mrAcquisitionType.getElementLength());
			this.buffer.write(this.mrAcquisitionType.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.sequenceName.getGroupNumber());
			this.buffer.writeUInt16(this.sequenceName.getElementNumber());
			this.buffer.writeUInt32(this.sequenceName.getElementLength());
			this.buffer.write(this.sequenceName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.sliceThickness.getGroupNumber());
			this.buffer.writeUInt16(this.sliceThickness.getElementNumber());
			this.buffer.writeUInt32(this.sliceThickness.getElementLength());
			this.buffer.write(this.sliceThickness.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.repetitionTime.getGroupNumber());
			this.buffer.writeUInt16(this.repetitionTime.getElementNumber());
			this.buffer.writeUInt32(this.repetitionTime.getElementLength());
			this.buffer.write(this.repetitionTime.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.echoTime.getGroupNumber());
			this.buffer.writeUInt16(this.echoTime.getElementNumber());
			this.buffer.writeUInt32(this.echoTime.getElementLength());
			this.buffer.write(this.echoTime.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.numberOfAverages.getGroupNumber());
			this.buffer.writeUInt16(this.numberOfAverages.getElementNumber());
			this.buffer.writeUInt32(this.numberOfAverages.getElementLength());
			this.buffer.write(this.numberOfAverages.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imagingFrequency.getGroupNumber());
			this.buffer.writeUInt16(this.imagingFrequency.getElementNumber());
			this.buffer.writeUInt32(this.imagingFrequency.getElementLength());
			this.buffer.write(this.imagingFrequency.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.echoNumber.getGroupNumber());
			this.buffer.writeUInt16(this.echoNumber.getElementNumber());
			this.buffer.writeUInt32(this.echoNumber.getElementLength());
			this.buffer.write(this.echoNumber.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.magneticFieldStrength.getGroupNumber());
			this.buffer.writeUInt16(this.magneticFieldStrength.getElementNumber());
			this.buffer.writeUInt32(this.magneticFieldStrength.getElementLength());
			this.buffer.write(this.magneticFieldStrength.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.spacingBetweenSlices.getGroupNumber());
			this.buffer.writeUInt16(this.spacingBetweenSlices.getElementNumber());
			this.buffer.writeUInt32(this.spacingBetweenSlices.getElementLength());
			this.buffer.write(this.spacingBetweenSlices.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.numberOfPhaseEncodingSteps.getGroupNumber());
			this.buffer.writeUInt16(this.numberOfPhaseEncodingSteps.getElementNumber());
			this.buffer.writeUInt32(this.numberOfPhaseEncodingSteps.getElementLength());
			this.buffer.write(this.numberOfPhaseEncodingSteps.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.numberOfPhaseEncodingSteps.getGroupNumber());
			this.buffer.writeUInt16(this.numberOfPhaseEncodingSteps.getElementNumber());
			this.buffer.writeUInt32(this.numberOfPhaseEncodingSteps.getElementLength());
			this.buffer.write(this.numberOfPhaseEncodingSteps.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.echoTrainLength.getGroupNumber());
			this.buffer.writeUInt16(this.echoTrainLength.getElementNumber());
			this.buffer.writeUInt32(this.echoTrainLength.getElementLength());
			this.buffer.write(this.echoTrainLength.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.pixelBandwidth.getGroupNumber());
			this.buffer.writeUInt16(this.pixelBandwidth.getElementNumber());
			this.buffer.writeUInt32(this.pixelBandwidth.getElementLength());
			this.buffer.write(this.pixelBandwidth.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.softwareVersion.getGroupNumber());
			this.buffer.writeUInt16(this.softwareVersion.getElementNumber());
			this.buffer.writeUInt32(this.softwareVersion.getElementLength());
			this.buffer.write(this.softwareVersion.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.protocolName.getGroupNumber());
			this.buffer.writeUInt16(this.protocolName.getElementNumber());
			this.buffer.writeUInt32(this.protocolName.getElementLength());
			this.buffer.write(this.protocolName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.protocolName.getGroupNumber());
			this.buffer.writeUInt16(this.protocolName.getElementNumber());
			this.buffer.writeUInt32(this.protocolName.getElementLength());
			this.buffer.write(this.protocolName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.heartRate.getGroupNumber());
			this.buffer.writeUInt16(this.heartRate.getElementNumber());
			this.buffer.writeUInt32(this.heartRate.getElementLength());
			this.buffer.write(this.heartRate.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.cardiacNumberOfImages.getGroupNumber());
			this.buffer.writeUInt16(this.cardiacNumberOfImages.getElementNumber());
			this.buffer.writeUInt32(this.cardiacNumberOfImages.getElementLength());
			this.buffer.write(this.cardiacNumberOfImages.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.triggerWindow.getGroupNumber());
			this.buffer.writeUInt16(this.triggerWindow.getElementNumber());
			this.buffer.writeUInt32(this.triggerWindow.getElementLength());
			this.buffer.write(this.triggerWindow.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.reconstructionDiameter.getGroupNumber());
			this.buffer.writeUInt16(this.reconstructionDiameter.getElementNumber());
			this.buffer.writeUInt32(this.reconstructionDiameter.getElementLength());
			this.buffer.write(this.reconstructionDiameter.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.receiveCoilName.getGroupNumber());
			this.buffer.writeUInt16(this.receiveCoilName.getElementNumber());
			this.buffer.writeUInt32(this.receiveCoilName.getElementLength());
			this.buffer.write(this.receiveCoilName.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.acquisitionMatrix.getGroupNumber());
			this.buffer.writeUInt16(this.acquisitionMatrix.getElementNumber());
			this.buffer.writeUInt32(this.acquisitionMatrix.getElementLength());
			this.buffer.write(this.acquisitionMatrix.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.inPhaseEncodingDirection.getGroupNumber());
			this.buffer.writeUInt16(this.inPhaseEncodingDirection.getElementNumber());
			this.buffer.writeUInt32(this.inPhaseEncodingDirection.getElementLength());
			this.buffer.write(this.inPhaseEncodingDirection.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.flipAngle.getGroupNumber());
			this.buffer.writeUInt16(this.flipAngle.getElementNumber());
			this.buffer.writeUInt32(this.flipAngle.getElementLength());
			this.buffer.write(this.flipAngle.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.SAR.getGroupNumber());
			this.buffer.writeUInt16(this.SAR.getElementNumber());
			this.buffer.writeUInt32(this.SAR.getElementLength());
			this.buffer.write(this.SAR.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientPosition.getGroupNumber());
			this.buffer.writeUInt16(this.patientPosition.getElementNumber());
			this.buffer.writeUInt32(this.patientPosition.getElementLength());
			this.buffer.write(this.patientPosition.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.studyInstanceUID.getGroupNumber());
			this.buffer.writeUInt16(this.studyInstanceUID.getElementNumber());
			this.buffer.writeUInt32(this.studyInstanceUID.getElementLength());
			this.buffer.write(this.studyInstanceUID.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.seriesInstanceUID.getGroupNumber());
			this.buffer.writeUInt16(this.seriesInstanceUID.getElementNumber());
			this.buffer.writeUInt32(this.seriesInstanceUID.getElementLength());
			this.buffer.write(this.seriesInstanceUID.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.studyID.getGroupNumber());
			this.buffer.writeUInt16(this.studyID.getElementNumber());
			this.buffer.writeUInt32(this.studyID.getElementLength());
			this.buffer.write(this.studyID.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.seriesNumber.getGroupNumber());
			this.buffer.writeUInt16(this.seriesNumber.getElementNumber());
			this.buffer.writeUInt32(this.seriesNumber.getElementLength());
			this.buffer.write(this.seriesNumber.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.acquisitionNumber.getGroupNumber());
			this.buffer.writeUInt16(this.acquisitionNumber.getElementNumber());
			this.buffer.writeUInt32(this.acquisitionNumber.getElementLength());
			this.buffer.write(this.acquisitionNumber.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.instanceNumber.getGroupNumber());
			this.buffer.writeUInt16(this.instanceNumber.getElementNumber());
			this.buffer.writeUInt32(this.instanceNumber.getElementLength());
			this.buffer.write(this.instanceNumber.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.patientOrientation.getGroupNumber());
			this.buffer.writeUInt16(this.patientOrientation.getElementNumber());
			this.buffer.writeUInt32(this.patientOrientation.getElementLength());
			this.buffer.write(this.patientOrientation.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imagePosition.getGroupNumber());
			this.buffer.writeUInt16(this.imagePosition.getElementNumber());
			this.buffer.writeUInt32(this.imagePosition.getElementLength());
			this.buffer.write(this.imagePosition.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imagePositionPatient.getGroupNumber());
			this.buffer.writeUInt16(this.imagePositionPatient.getElementNumber());
			this.buffer.writeUInt32(this.imagePositionPatient.getElementLength());
			this.buffer.write(this.imagePositionPatient.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imageOrientation.getGroupNumber());
			this.buffer.writeUInt16(this.imageOrientation.getElementNumber());
			this.buffer.writeUInt32(this.imageOrientation.getElementLength());
			this.buffer.write(this.imageOrientation.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imageOrientationPatient.getGroupNumber());
			this.buffer.writeUInt16(this.imageOrientationPatient.getElementNumber());
			this.buffer.writeUInt32(this.imageOrientationPatient.getElementLength());
			this.buffer.write(this.imageOrientationPatient.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.frameOfReferenceUID.getGroupNumber());
			this.buffer.writeUInt16(this.frameOfReferenceUID.getElementNumber());
			this.buffer.writeUInt32(this.frameOfReferenceUID.getElementLength());
			this.buffer.write(this.frameOfReferenceUID.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.imagesInAcquisition.getGroupNumber());
			this.buffer.writeUInt16(this.imagesInAcquisition.getElementNumber());
			this.buffer.writeUInt32(this.imagesInAcquisition.getElementLength());
			this.buffer.write(this.imagesInAcquisition.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.positionReferenceIndicator.getGroupNumber());
			this.buffer.writeUInt16(this.positionReferenceIndicator.getElementNumber());
			this.buffer.writeUInt32(this.positionReferenceIndicator.getElementLength());
			this.buffer.write(this.positionReferenceIndicator.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.sliceLocator.getGroupNumber());
			this.buffer.writeUInt16(this.sliceLocator.getElementNumber());
			this.buffer.writeUInt32(this.sliceLocator.getElementLength());
			this.buffer.write(this.sliceLocator.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.samplesPerPixel.getGroupNumber());
			this.buffer.writeUInt16(this.samplesPerPixel.getElementNumber());
			this.buffer.writeUInt32(this.samplesPerPixel.getElementLength());
			this.buffer.writeUInt16(this.samplesPerPixel.getIntElementData());
			
			this.buffer.writeUInt16(this.photometricInterpretation.getGroupNumber());
			this.buffer.writeUInt16(this.photometricInterpretation.getElementNumber());
			this.buffer.writeUInt32(this.photometricInterpretation.getElementLength());
			this.buffer.write(this.photometricInterpretation.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.rows.getGroupNumber());
			this.buffer.writeUInt16(this.rows.getElementNumber());
			this.buffer.writeUInt32(this.rows.getElementLength());
			this.buffer.writeUInt16(this.rows.getIntElementData());
			
			this.buffer.writeUInt16(this.columns.getGroupNumber());
			this.buffer.writeUInt16(this.columns.getElementNumber());
			this.buffer.writeUInt32(this.columns.getElementLength());
			this.buffer.writeUInt16(this.columns.getIntElementData());
			
			this.buffer.writeUInt16(this.pixelSpacing.getGroupNumber());
			this.buffer.writeUInt16(this.pixelSpacing.getElementNumber());
			this.buffer.writeUInt32(this.pixelSpacing.getElementLength());
			this.buffer.write(this.pixelSpacing.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.bitsAllocated.getGroupNumber());
			this.buffer.writeUInt16(this.bitsAllocated.getElementNumber());
			this.buffer.writeUInt32(this.bitsAllocated.getElementLength());
			this.buffer.writeUInt16(this.bitsAllocated.getIntElementData());
			
			this.buffer.writeUInt16(this.bitsStored.getGroupNumber());
			this.buffer.writeUInt16(this.bitsStored.getElementNumber());
			this.buffer.writeUInt32(this.bitsStored.getElementLength());
			this.buffer.writeUInt16(this.bitsStored.getIntElementData());
			
			this.buffer.writeUInt16(this.highBit.getGroupNumber());
			this.buffer.writeUInt16(this.highBit.getElementNumber());
			this.buffer.writeUInt32(this.highBit.getElementLength());
			this.buffer.writeUInt16(this.highBit.getIntElementData());
			
			this.buffer.writeUInt16(this.pixelRepresentation.getGroupNumber());
			this.buffer.writeUInt16(this.pixelRepresentation.getElementNumber());
			this.buffer.writeUInt32(this.pixelRepresentation.getElementLength());
			this.buffer.writeUInt16(this.pixelRepresentation.getIntElementData());
			
			this.buffer.writeUInt16(this.smallestImagePixelValue.getGroupNumber());
			this.buffer.writeUInt16(this.smallestImagePixelValue.getElementNumber());
			this.buffer.writeUInt32(this.smallestImagePixelValue.getElementLength());
			this.buffer.writeUInt16(this.smallestImagePixelValue.getIntElementData());
			
			this.buffer.writeUInt16(this.largestImagePixelValue.getGroupNumber());
			this.buffer.writeUInt16(this.largestImagePixelValue.getElementNumber());
			this.buffer.writeUInt32(this.largestImagePixelValue.getElementLength());
			this.buffer.writeUInt16(this.largestImagePixelValue.getIntElementData());
			
			this.buffer.writeUInt16(this.pixelPaddingValue.getGroupNumber());
			this.buffer.writeUInt16(this.pixelPaddingValue.getElementNumber());
			this.buffer.writeUInt32(this.pixelPaddingValue.getElementLength());
			this.buffer.writeUInt16(this.pixelPaddingValue.getIntElementData());
			
			this.buffer.writeUInt16(this.windowCenter.getGroupNumber());
			this.buffer.writeUInt16(this.windowCenter.getElementNumber());
			this.buffer.writeUInt32(this.windowCenter.getElementLength());
			this.buffer.write(this.windowCenter.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.windowWidth.getGroupNumber());
			this.buffer.writeUInt16(this.windowWidth.getElementNumber());
			this.buffer.writeUInt32(this.windowWidth.getElementLength());
			this.buffer.write(this.windowWidth.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.rescaleIntercept.getGroupNumber());
			this.buffer.writeUInt16(this.rescaleIntercept.getElementNumber());
			this.buffer.writeUInt32(this.rescaleIntercept.getElementLength());
			this.buffer.write(this.rescaleIntercept.getElementData().getBytes());

			this.buffer.writeUInt16(this.rescaleSlope.getGroupNumber());
			this.buffer.writeUInt16(this.rescaleSlope.getElementNumber());
			this.buffer.writeUInt32(this.rescaleSlope.getElementLength());
			this.buffer.write(this.rescaleSlope.getElementData().getBytes());
			
			this.buffer.writeUInt16(this.rescaleType.getGroupNumber());
			this.buffer.writeUInt16(this.rescaleType.getElementNumber());
			this.buffer.writeUInt32(this.rescaleType.getElementLength());
			this.buffer.write(this.rescaleType.getElementData().getBytes());

			
		}
		
		catch (Exception e) {
			
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public void writeImageToBuffer() {
		
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
