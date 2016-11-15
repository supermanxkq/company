package com.ccservice.elong.inter;
import java.util.UUID;
/**
 * 生成GUID
 * @author 师卫林
 *
 */
public class CreateGUID {
	public static void main(String[] args) {
		System.out.println(createGUID());
	}
	public static String createGUID(){
		UUID uuid = UUID.randomUUID();
		String guid = uuid.toString();
		//System.out.println(uuid.toString());
		//System.out.println(a.length());
		return guid;
	}

}
