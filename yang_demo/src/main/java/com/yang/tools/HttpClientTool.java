package com.yang.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * @ClassName: HttpClientTool
 * @package com.yang.tools
 * @Description: 基于URLConnection的请求工具类 -
 *               记录了请求/响应的首部，请求的参数列表,响应的消息体。每次发出申请，首先重置这些信息，再进行设置。
 * @author Mr.yang
 * @date 2016年1月15日 下午10:15:15
 * @version V1.0
 */
public class HttpClientTool {

	/**
	 * certmsgr.msc keytool文件目录为当前用户根目录 keytool -import -file baidu.cer
	 * -keystore baidu.keys -storepass baidu -storetype jks -storepass yang123
	 */
	/** 日志记录 */
	private static Logger logger = Logger.getLogger(HttpClientTool.class);

	/** 空字符串 */
	private static final String EMPTY = "";
	/** 请求方式 - GET */
	public static final String METHOD_GET = "GET";

	/** 请求方式 - POST */
	public static final String METHOD_POST = "POST";

	/** 请求方式 - HEAD */
	public static final String METHOD_HEAD = "HEAD";

	/** 请求方式 - PUT */
	public static final String METHOD_PUT = "PUT";

	/** 请求方式 - DELETE */
	public static final String METHOD_DELETE = "DELETE";

	/** 请求方式 - TRACE */
	public static final String METHOD_TRACE = "TRACE";

	/** 请求方式 - OPTIONS */
	public static final String METHOD_OPTIONS = "OPTIONS";

	/** MIME内容类型首部key - CONTENT_TYPE */
	public static final String CONTENT_TYPE = "Content-Type";

	/** MIME内容类型 - CONTENT_LENGTH */
	public static final String CONTENT_LENGTH = "Content-length";

	/** MIME内容类型 - MIME_JSON */
	public static final String MIME_JSON = "application/json";

	/** HOST 主机 */
	public static String HOST = "Host";

	/** 接收数据首部key - Accept */
	public static String ACCEPT = "Accept";

	/** 浏览器首部key - User-Agent */
	public static String USER_AGENT = "User-Agent";

	/** 接受的编码格式 - accept-encoding */
	public static String ACCEPT_ENCODING = "accept-encoding";

	/** cookie 首部 */
	public static String SET_COOKIE = "Set-Cookie";

	/** gzip 编码 */
	public static final String CHARSET_GIZP = "gzip";

	/** 默认连接超时时间 - 3秒 */
	public static final int DEFAULT_CONNECTION_TIME_OUT = 5000;

	/** 默认读取超时时间 - 10秒 */
	public static final int DEFAULT_SO_TIME_OUT = 10000;

	/** 请求方式 */
	private String method = EMPTY;
	/** 默认编码格式 - UTF-8 */
	public static final String DEFALUT_CHARSET_UTF_8 = "utf-8";

	/** 编码格式 - 默认编码格式 -UTF-8 */
	private String charset = DEFALUT_CHARSET_UTF_8;

	/** 默认 请求协议的标准名称 TLS */
	public static final String DEFAULT_PROTOCOL_TLS = "TLS";

	/** 请求协议的标准名称 - 默认 TLS */
	private String protocol = DEFAULT_PROTOCOL_TLS;

	/** 默认 密匙库类型 JKS */
	public static final String DEFAULT_KS_TYPE_JKS = "JKS";

	/** 默认 密匙库类型 JKS */
	public static final String DEFAULT_KS_TYPE_PKCS12 = "PKCS12";

	/** 密匙库类型 */
	private String ks_type = DEFAULT_KS_TYPE_JKS;

	/** 默认算法的标准名称 SunX509 */
	public static String DEFAULT_ALGORITHM_SUNX509 = "SunX509";

	/** 所请求算法的标准名称 */
	private String algorithm = DEFAULT_ALGORITHM_SUNX509;

	/** 安全套接字 SSL或者TLS */
	private SSLContext ssl_context = null;

	/** 请求参数列表 k-v */
	private Map<String, Object> req_params = null;

	/** 响应状态 - 默认200 */
	private int res_code = 200;

	/** 响应成功状态 范围起始 start - 200 */
	public static int RES_START_SUCCESS = 200;

	/** 响应成功状态 范围结束 end - 300 */
	public static int RES_END_SUCCESS = 300;

	/** 请求首部 */
	private Map<String, String> req_header = null;

	/** 响应首部 */
	private Map<String, String> res_header = null;

	/** 响应消息体 */
	private byte[] res_body = null;

	/** 连接超时时间 - 默认连接超时间 */
	private int connection_time_out = DEFAULT_CONNECTION_TIME_OUT;

	/** 读取超时时间 - 默认读取超时时间 */
	private int so_time_out = DEFAULT_SO_TIME_OUT;

	/** 请求成功：true;失败：false - 默认falseP：失败 */
	private boolean is_success = false;

	/** http */
	private static String HTTP = "http://";

	/** https */
	private static String HTTPS = "https://";

	/** 请求类型标记 true：http请求；false：https请求 - 默认true：http请求 */
	private boolean is_http = true;

	/** 连接对象 */
	private HttpURLConnection uc = null;

	/**
	 * @Title: 构造函数
	 * @Description: 初始化连接、读取超时时间-单位毫秒
	 * @param connection_time_out
	 *            连接超时时间
	 * @param so_time_out
	 *            读取超时时间
	 * @author : Mr.yang
	 * @date 2016年1月17日 下午1:49:31
	 */
	public HttpClientTool() {

		// 初始化请求信息
		initReqParams();
		// 初始化响应信息
		initResParams();
	}

	/**
	 * @Title: 构造函数
	 * @Description: 初始化连接、读取超时时间-单位毫秒
	 * @param connection_time_out
	 *            连接超时时间
	 * @param so_time_out
	 *            读取超时时间
	 * @author : Mr.yang
	 * @date 2016年1月17日 下午1:49:31
	 */
	public HttpClientTool(int connection_time_out, int so_time_out) {

		this();
		// 连接超时时间
		this.connection_time_out = connection_time_out;
		// 读取超时时间
		this.so_time_out = so_time_out;
	}

	/* GET请求重载 */
	public String doGet(String url) {

		return doGet(url, null, null, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doGet(String url, Map<String, Object> params) {

		return doGet(url, params, null, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doGet(String url, Map<String, Object> params, Map<String, String> req_header) {

		return doGet(url, params, req_header, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doGet(String url, Map<String, Object> params, String charset, int connection_time_out, int so_time_out) {

		return doGet(url, params, null, charset, connection_time_out, so_time_out);
	}

	public String doGet(String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out, int so_time_out) {
		return execute(METHOD_GET, url, params, req_header, charset, connection_time_out, so_time_out);
	}

	/* POST请求重载 */
	public String doPost(String url, Map<String, Object> params) {

		return doPost(url, params, null);
	}

	public String doPost(String url, Map<String, Object> params, Map<String, String> req_header) {

		return doPost(url, params, req_header, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doPost(String url, Map<String, Object> params, String charset, int connection_time_out, int so_time_out) {

		return doPost(url, params, null, charset, connection_time_out, so_time_out);
	}

	public String doPost(String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out, int so_time_out) {

		return execute(METHOD_POST, url, params, req_header, charset, connection_time_out, so_time_out);
	}

	/* PUT 请求重载 */
	public String doPut(String url, Map<String, Object> params) {

		return doPut(url, params, null);
	}

	public String doPut(String url, Map<String, Object> params, Map<String, String> req_header) {

		return doPut(url, params, req_header, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doPut(String url, Map<String, Object> params, String charset, int connection_time_out, int so_time_out) {

		return doPut(url, params, null, charset, connection_time_out, so_time_out);
	}

	public String doPut(String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out, int so_time_out) {

		return execute(METHOD_PUT, url, params, req_header, charset, connection_time_out, so_time_out);
	}

	/* DELETE 请求重载 */
	public String doDelete(String url, Map<String, Object> params) {

		return doDelete(url, params, null);
	}

	public String doDelete(String url, Map<String, Object> params, Map<String, String> req_header) {

		return doDelete(url, params, req_header, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doDelete(String url, Map<String, Object> params, String charset, int connection_time_out, int so_time_out) {

		return doDelete(url, params, null, charset, connection_time_out, so_time_out);
	}

	public String doDelete(String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out, int so_time_out) {

		return execute(METHOD_DELETE, url, params, req_header, charset, connection_time_out, so_time_out);
	}

	/* HEAD 请求重载 */
	public String doHead(String url, Map<String, Object> params) {

		return doHead(url, params, null);
	}

	public String doHead(String url, Map<String, Object> params, Map<String, String> req_header) {

		return doHead(url, params, req_header, this.charset, this.connection_time_out, this.so_time_out);
	}

	public String doHead(String url, Map<String, Object> params, String charset, int connection_time_out, int so_time_out) {

		return doHead(url, params, null, charset, connection_time_out, so_time_out);
	}

	public String doHead(String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out, int so_time_out) {

		return execute(METHOD_PUT, url, params, req_header, charset, connection_time_out, so_time_out);
	}

	/**
	 * @Title: execute
	 * @Description: 发送请求
	 * @param url
	 *            连接
	 * @param params
	 *            请求参数
	 * @param req_header
	 *            首部
	 * @param charset
	 *            编码
	 * @param connection_time_out
	 *            连接超时时间
	 * @param so_time_out
	 *            读取超时时间
	 * @return String
	 * @author Mr.yang
	 * @date 2016年1月17日 下午11:09:07
	 */
	public String execute(String method, String url, Map<String, Object> params, Map<String, String> req_header, String charset, int connection_time_out,
			int so_time_out) {

		// 初始化请求信息
		initReqParams();

		// 编码格式
		if (Utils.isNotNull(charset)) {
			this.charset = charset;
		}

		// 请求方式
		this.method = method.toUpperCase();

		// 返回值
		String result = EMPTY;
		try {
			// 初始化连接
			initUrlConnection(url);

			// 设置请求首部，连接、读取超时时间
			setReqParam(req_header, connection_time_out, so_time_out);

			if (this.method.equals(METHOD_GET) || this.method.equals(METHOD_HEAD)) {
				// HEAD、 GET请求
				// 设置请求参数
				url = setParam(url, params);
			} else if (this.method.equals(METHOD_POST) || this.method.equals(METHOD_PUT) || this.method.equals(METHOD_DELETE)) {
				// POST、PUT请求
				// 允许写入
				uc.setDoOutput(true);
				// 设置请求参数
				setParam(params);
			}

			// 读取记录响应信息
			readRes();

			// 响应首部字符编码
			String content_type_charset = getContentTypeCharset();
			result = new String(res_body, content_type_charset);

		} catch (Exception e) {

			is_success = false;
			e.printStackTrace();
			logger.error(this.method + "请求失败-url: " + url + params != null ? ",请求参数：" + params.toString() : "", e);
		} finally {
			if (Utils.isNotNull(this.uc)) {
				this.uc.disconnect();
			}
		}

		return result.trim();
	}

	/**
	 * @Title: initReqParams
	 * @Description: 初始化请求信息
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:44:49
	 */
	private void initReqParams() {
		this.req_header = new HashMap<String, String>();
		this.req_params = new HashMap<String, Object>();
	}

	/**
	 * @Title: initResParams
	 * @Description: 初始化响应信息
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:46:38
	 */
	private void initResParams() {

		this.res_code = 200;
		this.res_header = new HashMap<String, String>();
		this.res_body = EMPTY.getBytes();
	}

	/**
	 * @Title: getRes_Cookie
	 * @Description: 获取响应的cookie
	 * @return Map<String, String>
	 * @author Mr.yang
	 * @date 2016年1月18日 下午4:18:24
	 */
	public Map<String, String> getRes_Cookie() {

		Map<String, String> cookie = new HashMap<String, String>();

		cookie.put(SET_COOKIE, res_header.get(SET_COOKIE));
		return cookie;
	}

	/**
	 * @Title: setReqParam
	 * @Description: 设置请求首部，连接、读取超时时间。读取记录响应信息
	 * @param req_header
	 *            请求首部
	 * @param connection_time_out
	 *            连接超时时间
	 * @param so_time_out
	 *            读取超时时间
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:21:43
	 */
	private void setReqParam(Map<String, String> req_header, int connection_time_out, int so_time_out) throws IOException {

		// 允许读取
		this.uc.setDoInput(true);

		// 请求方式
		this.uc.setRequestMethod(this.method);

		// 设置请求首部信息
		setReqHeader(req_header);

		// 设置连接、读取超时时间
		setConnection(connection_time_out, so_time_out);

	}

	/**
	 * @Title: getContentTypeCharset
	 * @Description: 获得响应的MIME类型的编码
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:38:40
	 */
	public String getContentTypeCharset() {

		// MIME类型编码
		String content_type_charset = EMPTY;
		// 记录的响应首部中检索
		if (Utils.isNotNull(res_header) && res_header.containsKey(CONTENT_TYPE)) {
			String header = res_header.get(CONTENT_TYPE);
			if (header.indexOf("charset") != -1) {
				content_type_charset = header.substring(header.indexOf("charset")).split("=")[1];
			}
		}
		if (Utils.isNull(content_type_charset)) {
			// 如果不存在默认使用-请求传入的字符编码
			content_type_charset = charset;
		}
		return content_type_charset;
	}

	/**
	 * @Title: getReqHeader
	 * @Description: 根据head的键获取响应header中对应的值
	 * @param field
	 * @return String
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:16:14
	 */
	public String getResHeader(String field) {
		return res_header.get(field);
	}

	/**
	 * @Title: getReqHeader
	 * @Description: 根据head的键获取请求header中对应的值
	 * @param field
	 * @return String
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:16:14
	 */
	public String getReqHeader(String field) {
		return req_header.get(field);
	}

	/**
	 * @Title: readRes
	 * @Description: 读取记录响应信息
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:18:46
	 */
	private void readRes() throws IOException {

		// 初始化响应信息
		initResParams();

		// 响应码
		this.res_code = this.uc.getResponseCode();

		// 记录响应的首部信息
		recordResHeader();

		// 读取记录响应消息体
		readResContent();
	}

	/**
	 * @Title: recordResHeader
	 * @Description: 记录响应的首部信息 连接
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:00:12
	 */
	private void recordResHeader() {

		String header = EMPTY;
		// 响应首部
		this.res_header = new HashMap<String, String>();

		for (int i = 1;; i++) {
			header = this.uc.getHeaderField(i);
			if (Utils.isNull(header)) {
				// 没有首部信息 - 退出
				break;
			} else {
				// 记录响应的首部信息
				res_header.put(this.uc.getHeaderFieldKey(i), header);
			}
		}
	}

	/**
	 * @Title: readResContent
	 * @Description: 读取记录响应信息
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:16:15
	 */
	private void readResContent() throws IOException {

		// 响应输入流
		InputStream in = null;
		if (this.res_code >= RES_START_SUCCESS && this.res_code <= RES_END_SUCCESS) {
			// 成功响应 - 用inputStream
			in = this.uc.getInputStream();
		} else {
			// 成功响应以外的 - 用errorStream
			in = this.uc.getErrorStream();
		}

		if (Utils.isNotNull(in)) {
			// 响应消息体
			res_body = inputStreamTOByte(in);
			// 关闭输入流
			in.close();
		} else {
			res_body = null;
		}
	}

	/**
	 * @Title: inputStreamTOByte
	 * @Description: 读取输入流信息转换成字节数组 注意： 此处不处理输入流的关闭
	 * @param in
	 *            输入流
	 * @return byte[]
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:08:56
	 */
	private byte[] inputStreamTOByte(InputStream in) throws IOException {
		// 缓冲区
		int BUFFER_SIZE = 4096;
		// 字节输出流
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;

		// 从输入流中反复读取数据
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
			outStream.write(data, 0, count);
		}
		// 释放缓冲区
		data = null;
		// 输出流转出成字节数组
		byte[] outByte = outStream.toByteArray();
		// 关闭输出流
		outStream.close();

		return outByte;
	}

	/**
	 * @Title: setConnection
	 * @Description: 设置连接、读取超时时间
	 * @param connection_time_out
	 *            连接超时时间
	 * @param so_time_out
	 *            读取超时时间
	 * @author Mr.yang
	 * @date 2016年1月17日 下午3:26:40
	 */
	private void setConnection(int connection_time_out, int so_time_out) {

		// 连接超时间
		if (Utils.isNotNull(connection_time_out)) {
			this.connection_time_out = connection_time_out;
			this.uc.setConnectTimeout(connection_time_out);
		}

		// 读取超时时间
		if (Utils.isNotNull(so_time_out)) {
			this.so_time_out = so_time_out;
			this.uc.setReadTimeout(so_time_out);
		}
	}

	/**
	 * @Title: setParam
	 * @Description: 设置get请求参数
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午2:07:12
	 */
	private String setParam(String url, Map<String, Object> params) throws UnsupportedEncodingException {

		// 参数设置
		this.req_params = params;
		if (Utils.isNotNull(params)) {
			Iterator<String> it = params.keySet().iterator();
			String key = EMPTY;
			StringBuilder stringBuilder = new StringBuilder();
			while (it.hasNext()) {
				key = it.next();
				stringBuilder.append("&");
				stringBuilder.append(URLEncoder.encode(key, charset));
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(String.valueOf(params.get(key)), charset));
			}
			url += stringBuilder.toString().replaceFirst("&", "?");

		}
		return url;
	}

	/**
	 * @Title: setParam
	 * @Description: 设置POST请求参数
	 * @param params
	 *            请求参数
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:07:44
	 */
	private void setParam(Map<String, Object> params) throws IOException {

		// MIIME类型
		setReqHeader(CONTENT_TYPE, MIME_JSON + ";" + "charset=" + this.charset);

		this.req_params = params;
		if (Utils.isNotNull(params)) {
			// 参数转成json
			String json_params = JsonTool.mapToJson(params);

			// 字节数组数据
			byte[] data = json_params.getBytes(this.charset);

			// 限制每次读取最大数据长度
			int len = 1024;

			// 数据长度
			int content_length = data.length;
			// setReqHeader(uc, CONTENT_LENGTH, String.valueOf(content_length));

			// 连接的输出流
			BufferedOutputStream out = new BufferedOutputStream(this.uc.getOutputStream());
			int off = 0;

			// 参数写入输出流
			while (off < content_length) {
				if (len >= content_length) {
					out.write(data, off, content_length);
					off += content_length;
				} else {
					out.write(data, off, len);
					off += len;
					content_length -= len;
				}

				// 刷新缓冲区
				out.flush();
			}

			// 关闭输出流
			out.close();
		}
	}

	/**
	 * @Title: setReqHeader
	 * @Description: 设置首部信息
	 * @param req_header
	 *            首部信息
	 * @return void
	 * @throws UnsupportedEncodingException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午3:10:30
	 */
	private void setReqHeader(Map<String, String> req_header) throws UnsupportedEncodingException {
		//
		this.uc.setAllowUserInteraction(false);
		// 不使用缓存
		this.uc.setUseCaches(false);
		// 主机
		this.uc.setRequestProperty(HOST, this.uc.getURL().getHost());
		// 接受的MIME类型
		this.uc.setRequestProperty(ACCEPT, MIME_JSON);
		// 用户浏览器
		// this.uc.setRequestProperty(USER_AGENT, "chrom");
		// 接受的编码格式
		this.uc.setRequestProperty(ACCEPT_ENCODING, this.charset);

		// 循环遍历设置首部信息
		if (Utils.isNotNull(req_header)) {
			Iterator<String> it = req_header.keySet().iterator();
			String key = EMPTY;

			while (it.hasNext()) {
				key = it.next();
				this.uc.setRequestProperty(key, req_header.get(key));
			}

			// 记录请求的首部信息
			this.req_header.putAll(req_header);
		}
	}

	/**
	 * @Title: setReqHeader
	 * @Description: 设置首部信息
	 * @param filed_key
	 *            首部key
	 * @param field_value
	 *            首部值
	 * @author Mr.yang
	 * @date 2016年1月17日 下午10:31:27
	 */
	private void setReqHeader(String filed_key, String field_value) {
		this.uc.setRequestProperty(filed_key, field_value);
		// 记录请求的首部信息
		this.req_header.put(filed_key, field_value);
	}

	/**
	 * @Title: getUrlConnection
	 * @Description: 根据请求地址获取请求链接
	 * @param url
	 *            请求地址
	 * @return URLConnection
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @author Mr.yang
	 * @date 2016年1月17日 下午3:06:35
	 */
	private void initUrlConnection(String url) throws IOException, KeyManagementException, NoSuchAlgorithmException {

		URL url_obj = new URL(url);

		this.uc = (HttpURLConnection) url_obj.openConnection();

		// 根据请求方式设置http/https请求标记
		setRequestType(url);

		if (!is_http) {
			// htts请求
			if (Utils.isNull(ssl_context)) {

				// 内部默认实现的的SSLContext安全套接字实现,信任所有
				initDefalutSSLClient();
			}
			// 安全套接字工厂
			SSLSocketFactory sf = ssl_context.getSocketFactory();
			// 设置HttpsURLConnection的安全套接字工厂
			HttpsURLConnection https_url_con = (HttpsURLConnection) this.uc;
			https_url_con.setSSLSocketFactory(sf);
			https_url_con.setHostnameVerifier(new TrustAnyHostnameVerifier());
		}

		// 根据响应首部设置当前对象cookie保持会话连接状态
		Map<String, String> cookie = getRes_Cookie();
		if (Utils.isNotNull(cookie)) {
			setReqHeader(SET_COOKIE, cookie.get(SET_COOKIE));
		}
	}

	/**
	 * @ClassName: TrustAnyHostnameVerifier
	 * @package com.yang.tools
	 * @Description: 内部类
	 * @author Mr.yang
	 * @date 2016年1月29日 下午8:13:35
	 * @version V1.0
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	/**
	 * @Title: initDefalutSSLClient
	 * @Description: 内部默认实现的的SSLContext安全套接字实现,信任所有
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:44:03
	 * @version V1.0
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws Exception
	 */
	private void initDefalutSSLClient() throws NoSuchAlgorithmException, KeyManagementException {

		// 实例化SSL安全套接字
		ssl_context = SSLContext.getInstance(DEFAULT_PROTOCOL_TLS);

		// 初始化SSL上下文
		ssl_context.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());

	}

	/**
	 * 
	 * @Title: setCustomSSLContext
	 * @Description: 初始化SSLContext安全套接字(使用默认属性 TLS,JKS,SunX509)
	 *               如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL TLS并不会执行
	 * @param ks_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @author Mr.yang
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws IOException
	 * @throws CertificateException
	 * @date 2016年1月7日 下午3:57:53
	 */
	public void setCustomSSLContext(String ks_path, String password) throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException {

		setCustomSSLContext(ks_path, password, algorithm, protocol, ks_type);
	}

	/**
	 * @Title: setCustomSSLContext
	 * @Description: 初始化SSLContext安全套接字，如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL
	 *               TLS并不会执行
	 * @param ks_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @param algorithm
	 *            密匙管理器算法的标准名称
	 * @param protocol
	 *            SSLContext 类型 SSL/TLS
	 * @param ks_type
	 *            密匙库类型 JKS
	 * @author Mr.yang
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws CertificateException
	 * @date 2016年1月7日 下午3:57:53
	 */
	public void setCustomSSLContext(String ks_path, String password, String algorithm, String protocol, String ks_type) throws NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException, KeyManagementException, CertificateException, IOException {

		// https请求
		is_http = false;

		// 密匙管理器算法的标准名称
		this.algorithm = algorithm;

		// 实例化密钥库管理器
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);

		// 获得密钥库
		KeyStore keyStore = getKeyStore(ks_path, password, ks_type);
		// 初始化密钥库管理器
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库 目前采用默认的算法
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		// 初始化信任库管理器
		trustManagerFactory.init(keyStore);
		// 实例化SSL安全套接字
		ssl_context = SSLContext.getInstance(protocol);
		// 初始化SSL上下文
		ssl_context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

	}

	/**
	 * 获得KeyStore.
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param password
	 *            密码
	 * @param ks_type
	 *            keystore 类型
	 * @return 密钥库
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 */
	private KeyStore getKeyStore(String ks_path, String password, String ks_type) throws KeyStoreException, IOException, NoSuchAlgorithmException,
			CertificateException {
		// 实例化密钥库
		KeyStore ks = KeyStore.getInstance(ks_type);
		// 获得密钥库文件流
		FileInputStream is = new FileInputStream(new File(ks_path));
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}

	/**
	 * @Title: setRequestType
	 * @Description: 根据请求方式设置http/https请求标记
	 * @param url
	 *            请求地址
	 * @author Mr.yang
	 * @date 2016年1月17日 下午2:16:21
	 */
	private void setRequestType(String url) {
		if (url.startsWith(HTTPS)) {
			// https请求
			is_http = false;
		} else {
			// http请求
			is_http = true;
		}
	}

	/**
	 * 获取charset
	 * 
	 * @return charset charset
	 */

	public String getCharset() {
		return charset;
	}

	/**
	 * 设置charset
	 * 
	 * @param charset
	 *            charset
	 */

	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 获取protocol
	 * 
	 * @return protocol protocol
	 */

	public String getProtocol() {
		return protocol;
	}

	/**
	 * 设置protocol
	 * 
	 * @param protocol
	 *            protocol
	 */

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * 获取ks_type
	 * 
	 * @return ks_type ks_type
	 */

	public String getKs_type() {
		return ks_type;
	}

	/**
	 * 设置ks_type
	 * 
	 * @param ks_type
	 *            ks_type
	 */

	public void setKs_type(String ks_type) {
		this.ks_type = ks_type;
	}

	/**
	 * 获取algorithm
	 * 
	 * @return algorithm algorithm
	 */

	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * 设置algorithm
	 * 
	 * @param algorithm
	 *            algorithm
	 */

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 获取ssl_context
	 * 
	 * @return ssl_context ssl_context
	 */

	public SSLContext getSsl_context() {
		return ssl_context;
	}

	/**
	 * 设置ssl_context
	 * 
	 * @param ssl_context
	 *            ssl_context
	 */

	public void setSsl_context(SSLContext ssl_context) {
		this.ssl_context = ssl_context;
	}

	/**
	 * 获取connection_time_out
	 * 
	 * @return connection_time_out connection_time_out
	 */

	public int getConnection_time_out() {
		return connection_time_out;
	}

	/**
	 * 设置connection_time_out
	 * 
	 * @param connection_time_out
	 *            connection_time_out
	 */

	public void setConnection_time_out(int connection_time_out) {
		this.connection_time_out = connection_time_out;
	}

	/**
	 * 获取so_time_out
	 * 
	 * @return so_time_out so_time_out
	 */

	public int getSo_time_out() {
		return so_time_out;
	}

	/**
	 * 设置so_time_out
	 * 
	 * @param so_time_out
	 *            so_time_out
	 */

	public void setSo_time_out(int so_time_out) {
		this.so_time_out = so_time_out;
	}

	/**
	 * 获取method
	 * 
	 * @return method method
	 */

	public String getMethod() {
		return method;
	}

	/**
	 * 获取req_params
	 * 
	 * @return req_params req_params
	 */

	public Map<String, Object> getReq_params() {
		return req_params;
	}

	/**
	 * 获取res_code
	 * 
	 * @return res_code res_code
	 */

	public int getRes_code() {
		return res_code;
	}

	/**
	 * 获取req_header
	 * 
	 * @return req_header req_header
	 */

	public Map<String, String> getReq_header() {
		return req_header;
	}

	/**
	 * 获取res_header
	 * 
	 * @return res_header res_header
	 */

	public Map<String, String> getRes_header() {
		return res_header;
	}

	/**
	 * 获取res_body
	 * 
	 * @return res_body res_body
	 */

	public byte[] getRes_body() {
		return res_body;
	}

	/**
	 * 获取isSuccess
	 * 
	 * @return is_success is_success
	 */

	public boolean isSuccess() {
		return is_success;
	}

	/**
	 * 获取isHttp
	 * 
	 * @return is_http is_http
	 */

	public boolean isHttp() {
		return is_http;
	}

	public static void main(String[] args) {
		HttpClientTool client_tool = new HttpClientTool();
		String key_path = FileUtils.getClassPath() + "\\conf\\baidu.keys";
		try {
			// client_tool.setCustomSSLContext(key_path, "yang123");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "https://www.sogou.com/";
		System.out.println(client_tool.doGet(url));
	}

}
