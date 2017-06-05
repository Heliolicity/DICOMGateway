package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PresentationContext_RQ extends DICOMItem {

	//private byte itemType = 0x20;
	private int presentationContextID = 1; //This should be an unsigned binary integer - does this need special handling?
	private List<TransferSyntax> transferSyntaxSubItems;
	private AbstractSyntax abstractSyntaxSubItem;
	
	public PresentationContext_RQ(byte itemType, 
			int presentationContextID,
			List<TransferSyntax> transferSyntaxSubItems, 
			AbstractSyntax abstractSyntaxSubItem) {
		
		super();
		this.itemType = itemType;
		//this.presentationContextID = this.convertDecToBin(presentationContextID);
		this.presentationContextID = presentationContextID;
		this.transferSyntaxSubItems = transferSyntaxSubItems;		
		this.abstractSyntaxSubItem = abstractSyntaxSubItem;
		//this.determineLength();
		
		this.abstractSyntaxSubItem.writeToBuffer();
		int a = this.abstractSyntaxSubItem.getStream().size();
		
		int b = 0;
		
		for (TransferSyntax transferSyntax : this.transferSyntaxSubItems)  {
			
			transferSyntax.writeToBuffer();
			b += transferSyntax.getStream().size();
			
		}
		
		this.itemLength = 4 + a + b;
		
	}
	
	public PresentationContext_RQ() { super(); }
	
	public int getPresentationContextID() {
		return presentationContextID;
	}

	public void setPresentationContextID(int presentationContextID) {
		this.presentationContextID = presentationContextID;
	}

	public List<TransferSyntax> getTransferSyntaxSubItems() {
		return transferSyntaxSubItems;
	}

	public void setTransferSyntaxSubItem(List<TransferSyntax> transferSyntaxSubItems) {
		this.transferSyntaxSubItems = transferSyntaxSubItems; 
	}

	public AbstractSyntax getAbstractSyntaxSubItem() {
		return abstractSyntaxSubItem;
	}

	public void setAbstractSyntaxSubItem(AbstractSyntax abstractSyntaxSubItem) {
		this.abstractSyntaxSubItem = abstractSyntaxSubItem;
	}
	
	public void determineLength() {
		
		int length = 1 + 1 + 2 + this.abstractSyntaxSubItem.getItemLength();
		
		for (TransferSyntax transferSyntax : this.transferSyntaxSubItems) 
			
			length += 1 + 1 + 2 + transferSyntax.getItemLength();
		
		length += 1 + 1 + 1 + 1;
		
		this.itemLength = length;
		
	}
	
	public void writeToBuffer() {
		
		try {
			
			this.stream = new ByteArrayOutputStream();
			this.stream.write(this.itemType);
			this.stream.write(this.reserved);
			this.stream.write(this.itemLength);
			this.stream.write(this.presentationContextID);
			this.stream.write(this.reserved);
			this.stream.write(this.reserved);
			this.stream.write(this.reserved);
			this.stream.writeTo(this.abstractSyntaxSubItem.getStream());
			
			for (TransferSyntax transferSyntax : this.transferSyntaxSubItems) 
				
				this.stream.writeTo(transferSyntax.getStream());
			
			
		}
		
		catch (Exception e) {
			
			this.pl(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
}
