package com.gateway.dicom.entities;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PresentationContext_AC extends DICOMItem {

	private int presentationContextID = 1; //This should be an unsigned binary integer - does this need special handling?
	private int result;
	private List<TransferSyntax> transferSyntaxSubItems;
	private AbstractSyntax abstractSyntaxSubItem;
	
	public PresentationContext_AC(byte itemType, 
			int presentationContextID, 
			int result, 
			List<TransferSyntax> transferSyntaxSubItems, 
			AbstractSyntax abstractSyntaxSubItem) {
		
		super();
		this.itemType = itemType;
		//this.presentationContextID = this.convertDecToBin(presentationContextID);
		this.presentationContextID = presentationContextID;
		this.result = result;
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
	
	public PresentationContext_AC() { super(); }
	
	public String response() {
		
		String response = "";
		
		switch(this.result) {
		
			case 0: response = "Association Request was accepted - acceptance"; break;
			case 1: response = "Association Request was rejected - user rejection"; break;
			case 2: response = "Association Request was rejected - no reason (provider rejection)"; break;
			case 3: response = "Association Request was rejected - abstract syntax not supporter (provider rejection)"; break;
			case 4: response = "Association Request was rejected - transfer syntaxes not supporter (provider rejection)"; break;

		}
		
		return response;
		
	}

	
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
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public void setTransferSyntaxSubItems(List<TransferSyntax> transferSyntaxSubItems) {
		this.transferSyntaxSubItems = transferSyntaxSubItems;
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
