/**
 * 
 */
package com.yang.tools;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * @ClassName: Base64Util
 * @package com.arvato.hnair.modules.exchange.util
 * @Description: BASE64工具类
 * @author Mr.yang
 * @date 2016年1月18日 下午5:37:23
 * @version V1.0
 */

public class Base64Utils {
	/**
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decode(String str) throws UnsupportedEncodingException {
		return Base64.decodeBase64(str.getBytes("utf-8"));
	}

	/**
	 * 二进制数据编码为BASE64字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String encode(String str) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(str.getBytes("utf-8")));
	}

}
