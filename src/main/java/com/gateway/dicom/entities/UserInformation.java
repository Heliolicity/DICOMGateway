package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

public class UserInformation extends DICOMItem {

	//private byte itemType = 0x50;
	protected MaximumLengthSubItem maximumLengthSubItem;
	protected ImplementationItem implementationItem;
	protected AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem;
	protected SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem;
	protected ExtendedNegotiationSubItem extendedNegotiationSubItem;
	
	public UserInformation(byte itemType, 
			MaximumLengthSubItem maximumLengthSubItem,
			ImplementationItem implementationItem,
			AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem,
			SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem, 
			ExtendedNegotiationSubItem extendedNegotiationSubItem) {
		
		super();
		this.itemType = itemType;
		this.maximumLengthSubItem = maximumLengthSubItem;
		this.implementationItem = implementationItem;
		this.asynchronousOperationsWindowSubItem = asynchronousOperationsWindowSubItem;
		this.scpSCURoleSelectionNegotiationSubItem = scpSCURoleSelectionNegotiationSubItem;
		this.extendedNegotiationSubItem = extendedNegotiationSubItem;
		
		this.maximumLengthSubItem.writeToBuffer();
		int a = this.maximumLengthSubItem.getStream().size();
		
		this.implementationItem.implementationClassUIDSubItem.writeToBuffer();
		int b = this.implementationItem.implementationClassUIDSubItem.getStream().size();
		
		this.implementationItem.implementationVersionNameSubItem.writeToBuffer();
		int c = this.implementationItem.implementationVersionNameSubItem.getStream().size();
		
		this.asynchronousOperationsWindowSubItem.writeToBuffer();
		int d = this.asynchronousOperationsWindowSubItem.getStream().size();
		
		this.scpSCURoleSelectionNegotiationSubItem.writeToBuffer();
		int e = this.scpSCURoleSelectionNegotiationSubItem.getStream().size();
		
		this.extendedNegotiationSubItem.writeToBuffer();
		int f = this.extendedNegotiationSubItem.getStream().size();
		
		this.itemLength = a + b + c + d + e + f;
		
	}

	public UserInformation() { super(); }

	public MaximumLengthSubItem getMaximumLengthSubItem() {
		return maximumLengthSubItem;
	}

	public void setMaximumLengthSubItem(MaximumLengthSubItem maximumLengthSubItem) {
		this.maximumLengthSubItem = maximumLengthSubItem;
	}

	public ImplementationItem getImplementationItem() {
		return implementationItem;
	}

	public void setImplementationItem(ImplementationItem implementationItem) {
		this.implementationItem = implementationItem;
	}

	public AsynchronousOperationsWindowSubItem getAsynchronousOperationsWindowSubItem() {
		return asynchronousOperationsWindowSubItem;
	}

	public void setAsynchronousOperationsWindowSubItem(
			AsynchronousOperationsWindowSubItem asynchronousOperationsWindowSubItem) {
		this.asynchronousOperationsWindowSubItem = asynchronousOperationsWindowSubItem;
	}

	public SCPSCURoleSelectionNegotiationSubItem getScpSCURoleSelectionNegotiationSubItem() {
		return scpSCURoleSelectionNegotiationSubItem;
	}

	public void setScpSCURoleSelectionNegotiationSubItem(
			SCPSCURoleSelectionNegotiationSubItem scpSCURoleSelectionNegotiationSubItem) {
		this.scpSCURoleSelectionNegotiationSubItem = scpSCURoleSelectionNegotiationSubItem;
	}

	public ExtendedNegotiationSubItem getExtendedNegotiationSubItem() {
		return extendedNegotiationSubItem;
	}

	public void setExtendedNegotiationSubItem(ExtendedNegotiationSubItem extendedNegotiationSubItem) {
		this.extendedNegotiationSubItem = extendedNegotiationSubItem;
	}

	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.writeTo(this.maximumLengthSubItem.getStream());
			this.stream.writeTo(this.implementationItem.implementationClassUIDSubItem.getStream());
			this.stream.writeTo(this.implementationItem.implementationVersionNameSubItem.getStream());
			this.stream.writeTo(this.asynchronousOperationsWindowSubItem.getStream());
			this.stream.writeTo(this.scpSCURoleSelectionNegotiationSubItem.getStream());
			this.stream.writeTo(this.extendedNegotiationSubItem.getStream());
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
