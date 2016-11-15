package com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method;

public class fantao {
	public static void main(String[] args) {
		String s = "改签票款差价：0.0  元";
		s = s.split("：")[1];
		System.out.println(s);
		System.out.println(Double.valueOf(s.split("元")[0]));
		System.out.println(s.split("元")[0].trim());
//		System.out.println(s.split("元")[1]);
		
	}
}
