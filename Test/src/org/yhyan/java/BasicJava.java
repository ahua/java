package org.yhyan.java;

import java.util.ArrayList;
import java.util.List;

public class BasicJava {
	public static void main(String[] args) {
		try {
			int a = 0;
		} catch (Exception e) {
			System.exit(0);
		}
		
        double val;
        try {
            val = Float.parseFloat(null);
        } catch (Exception e) {
            System.out.println("Invalid double: " + null);
            val = Double.NaN;
        }
        System.out.println(val);

//		List<Integer> t = new ArrayList<Integer>();
//		for (int i = 0; i < 5; ++i) {
//			t.add(Integer.valueOf(i));
//		}
//
//		t = t.subList(2, 5);
//		System.out.println(t.size());
//
//		//		System.out.println(a);
//		
//		
//		coords(-87.0, -128);
//		coords(90, 180);
//		
//		System.out.println(Math.pow(2, 0));
	}

	public static double[] coords(double latitude, double longitude) {
		double rlat = Math.toRadians(latitude);
		double rlong = Math.toRadians(longitude);
		double nlat = rlong * Math.cos(rlat);
		double r[] = { nlat, rlong };
		
		System.out.println(rlat + " " + rlong + " " + nlat);
		return r;

	}
}
