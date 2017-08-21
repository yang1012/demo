package com.yang.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.spreada.utils.chinese.ZHConverter;

/**
 * @ClassName: FontConvertUtil
 * @package com.yang.util
 * @Description: 文件字体转换
 * @author 杨森
 * @date 2015年12月2日 下午2:57:53
 * @version V1.0
 */
public class FontConvertUtil {

	/**
	 * 編碼 utf-8
	 */
	public static final String UTF_8 = "utf-8";

	/**
	 * 编码 big5 繁体
	 */
	public static final String BIG5 = "big5";

	/**
	 * 编码 GB2312
	 */
	public static final String GB2312 = "GB2312";

	/**
	 * @Title: convertToOtherFont
	 * @Description: 简体→繁体，繁体→简体
	 * @param ori_encoding
	 *            原文件编码格式 可以为空
	 * @param font
	 *            目的字体-ZHConverter.SIMPLIFIED：0（繁体），ZHConverter.SIMPLIFIED:1（简体）
	 * @param file_path
	 *            原文件目录路径
	 * @param default_dict
	 *            若不为空，null or "" 替换原文件目录最外层的目录信息（包括文件中同名的路径名）如www.baidu.com/cn
	 *            替换cn为default_dict
	 * @author 杨森
	 * @date 2015年12月2日 下午3:01:03
	 */
	public static void convertToOtherFont(String ori_encoding, int font, String file_path, String default_dict) throws Exception {
		try {

			// 文件类集合
			List<File> file_list = getExistFiles(new File(file_path));
			// 生成转变编码的文件
			if (file_list != null && file_list.size() > 0) {
				convertToOtherFont(ori_encoding, font, file_path, file_list, default_dict);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @Title: getExistFiles
	 * @Description: 通过起始路径名检索该路径下所有文件
	 * @param file_path
	 * @return List<File> 存在的文件集合
	 * @author 杨森
	 * @throws Exception
	 * @date 2015年12月2日 下午3:14:42
	 */
	public static List<File> getExistFiles(File file) throws Exception {
		// 文件类集合
		List<File> file_list = null;
		try {
			file_list = new ArrayList<File>();

			if (file.isDirectory()) {
				// 目录文件
				File[] fileArr = file.listFiles();
				for (File eachFile : fileArr) {
					// 递归调用-检索出所有存在的标准文件
					file_list.addAll(getExistFiles(eachFile));
				}
			}
			if (file.isFile()) {
				// 标准文件
				file_list.add(file);
			}
		} catch (Exception e) {
			throw new Exception("检索文件信息异常!", e);
		}
		return file_list;
	}

	/**
	 * @Title: convertToOtherFont
	 * @Description: 简体→繁体，繁体→简体
	 * @param ori_encoding
	 *            原文件编码格式 可以为空
	 * @param font
	 *            目的字体-ZHConverter.SIMPLIFIED：0（繁体），ZHConverter.SIMPLIFIED:1（简体）
	 * @param file_path
	 *            原文件根起始目录
	 * @param file_list
	 *            文件集合
	 * @param default_dict
	 *            若不为空，null or "" 替换原文件目录最外层的目录信息（包括文件中同名的路径名）如www.baidu.com/cn
	 *            替换cn为default_dict
	 * @author 杨森
	 * @date 2015年12月2日 下午3:01:03
	 */
	public static void convertToOtherFont(String ori_encoding, int font, String file_path, List<File> file_list, String default_dict) throws Exception {
		try {
			// 循环遍历生成转变编码的文件
			ZHConverter converter = ZHConverter.getInstance(font);
			// 默认截取最外层根目录信息，若文件内容中有关该该外层信息的目录，则替换为参数信息
			if (default_dict == null || default_dict == "") {
				default_dict = "转换后的文件路径";
			}

			// 文件路径
			// 最外层根目录创建编码对应的文件目录
			int index = file_path.lastIndexOf("\\");

			// 新的文件路径
			String new_file_path = file_path.substring(0, index + 1) + default_dict;

			// 原文件目录最外层后缀 如www.baidu.com/cn 后缀为\\c\\
			String ori_file_suffix = "/" + file_path.substring(index + 1) + "/";

			// 文件内部相关路径替换字段信息("\\转换后的文件路径\\")
			String inner_to_str = "/" + default_dict + "/";

			// 文件名
			String fileName = "";
			// 字节信息
			byte[] data = null;
			// 文件输入流
			BufferedReader reader = null;
			// 文件输出流
			OutputStream out = null;
			// 新文件
			File new_file = null;
			for (File file : file_list) {
				// 读取源文件
				reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
				// 文件名
				fileName = file.getPath().replace(file_path, new_file_path);

				// 创建新文件
				new_file = new File(fileName);
				if (!new_file.getParentFile().exists()) {
					// 创建父文件目录
					new_file.getParentFile().mkdirs();
				}
				// 创建变换后的文件
				if (!new_file.exists()) {
					new_file.createNewFile();
				}
				// 文件转码
				out = new BufferedOutputStream(new FileOutputStream(new_file));
				// 读取长度
				String line = "";
				while (line != null) {
					// 读取文件
					line = reader.readLine();
					if (line != "" && line != null) {
						// 循环替换存在的原路径信息 replaceAll 使用正则表达式匹配，有可能出现异常
						while (line.indexOf(ori_file_suffix) != -1) {
							line = line.replace(ori_file_suffix, inner_to_str);
						}
						// 读取每一行并添加换行符 -生成的文件换行
						if (ori_encoding != null && ori_encoding != "") {
							data = (converter.convert(line) + "\n").getBytes(ori_encoding);
						} else {
							data = (converter.convert(line) + "\n").getBytes();
						}
						out.write(data);
					}
				}
				reader.close();
				out.flush();
				out.close();
				System.out.println("生成字体转换后的文件--" + new_file.getPath());
			}
		} catch (Exception e) {
			throw new Exception("字体转换生成文件异常！", e);
		}
	}

	public static void main(String[] args) {
		try {
			String file_path = "E:\\yang\\template\\cn";
			String default_dict = "big5";
			FontConvertUtil.convertToOtherFont(null, ZHConverter.TRADITIONAL, file_path, default_dict);
			// String testStr = "试验";
			// ZHConverter converter =
			// ZHConverter.getInstance(ZHConverter.TRADITIONAL);
			// String(converter.convert(testStr).getBytes()));
			// System.out.println(converter.convert(testStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
