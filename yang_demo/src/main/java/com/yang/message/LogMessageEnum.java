package com.yang.message;

/**
 * @ClassName: LogMessageEnum
 * @package com.yang.information
 * @Description: 日志提醒枚举类
 * @author Mr.yang
 * @date 2016年1月10日 下午3:49:20
 * @version V1.0
 */
public enum LogMessageEnum {

	LOG_INFO_FILE_FOUND("001:成功获取文件！:成功获取文件！"),

	LOG_WARN_FILE_NOT_FOUND("002:文件不存在！:文件不存在！"),

	LOG_WARN_LOAD_PRO_FAILED("003:初始化加载资源文件配置信息失败！:初始化加载资源文件配置信息失败！"),

	LOG_INFO_LOAD_PRO_SUCCESS("004:成功初始化加载资源文件配置信息！:成功初始化加载资源文件配置信息！"),

	LOG_ERROR_GET_CLASSPATH_FAILED("005:获取文件当前线程上下文路径失败！:获取文件当前线程上下文路径失败！");

	/** 枚举值 信息编码code */
	private String code;

	/** 枚举值 value_pro 程序信息 */
	private String value_pro;

	/** 枚举值 value_user 用户查看信息 */
	private String value_user;

	/**
	 * @Title: 构造
	 * @Description: 初始化
	 * @param _enum
	 *            当前枚举值
	 * @author : Mr.yang
	 */
	private LogMessageEnum(String _enum) {
		// 键值对
		String[] strs = _enum.split(":");

		// 枚举值 信息编码code
		this.code = strs[0];

		// 枚举值 程序信息
		this.value_pro = strs[1];

		// 枚举值 用户查看信息
		this.value_pro = strs[2];
	}

	/**
	 * 获取 信息编码code
	 * 
	 * @return code code
	 */

	public String getCode() {
		return code;
	}

	/**
	 * 获取 程序信息
	 * 
	 * @return value_pro value_pro
	 */

	public String getValue_pro() {
		return value_pro;
	}

	/**
	 * 获取 用户查看信息
	 * 
	 * @return value_user value_user
	 */

	public String getValue_user() {
		return value_user;
	}

}
