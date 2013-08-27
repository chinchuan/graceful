package org.graceful.correct.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 * 
 * @author qin
 * 
 * @date 2013-6-28
 */
public class SecurityUtil {

	
	/**
	 * sha1 加密码
	 * @param text  明文
	 * @return  密文
	 */
	public static String sha1(String text) {
		 //sha1加密
	    MessageDigest md = null;
	    String encrypt = null;
	    try {
	    	md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(text.toString().getBytes()); 
			encrypt = hex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return encrypt;
	}
	
	/**
	 * md5  加密码
	 * @param text  明文
	 * @return  密文
	 */
	public static String md5(String text) {
	   String origMD5 = null;  
        try {  
            MessageDigest md5 = MessageDigest.getInstance("MD5");  
            byte[] result = md5.digest(text.getBytes());  
            origMD5 = hex(result);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return origMD5; 
	}
	
	/**
	 * 返回十六进制字符串
	 * 
	 * @param arr
	 *            字节数组
	 * @return
	 */
	public static String hex(byte[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,3));
		}
		return sb.toString();
	}
}
