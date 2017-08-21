package com.yang.tools;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Util
 * @Description: 常用公共方法的调空
 * @author 杨森
 * @date 2015-3-12 上午10:23:07
 */
public class Utils {

	/**
	 * @Title: isNull
	 * @Description: 判断字符串是否为空
	 * @param str
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isNull(String str) {
		return null == str || "".equals(str.trim()) || str.trim().equals("null") || str.trim().equals("NULL");
	}

	/**
	 * @Title: isNotNull
	 * @Description: 判断字符串是否不为空
	 * @param str
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isNotNull(String str) {
		return !isNull(str);
	}

	/**
	 * @Title: isNull
	 * @Description:判断集合<List>是否为空
	 * @param list
	 * @return boolean 返回类型
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNull(List list) {
		return null == list || list.size() == 0;
	}

	/**
	 * @Title: isNull
	 * @Description:判断集合<List>是否不为空
	 * @param list
	 * @return boolean 返回类型
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotNull(List list) {
		return !isNull(list);
	}

	/**
	 * @Title: isNull
	 * @Description: 判断Object是否为空
	 * @param obj
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isNull(Object obj) {
		return null == obj;
	}

	/**
	 * @Title: isNotNull
	 * @Description: 判断Object是否不为空
	 * @param obj
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isNotNull(Object obj) {
		return !isNull(obj);
	}

	/**
	 * @Title: isNull
	 * @Description: 判断Map是否为空
	 * @author: 杨森
	 * @param map
	 * @return boolean
	 * @date 2015-3-24 下午10:46:49
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNull(Map map) {
		return map == null || map.size() == 0;
	}

	/**
	 * @Title: isNull
	 * @Description: 判断Map是否不为空
	 * @author: 杨森
	 * @param map
	 * @return boolean
	 * @date 2015-3-24 下午10:46:49
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotNull(Map map) {
		return !isNull(map);
	}

	/**
	 * @Title: isNull
	 * @Description: 判断字符串数组是否为空
	 * @author: 杨森
	 * @param str
	 * @return
	 * @date 2015-5-13 下午04:14:38
	 */
	public static boolean isNull(String[] str) {
		return null == str || str.length == 0;
	}

	/**
	 * @Title: isNull
	 * @Description: 判断字符串数组是否不为空
	 * @author: 杨森
	 * @param str
	 * @return
	 * @date 2015-5-13 下午04:14:38
	 */
	public static boolean isNotNull(String[] str) {
		return !isNull(str);
	}

}
