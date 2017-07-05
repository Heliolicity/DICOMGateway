package com.gateway.dicom.test;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

public final class BitJuggler {
	 
    //================================================
    // members
    //================================================
    private final long value;
    private static final LongBinaryOperator SET = (x, i) -> x | (1L << i);
    private static final LongBinaryOperator CLEAR = (x, i) -> x & ~(1L << i);
    private static final LongBinaryOperator TOGGLE = (x, i) -> x ^ (1L << i);
     
    //================================================
    // constructors
    //================================================
    private BitJuggler(long v) {
        value = v;
    }
     
    //================================================
    // public methods
    //================================================
    public static BitJuggler initialize(long v) {
        return new BitJuggler(v);
    }
     
    //----------------------------
    public BitJuggler setBits(long... bits) {
        return reduce(value, SET, bits);
    }
     
    //----------------------------
    public BitJuggler clearBits(long... bits) {
        return reduce(value, CLEAR, bits);
    }
     
    //----------------------------
    public BitJuggler toggleBits(long... bits) {
        return reduce(value, TOGGLE, bits);
    }
     
    //----------------------------
    public long getValue() {
        return value;
    }
     
    //----------------------------
    public static void main(String... args) {
        /*byte b = -127;
        System.out.println("input:  "+ Long.toBinaryString(b));
        long c = BitJuggler.initialize(b).clearBits(1, 2).setBits(3).toggleBits(5, 6).getValue();
        System.out.println("output: "+ Long.toBinaryString(c));
        byte d = (byte) c;
        System.out.format("%o%n", d);*/
    	
    	int b = 128;
    	pl("input:  " + Long.toBinaryString(b));
    	long c = BitJuggler.initialize(b).clearBits(1, 2).getValue();
    	pl("Cleared bits 1 and 2:  " + Long.toBinaryString(c));
    	c = BitJuggler.initialize(b).clearBits(1, 2).setBits(6).getValue();
    	pl("NEW FIGURE: " + c);
    	pl("Set bit 3:  " + Long.toBinaryString(c));
    	c = BitJuggler.initialize(b).clearBits(1, 2).setBits(3).toggleBits(5, 6).getValue();
    	pl("Toggled bits 5 and 6:  " + Long.toBinaryString(c));
    	
    	b = 0;
    	pl("input:  " + Long.toBinaryString(b));
    	c = BitJuggler.initialize(b).setBits(6).getValue();
    	pl("NEW FIGURE: " + c);
    	pl("Set bit 3:  " + Long.toBinaryString(c));
    	c = BitJuggler.initialize(b).clearBits(1, 2).setBits(3).toggleBits(5, 6).getValue();
    	pl("Toggled bits 5 and 6:  " + Long.toBinaryString(c));
    	
    }
     
    //================================================
    // private methods
    //================================================
    private BitJuggler reduce(long v, LongBinaryOperator lbo, long... bits) {
        return new BitJuggler(Arrays.stream(bits).reduce(v, lbo));
    }
    
    private static void pl(String s) { System.out.println(s); }
    
    //================================================
    // end of class
    //================================================
}