package com.czt.log.util;

import java.security.MessageDigest;

public class MD5Util {
	/**
	 * JAVA的md5操作
	 * 
	 * @author 陈潼
	 * 
	 */
	// 进制转换
	private static String hexDigit(byte x) {
		StringBuffer sb = new StringBuffer();
		char c;
		// First nibble
		c = (char) ((x >> 4) & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}
		sb.append(c);
		// Second nibble
		c = (char) (x & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}
		sb.append(c);
		return sb.toString();
	}

	// 对字符串加密
	static public String makeMD5(String text) {
		//
		MessageDigest md5;
		try {
			// 生成一个MD5加密计算摘要
			md5 = MessageDigest.getInstance("MD5"); // 计算md5函数
			byte b[] = text.getBytes();
			md5.update(b);
			// digest()最后确定返回md5 hash值，返回值为8wei字符串。因为md5
			// hash值是16位的hex值，实际上就是8位的字符
			byte digest[] = md5.digest();
			StringBuffer hexString = new StringBuffer();
			int digestLength = digest.length;
			for (int i = 0; i < digestLength; i++) {
				hexString.append(hexDigit(digest[i]));
			}
			return hexString.toString();
		} catch (Exception e) {
		}
		return text;
	}
}
