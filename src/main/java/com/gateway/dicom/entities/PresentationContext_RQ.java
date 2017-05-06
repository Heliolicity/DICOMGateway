package com.gateway.dicom.entities;

import java.util.ArrayList;
import java.util.List;

public class PresentationContext_RQ extends DICOMItem {

	//private byte itemType = 0x20;
	private int presentationContextID; //This should be an unsigned binary integer - does this need special handling?
	private List<TransferSyntax> transferSyntaxSubItems;
	private AbstractSyntax abstractSyntaxSubItem;
	
	public PresentationContext_RQ(byte itemType, 
			int presentationContextID,
			List<TransferSyntax> transferSyntaxSubItems, 
			AbstractSyntax abstractSyntaxSubItem) {
		super();
		this.itemType = itemType;
		this.presentationContextID = this.convertDecToBin(presentationContextID);
		this.transferSyntaxSubItems = transferSyntaxSubItems;		
		this.abstractSyntaxSubItem = abstractSyntaxSubItem;
		this.determineLength();
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
	
}
