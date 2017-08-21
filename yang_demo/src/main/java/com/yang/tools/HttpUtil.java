package com.yang.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName: HttpUtil
 * @package com.arvato.hnair.exchange.util
 * @Description: 基于HttpClient4.2的 http/https模拟请求工具类
 *               用户可以自行初始化自己的https请求相关的sslContext信息
 *               （调用init_SSLContext方法或者直接setContext
 *               ）。系统默认提供的sslContext安全套接字不保证https请求的授权成功。
 *               该工具类一旦被实例化，将不能够修改http请求类型，http or https
 * @author Mr.yang
 * @date 2016年1月6日 下午5:04:20
 * @version V1.0
 */
public class HttpUtil {

	/** CONTENT_TYPE */
	private static String CONTENT_TYPE_JSON = "application/json;charset=";

	private static String CONTENT_TYPE_CHARSET = ";charset=";

	/** 默认的连接超时时间 */
	private static int DEFAULT_CONNECTION_TIME_OUT = 3000;

	/** 默认的读取数据超时时间 */
	private static int DEFAULT_SO_TIME_OUT = 10000;

	/** 默认编码格式 */
	private static String DEFAULT_CHARSET_UTF_8 = "UTF-8";

	/** 默认 SSL */
	private static String DEFAULT_SSL = "SSL";

	/** 默认 JKS */
	private static String DEFAULT_JKS = "JKS";

	/** 默认 SunX509 */
	private static String DEFAULT_SUNX509 = "SunX509";

	/** 默认 false:http请求；true:https请求 */
	private boolean http_flag = false;

	/** 记录上一次请求的响应状态码-默认成功200 */
	private int status_code = 200;

	/** 安全套接字 SSL或者TLS */
	private SSLContext ssl_context = null;

	/** 默认保持上下文 */
	private HttpClientContext http_context = HttpClientContext.create();

	/**
	 * @Title: 构造函数
	 * @Description: 无参构造函数 - http请求
	 * @author : Mr.yang
	 * @throws Exception
	 */
	public HttpUtil() throws Exception {
		// http请求
		this.http_flag = false;
	}

	/**
	 * @Title: 构造函数
	 * @Description: 初始化确认请求是http还是https请求，默认使用内部自定义的安全套接
	 *               ,不保证用户https请求的正确调用，最好提供个性化的安全套接
	 * @author : Mr.yang
	 * @throws Exception
	 */
	public HttpUtil(boolean http_flag) throws Exception {
		this.http_flag = http_flag;

		if (http_flag) {
			// 内部默认实现的的SSLContext安全套接字实现,信任所有
			defalut_SSLClient();
		}
	}

	/**
	 * @Title: 构造函数
	 * @Description: 初始化安全套接字 SSL TLS，则该类会变为https请求，使用用户自定义的安全套接信息
	 * @author : Mr.yang
	 */
	public HttpUtil(SSLContext ssl_context) {
		// https请求
		this.http_flag = true;

		// 初始化用户自定义的安全套接字
		this.ssl_context = ssl_context;
	}

	/**
	 * @Title: 构造函数
	 * @Description: 初始化安全套接 SSL TLS，则该类会变为https请求，使用用户自定义的安全套接信息
	 * @param cer_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @param km_type
	 *            密匙管理器密匙类型
	 * @param ctx_type
	 *            SSLContext 类型 SSL/TLS
	 * @param k_type
	 *            密匙库类型 JKS
	 * @throws Exception
	 * @author Mr.yang
	 */
	public HttpUtil(String cer_path, String password, String km_type, String ctx_type, String k_type) throws Exception {
		// https请求
		this.http_flag = true;

		// 初始化用户自定义的安全套接字
		this.init_SSLContext(cer_path, password, km_type, ctx_type, k_type);
	}

	/**
	 * @Title: 构造函数
	 * @Description: 初始化SSLContext安全套接 SSL TLS ,则该类会变为https请求，(使用默认属性
	 *               SSL,JKS,SunX509)
	 * @param cer_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @author : Mr.yang
	 * @throws Exception
	 */
	public HttpUtil(String cer_path, String password) throws Exception {
		// https请求
		this.http_flag = true;

		// 初始化用户自定义的安全套接字
		this.init_SSLContext(cer_path, password);
	}

	/**
	 * 
	 * @Title: init_SSLContext
	 * @Description: 初始化SSLContext安全套接字，如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL
	 *               TLS并不会执行
	 * @param cer_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @param km_type
	 *            密匙管理器密匙类型
	 * @param ctx_type
	 *            SSLContext 类型 SSL/TLS
	 * @param k_type
	 *            密匙库类型 JKS
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:57:53
	 */
	public void init_SSLContext(String cer_path, String password, String km_type, String ctx_type, String k_type) throws Exception {
		// https请求
		if (http_flag) {
			// 实例化密钥库管理器
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(km_type);

			// 获得密钥库
			KeyStore keyStore = getKey_store(cer_path, password, k_type);
			// 初始化密钥库管理器
			keyManagerFactory.init(keyStore, password.toCharArray());

			// 实例化信任库 目前采用默认的算法
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			// 初始化信任库管理器
			trustManagerFactory.init(keyStore);
			// 实例化SSL安全套接字
			ssl_context = SSLContext.getInstance(ctx_type);
			// 初始化SSL上下文
			ssl_context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

			// 用户自定义的SSLContext安全套接字实现
			user_SSLClient();
		}
	}

	/**
	 * 
	 * @Title: init_SSLContext
	 * @Description: 初始化SSLContext安全套接字(使用默认属性 SSL,JKS,SunX509)
	 *               如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL TLS并不会执行
	 * @param cer_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:57:53
	 */
	public void init_SSLContext(String cer_path, String password) throws Exception {

		init_SSLContext(cer_path, password, DEFAULT_SUNX509, DEFAULT_SSL, DEFAULT_JKS);
	}

	/**
	 * @Title: user_SSLClient
	 * @Description: 用户自定义的SSLContext安全套接字实现
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:59:03
	 * @version V1.0
	 * @throws Exception
	 */
	private void user_SSLClient() throws Exception {

		// 创建默认的安全套接字工厂
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ssl_context);
		// 设置默认的安全套接字工厂
		HttpClients.custom().setSSLSocketFactory(sslsf);
	}

	/**
	 * @Title: defalut_SSLClient
	 * @Description: 内部默认实现的的SSLContext安全套接字实现,信任所有
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:44:03
	 * @version V1.0
	 * @throws Exception
	 */
	private void defalut_SSLClient() throws Exception {

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// 信任所有
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		// 创建默认的安全套接字工厂
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

		// 设置默认的安全套接字工厂
		HttpClients.custom().setSSLSocketFactory(sslsf);

	}

	/**
	 * 获得KeyStore.
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param password
	 *            密码
	 * @param k_type
	 *            keystore 类型
	 * @return 密钥库
	 * @throws Exception
	 */
	public KeyStore getKey_store(String cer_path, String password, String k_type) throws Exception {
		// 实例化密钥库
		KeyStore ks = KeyStore.getInstance(k_type);
		// 获得密钥库文件流
		FileInputStream is = new FileInputStream(new File(cer_path));
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}

	/**
	 * 
	 * @Title: getHttp_client
	 * @Description: 获取 CloseableHttpClient
	 * @return HttpClient
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月7日 下午4:06:41
	 */
	public CloseableHttpClient getHttp_client() throws Exception {

		CloseableHttpClient http_client = null;
		if (http_flag) {
			// https请求
			if (Utils.isNull(ssl_context)) {
				// 使用默认实现
				http_client = HttpClients.custom().build();
			} else {
				// 用户自定义
				http_client = HttpClients.custom().build();
			}
		} else {

			// http请求
			http_client = HttpClients.createDefault();
		}
		return http_client;
	}

	/**
	 * @Title: get
	 * @Description: get请求
	 * @param urlvalue
	 *            请求地址
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String get(String urlvalue, Map<String, String> params, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		String result = doGet(urlvalue, params, charset, connectionTimeout, soTimeout);
		return result;
	}

	/**
	 * @Title: get
	 * @Description: GET请求 使用默认的连接超时时间、数据读取超时时间
	 * @param urlvalue
	 *            请求地址
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String get(String urlvalue, Map<String, String> params, String charset) throws IOException, Exception {
		String result = doGet(urlvalue, params, charset, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: get
	 * @Description: GET请求 使用默认的连接超时时间、数据读取超时时间、数据编码格式
	 * @param urlvalue
	 *            请求地址
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String get(String urlvalue, Map<String, String> params) throws IOException, Exception {
		String result = doGet(urlvalue, params, DEFAULT_CHARSET_UTF_8, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: post
	 * @Description: post请求
	 * @param urlvalue
	 *            请求地址
	 * @param paras
	 *            请求参数
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String post(String urlvalue, Map<String, String> params, String charset, Integer connectionTimeout, Integer soTimeout) throws IOException, Exception {
		String result = doPost(urlvalue, params, charset, connectionTimeout, soTimeout);
		return result;
	}

	/**
	 * @Title: post
	 * @Description: post请求 使用默认的连接超时时间、数据读取超时时间
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String post(String urlvalue, Map<String, String> params, String charset) throws IOException, Exception {
		String result = doPost(urlvalue, params, charset, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: post
	 * @Description: post请求 使用默认的连接超时时间、数据读取超时时间、数据编码格式
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String post(String urlvalue, Map<String, String> params) throws IOException, Exception {
		String result = doPost(urlvalue, params, DEFAULT_CHARSET_UTF_8, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: doPost
	 * @Description:
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	private String doPost(String urlvalue, Map<String, String> params, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		CloseableHttpClient http_client = getHttp_client();
		;

		// post请求
		HttpPost post = new HttpPost(urlvalue);
		/*
		 * 未设置消息体 httppost.addHeader(header); httppost.addHeaders(headers);
		 */
		// 参数设置
		if (Utils.isNotNull(params)) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				formparams.add(new BasicNameValuePair(it.next(), params.get(it.next())));
			}
			// 请求实体
			UrlEncodedFormEntity request_entity = new UrlEncodedFormEntity(formparams, charset);
			post.setEntity(request_entity);
		}

		// 设置连接超时和读取超时
		RequestConfig req_config = RequestConfig.custom().setSocketTimeout(soTimeout).setConnectTimeout(connectionTimeout).build();
		post.setConfig(req_config);
		// CONTENT_TYPE
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_CHARSET + charset);
		post.addHeader(header);

		// 请求响应
		CloseableHttpResponse response = http_client.execute(post, http_context);

		// 请求响应实体类即消息体
		HttpEntity response_entity = response.getEntity();
		// 首行-状态行
		status_code = response.getStatusLine().getStatusCode();

		// 返回消息体字符串信息
		return EntityUtils.toString(response_entity, charset);
	}

	/**
	 * @Title: doGet
	 * @Description: GET 请求
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码格式（同时指定了编码方式text/html和参数编码方式）
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws HttpException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	private String doGet(String urlvalue, Map<String, String> params, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		CloseableHttpClient http_client = getHttp_client();

		// 参数设置
		if (Utils.isNotNull(params)) {
			Iterator<String> it = params.keySet().iterator();
			String key = "";
			StringBuilder stringBuilder = new StringBuilder();
			while (it.hasNext()) {
				key = it.next();
				stringBuilder.append("&");
				stringBuilder.append(URLEncoder.encode(key, charset));
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(params.get(key), charset));
			}
			urlvalue += stringBuilder.toString().replaceFirst("&", "?");

		}
		// GET请求
		HttpGet get = new HttpGet(urlvalue);
		// 设置请求和读取超时时间
		RequestConfig req_config = RequestConfig.custom().setSocketTimeout(soTimeout).setConnectTimeout(connectionTimeout).build();
		// CONTENT_TYPE
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_CHARSET + charset);
		get.addHeader(header);

		get.setConfig(req_config);

		// 请求响应
		HttpResponse response = http_client.execute(get, http_context);

		// 请求响应实体类即消息体
		HttpEntity response_entity = response.getEntity();
		// 首行-状态行
		status_code = response.getStatusLine().getStatusCode();

		// 返回消息体字符串信息
		return EntityUtils.toString(response_entity, charset);
	}

	/**
	 * @Title: getStatus_code
	 * @Description: 获取 记录上一次请求的响应状态码
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:53:47
	 */
	public int getStatus_code() {
		return status_code;
	}

	/**
	 * @Title: getSsl_context
	 * @Description: 获取自定义的SSL或者SSL安全套接字协议
	 * @return SSLContext
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:51:58
	 */
	public SSLContext getSsl_context() {
		return ssl_context;
	}

	/**
	 * @Title: setSsl_context
	 * @Description: 设置自定义的SSL或者SSL安全套接字协议
	 * @return void
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:51:58
	 */
	public void setSsl_context(SSLContext context) {
		this.ssl_context = context;
	}

	/**
	 * @Title: isHttps
	 * @Description: 获取 请求 false:http请求；true：https请求
	 * @return boolean
	 * @author Mr.yang
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @date 2016年1月7日 下午3:01:04
	 */
	public boolean getHttp_flag() {
		return this.http_flag;
	}

	public static void main(String[] args) {
		Map params = new HashMap();
		String url = "http://api.csai.cn/oauth2/access_token2";
		params.put("username", "444575003@qq.com");
		params.put("ip_address", "10.1.1.1");
		params.put("user_agent", "chrom");

		// 绑定回调
		try {
			HttpUtil http = new HttpUtil();
			// String result = http.get("http://www.baidu.com");
			// System.out.println(result.replaceAll("\"", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}