package com.gateway.dicom.entities;

public class PresentationContext_RQ {

	private byte itemType = 0x20;
	private byte reserved = 0x00;
	private int itemLength;
	private int presentationContextID;
	private TransferSyntax transferSyntaxSubItem;
	private AbstractSyntax abstractSyntaxSubItem;
	
	public PresentationContext_RQ(int presentationContextID,
			TransferSyntax transferSyntaxSubItem, 
			AbstractSyntax abstractSyntaxSubItem) {
		super();
		this.presentationContextID = presentationContextID;
		this.transferSyntaxSubItem = transferSyntaxSubItem;		
		this.abstractSyntaxSubItem = abstractSyntaxSubItem;
	}
	
	public PresentationContext_RQ() { super(); }

	public byte getItemType() {
		return itemType;
	}

	public void setItemType(byte itemType) {
		this.itemType = itemType;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	public int getItemLength() {
		return itemLength;
	}

	public void setItemLength(int itemLength) {
		this.itemLength = itemLength;
	}

	public int getPresentationContextID() {
		return presentationContextID;
	}

	public void setPresentationContextID(int presentationContextID) {
		this.presentationContextID = presentationContextID;
	}

	public TransferSyntax getTransferSyntaxSubItem() {
		return transferSyntaxSubItem;
	}

	public void setTransferSyntaxSubItem(TransferSyntax transferSyntaxSubItem) {
		this.transferSyntaxSubItem = transferSyntaxSubItem;
	}

	public AbstractSyntax getAbstractSyntaxSubItem() {
		return abstractSyntaxSubItem;
	}

	public void setAbstractSyntaxSubItem(AbstractSyntax abstractSyntaxSubItem) {
		this.abstractSyntaxSubItem = abstractSyntaxSubItem;
	}
	
	
	
}
