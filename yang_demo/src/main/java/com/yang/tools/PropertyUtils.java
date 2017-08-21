package com.yang.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.yang.message.LogMessageEnum;


/**
 * @ClassName: PropertyUtils
 * @package com.yang.tools
 * @Description: 属性文件(.properties)工具类--资源文件为项目资源相对路径
 * @author Mr.yang
 * @date 2016年1月10日 下午3:25:37
 * @version V1.0
 */
public class PropertyUtils {
	/** 日志记录 */
	private static Logger logger = Logger.getLogger(PropertyUtils.class);

	/** 资源文件根路径 ---相对路径 */
	private static String root_file_path = "\\conf\\";

	/** 是否启用加密 (注意如果启用加密，请确保资源文件信息已经加密，并且和解密规则一致) */
	private static boolean use_encrypy = false;

	/** 属性资源 k-v 缓存 */
	private static Map<String, Properties> prop_map = null;

	/**
	 * 静态块初始化加载根目录下所有资源(.properties)文件
	 */
	static {

		// 获取所有资源文件
		File[] property_files = FileUtils.getFiles(root_file_path);

		if (Utils.isNotNull(property_files)) {
			// 初始化加载所有.properties资源文件
			prop_map = new HashMap<String, Properties>();

			// 日志信息
			LogMessageEnum msg = null;
			// 文件名
			String file_name = "";
			// 输入流
			InputStream in = null;
			// 属性集
			Properties properties = null;
			// 属性集键
			String key = null;
			for (File each : property_files) {

				if (Utils.isNotNull(each)) {
					// 文件路径/
					file_name = each.getName();

					if (file_name.indexOf(".") != -1 && file_name.substring(file_name.lastIndexOf(".")).equals(".properties")) {
						// 资源文件.properties
						// 属性集键
						key = file_name;
						// 属性集
						properties = new Properties();
						try {
							// 属性集输入流
							in = new FileInputStream(each);
							properties.load(in);

							prop_map.put(key, properties);

							msg = LogMessageEnum.LOG_INFO_LOAD_PRO_SUCCESS;

							logger.info(msg.getValue_pro() + "-file:" + each.getPath());

						} catch (Exception e) {

							// 初始化加载资源文件配置信息失败
							msg = LogMessageEnum.LOG_WARN_LOAD_PRO_FAILED;

							logger.warn(msg.getValue_pro() + "-file:" + each.getPath(), e);
						}
					}
				}
			}
		}
	}

	/**
	 * @Title: getProperty
	 * @Description: 根据资源文件名读取其某个属性
	 * @param property_name
	 * @param file_name
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月10日 下午4:55:43
	 */
	public static String getProperty(String property_name, String file_name) {

		// 属性值
		String property_value = "";

		if (Utils.isNotNull(prop_map)) {
			// 属性集
			Properties properties = prop_map.get(file_name);
			if (Utils.isNotNull(properties)) {
				// 存当前资源文件的属性集
				property_value = properties.getProperty(property_name);
			}
		}

		if (use_encrypy) {
			// 启用加密,执行解密
			// TODO
		}
		return property_value;
	}

	/**
	 * @Title: getProperty
	 * @Description: 根据某个属性(若存在多个资源文件中属性值有同名的情况，不保证返回的属性值的正确性，只返回检索到的第一个文件中的属性值
	 *               )
	 * @param property_name
	 * @param file_name
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月10日 下午4:55:43
	 */
	public static String getProperty(String property_name) {

		// 属性值
		String property_value = "";

		if (Utils.isNotNull(prop_map)) {
			// 属性集
			Properties properties = null;

			Iterator<String> it = prop_map.keySet().iterator();
			// 属性集 键
			String key = "";
			// 循环遍历检索第一次出现的属性值
			while (it.hasNext() && Utils.isNull(property_value)) {
				key = it.next();
				properties = prop_map.get(key);
				if (Utils.isNotNull(properties)) {
					property_value = properties.getProperty(property_name);
				}
			}

		}

		if (use_encrypy) {
			// 启用加密,执行解密
			// TODO
		}
		return property_value;
	}

	public static void main(String[] args) {

		//System.out.println(PropertyUtils.getProperty(PointsConstant.GET_ALL_ORDERS,PointsConstant.POINTS_PRO));
	}
}
