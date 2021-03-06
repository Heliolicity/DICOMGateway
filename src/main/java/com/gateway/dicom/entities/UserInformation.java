package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;

import com.gateway.dicom.lib.DicomOutputBuffer;

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

	public void writeToStream() {
		
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
	
	public void writeToBuffer() {
		
		try {
			
			this.maximumLengthSubItem.writeToBuffer();
			int a = this.maximumLengthSubItem.getBuffer().size();
			
			this.implementationItem.implementationClassUIDSubItem.writeToBuffer();
			int b = this.implementationItem.implementationClassUIDSubItem.getBuffer().size();
			
			this.implementationItem.implementationVersionNameSubItem.writeToBuffer();
			int c = this.implementationItem.implementationVersionNameSubItem.getBuffer().size();
			
			int d = 0;
			int e = 0;
			int f = 0;
			
			if (! (this.asynchronousOperationsWindowSubItem == null)) {
				
				this.asynchronousOperationsWindowSubItem.writeToBuffer();
				d = this.asynchronousOperationsWindowSubItem.getBuffer().size();
				//this.buffer.write(this.asynchronousOperationsWindowSubItem.getBuffer().toByteArray());
				
			}
			
			if (! (this.scpSCURoleSelectionNegotiationSubItem == null)) {
			
				this.scpSCURoleSelectionNegotiationSubItem.writeToBuffer();
				e = this.scpSCURoleSelectionNegotiationSubItem.getBuffer().size();
				//this.buffer.write(this.scpSCURoleSelectionNegotiationSubItem.getBuffer().toByteArray());
			
			}
			
			if (! (this.extendedNegotiationSubItem == null)) {
			
				this.extendedNegotiationSubItem.writeToBuffer();
				f = this.extendedNegotiationSubItem.getBuffer().size();
				//this.buffer.write(this.extendedNegotiationSubItem.getBuffer().toByteArray());
			
			}
			
			this.itemLength = a + b + c + d + e + f;
			
			this.buffer = new DicomOutputBuffer(DicomOutputBuffer.BYTE_ORDERING_BIG_ENDIAN);
			this.buffer.writeUInt8(this.itemType);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt8(this.reserved);
			this.buffer.writeUInt8(this.itemLength);
			this.buffer.write(this.maximumLengthSubItem.getBuffer().toByteArray());
			this.buffer.write(this.implementationItem.implementationClassUIDSubItem.getBuffer().toByteArray());
			this.buffer.write(this.implementationItem.implementationVersionNameSubItem.getBuffer().toByteArray());
			
			if (! (this.asynchronousOperationsWindowSubItem == null)) 
				
				this.buffer.write(this.asynchronousOperationsWindowSubItem.getBuffer().toByteArray());
			
			if (! (this.scpSCURoleSelectionNegotiationSubItem == null)) 
			
				this.buffer.write(this.scpSCURoleSelectionNegotiationSubItem.getBuffer().toByteArray());
			
			if (! (this.extendedNegotiationSubItem == null)) 
			
				this.buffer.write(this.extendedNegotiationSubItem.getBuffer().toByteArray());
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
