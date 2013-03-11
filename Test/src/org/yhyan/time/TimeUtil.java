package org.yhyan.time;

import java.util.Date;

public class TimeUtil {
	public static void main(String[] args){
		Date date = new Date();
		System.out.println(date.getTime()/1000);
		System.out.println(1L);
		// number of milliseconds since January 1, 1970, 00:00:00 GMT r
		int n = 5;
		double[] a = new double[n];
		for(int i = 0; i < n; i++){
			System.out.println(a[i]);
		}
		arrayTest(a);
		for(int i = 0; i < n; i++){
			System.out.println(a[i]);
		}
	}
	
	public static void arrayTest(double[] a){
		for(int i = 0; i < a.length; ++i){
			a[i]  = a[i] + i * 3;
		}
	}
}
