package com.gateway.dicom.entities;

public class ImplementationItem extends DICOMItem {

	protected ImplementationClassUIDSubItem implementationClassUIDSubItem;
	protected ImplementationVersionNameSubItem implementationVersionNameSubItem;
	
	public ImplementationItem(ImplementationClassUIDSubItem implementationClassUIDSubItem,
			ImplementationVersionNameSubItem implementationVersionNameSubItem) {
		this.implementationClassUIDSubItem = implementationClassUIDSubItem;
		this.implementationVersionNameSubItem = implementationVersionNameSubItem;
	}
	
	public ImplementationItem() {} 

	public ImplementationClassUIDSubItem getImplementationClassUIDSubItem() {
		return implementationClassUIDSubItem;
	}

	public void setImplementationClassUIDSubItem(ImplementationClassUIDSubItem implementationClassUIDSubItem) {
		this.implementationClassUIDSubItem = implementationClassUIDSubItem;
	}

	public ImplementationVersionNameSubItem getImplementationVersionNameSubItem() {
		return implementationVersionNameSubItem;
	}

	public void setImplementationVersionNameSubItem(ImplementationVersionNameSubItem implementationVersionNameSubItem) {
		this.implementationVersionNameSubItem = implementationVersionNameSubItem;
	}
	
	
	
}
