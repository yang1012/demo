package com.yang.tools;

import java.io.File;

import org.apache.log4j.Logger;

import com.yang.message.LogMessageEnum;


/**
 * @ClassName: FileUtils
 * @package com.yang.tools
 * @Description: 读取文件工具类--资源文件为项目资源相对路径
 * @author Mr.yang
 * @date 2016年1月10日 下午3:34:27
 * @version V1.0
 */
public class FileUtils {

	/** 日志记录 */
	private static Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * @Title: getFiles
	 * @Description: 获取文件/文件目录下的所有文件。（注意不会进行层级检索，只会加载一级下的所有文件）
	 * @param filePath
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月10日 下午4:13:40
	 */
	public static File[] getFiles(String filePath) {

		// 日志信息
		LogMessageEnum msg = null;

		File[] files = null;

		// 上下文路径-class文件父级目录
		String class_path = getClassPath();
		if (Utils.isNotNull(class_path)) {
			File root_file = new File(getClassPath() + filePath);

			if (Utils.isNotNull(root_file) && root_file.exists()) {
				// 文件/目录存在
				if (root_file.isDirectory()) {
					// 目录文件
					files = root_file.listFiles();
				} else {
					// 资源文件
					files = new File[1];
					files[0] = root_file;
				}

				// 成功获取文件
				msg = LogMessageEnum.LOG_INFO_FILE_FOUND;

				logger.info(msg.getValue_pro() + "-filePath:" + class_path + filePath);

			} else {
				// 文件不存在
				msg = LogMessageEnum.LOG_WARN_FILE_NOT_FOUND;

				logger.warn(msg.getValue_pro() + "-filePath:" + class_path + filePath);
			}
		}

		return files;
	}

	/**
	 * @Title: getClassPath
	 * @Description: 获取文件当前线程上下文路径--classes文件父级路径
	 * @return String
	 * @author Mr.yang
	 * @date 2016年1月10日 下午5:06:39
	 */
	public static String getClassPath() {
		String currenFilePath = "";
		File file = null;
		try {
			currenFilePath = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
			file = new File(currenFilePath);
			while (!(file.getName().toUpperCase().equals("CLASSES"))) {
				// 上一级目录文件
				file = file.getParentFile();
			}
			// 返回绝对路径
			return file.getAbsolutePath();
		} catch (Exception e) {

			// 005:获取文件当前线程上下文路径失败！
			logger.error(LogMessageEnum.LOG_ERROR_GET_CLASSPATH_FAILED.getValue_pro(), e);
		}

		return null;
	}
}
