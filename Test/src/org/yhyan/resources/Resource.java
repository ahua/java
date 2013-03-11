package org.yhyan.resources;

import java.util.ResourceBundle;

public class Resource {
	public static void main(String[] args){
		ResourceBundle rb = ResourceBundle.getBundle("feedback");
		if(rb == null){
			throw new IllegalArgumentException("cannot find the feedback.properties");
		}
		
		int n = Integer.parseInt(rb.getString("feedback.n"));
		float[] w = new float[n]; 
		
		String[] weights = rb.getString("feedback.weights").split(",");
		for(int i = 0; i < n; ++i){
			if(i < weights.length){
				w[i] = Float.parseFloat(weights[i]);
			}else{
				w[i] = 0;
			}
		}
		
		int period = Integer.parseInt(rb.getString("feedback.period"));
		float upcount = Float.parseFloat(rb.getString("feedback.upcount"));
		
		debug_print(period);
		debug_print(upcount);
		for(int i = 0; i < n; ++i){
			debug_print(w[i]);
		}
	}
	
	public static boolean DEBUG = true;
	public static void debug_print(String s){
		if(DEBUG){
			System.out.println(s);
		}
	}
	public static void debug_print(float f){
		if(DEBUG){
			System.out.println(f);
		}
	}
	public static void debug_print(int i){
		if(DEBUG){
			System.out.println(i);
		}
	}
}
