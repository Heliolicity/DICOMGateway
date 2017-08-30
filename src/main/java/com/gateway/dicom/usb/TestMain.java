package com.gateway.dicom.usb;

import java.util.List;

import javax.usb.*;
import javax.usb.event.*;
import javax.usb.util.*;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		USBReader reader = new USBReader();
		reader.run();
		
		/*try {
        
			UsbServices services = UsbHostManager.getUsbServices();
			dump(services.getRootUsbHub(), 0);
			
		}
		
		catch (UsbException usbe) {
			
		}*/
		
	}

    public static void dump(UsbDevice device, int level) {
        
    	try {
    	
	    	pl("Device: " + device.toString());
	        pl("Manufacturer: " + device.getManufacturerString());
	    	pl("Product: " + device.getProductString());
	    	pl("Serial Number: " + device.getSerialNumberString());
	        
	        if (device.isUsbHub()) {
	            
	        	final UsbHub hub = (UsbHub) device;
	            
	        	for (UsbDevice child: (List<UsbDevice>) hub.getAttachedUsbDevices()) 
	                
	        		dump(child, level + 1);
	            
	        }
	        
	        else {
	        	
	        	pl("Device is not a USB hub");
	        	
	        }
    
    	}
    	
    	catch (Exception e) {
    		
    		pl(e.getMessage());
    		//e.printStackTrace();
    		
    	}
        
    }
	
    private static void pl(String s) { System.out.println(s); }
    
}
