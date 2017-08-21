package com.yang.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 获取对称加密后键值
 * 
 * @author dingyaming
 * 
 */
public class KeyboardUtil {
	private static final String[] key = { "!", "\"", "#", "$", "%", "&", "'",
			"(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A",
			"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[",
			"\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z", "{", "|", "}", "~" };

	public static Map<String, String> EncodeKeyboard(String keyword) {
		Map<String, String> map = new HashMap<String, String>();
		for (String string : key) {
			String string_tmp = AESUtil.encryptByStr(string, keyword);
			map.put(string, string_tmp);
		}
		return map;
	}

	public static String DecodeKeyboard(String password, String keyword) {
		if (password == null || password.trim().isEmpty()) {
			return "";
		}
		String[] pwList = password.split("\\|");
		StringBuffer tmp = new StringBuffer();
		for (String string : pwList) {
			tmp.append(AESUtil.decryptByStr(string, keyword));
		}
		return tmp.toString();
	}

	public static void main(String[] args) {
		String password = UUID.randomUUID().toString();
		// 加密
		System.out.println("秘钥：" + password);
		Map<String, String> map = EncodeKeyboard(password);
		String json = JSON.toJSONString(map);
		System.out.println(json);
		String p1 = "123";
		char[] a = p1.toCharArray();
		StringBuffer tmp = new StringBuffer();
		for (char c : a) {
			tmp.append("|").append(map.get(Character.toString(c)));
		}
		String keytmp = tmp.toString().substring(1);
		System.out.println(keytmp);

		System.out.println(DecodeKeyboard(keytmp, password));
	}
}
