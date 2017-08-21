package com.yang.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName: HttpUtil
 * @package com.arvato.hnair.exchange.util
 * @Description: 基于HttpClient4.2的 http/https模拟请求工具类
 *               用户可以自行初始化自己的https请求相关的sslContext信息
 *               （调用init_SSLContext方法或者直接setContext
 *               ）。系统默认提供的sslContext安全套接字不保证https请求的授权成功。
 * @author Mr.yang
 * @date 2016年1月6日 下午5:04:20
 * @version V1.0
 */
public class HttpUtil42 {

	/** 默认的连接超时时间 */
	private static final int DEFAULT_CONNECTION_TIME_OUT = 3000;

	/** 默认的读取数据超时时间 */
	private static final int DEFAULT_SO_TIME_OUT = 10000;

	/** 默认编码格式 */
	private static final String DEFAULT_CHARSET_UTF_8 = "UTF-8";

	/** 默认 SSL */
	private static final String DEFAULT_SSL = "SSL";

	/** 默认 JKS */
	private static final String DEFAULT_JKS = "JKS";

	/** 默认 SunX509 */
	private static final String DEFAULT_SUNX509 = "SunX509";

	/** 默认 false:http请求；true:https请求 */
	private boolean http_flag = false;

	/** 记录上一次请求的响应状态码-默认成功200 */
	private int status_code = 200;

	/** SSL或者TLS */
	SSLContext ssl_context = null;

	/**
	 * 
	 * @Title: init_SSLContext
	 * @Description: 初始化SSLContext安全套接字
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

		// 实例化密钥库管理器
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(km_type);

		// 获得密钥库
		KeyStore keyStore = getKeyStore(cer_path, password, k_type);
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
	}

	/**
	 * 
	 * @Title: init_SSLContext
	 * @Description: 初始化SSLContext安全套接字(使用默认属性 SSL,JKS,SunX509)
	 * @param cer_path
	 *            证书路径
	 * @param password
	 *            证书公匙
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:57:53
	 */
	public void init_SSLContext(String cer_path, String password) throws Exception {

		// 实例化密钥库管理器
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(DEFAULT_SUNX509);

		// 获得密钥库
		KeyStore keyStore = getKeyStore(cer_path, password, DEFAULT_JKS);
		// 初始化密钥库管理器
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		// 初始化信任库管理器
		trustManagerFactory.init(keyStore);
		// 实例化SSL安全套接字
		ssl_context = SSLContext.getInstance(DEFAULT_SSL);
		// 初始化SSL上下文
		ssl_context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
	}

	/**
	 * @ClassName: defalut_SSLClient
	 * @package com.arvato.hnair.exchange.util
	 * @Description: 内部默认实现的的SSLContext安全套接字实现类
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:44:03
	 * @version V1.0
	 */
	public class defalut_SSLClient extends DefaultHttpClient {
		public defalut_SSLClient() throws Exception {
			super();
			SSLContext ctx = SSLContext.getInstance(DEFAULT_SSL);
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = this.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		}
	}

	/**
	 * @ClassName: user_SSLClient
	 * @package com.arvato.hnair.exchange.util
	 * @Description: 用户自定义的SSLContext安全套接字实现类
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:59:03
	 * @version V1.0
	 */
	public class user_SSLClient extends DefaultHttpClient {
		public user_SSLClient() throws Exception {
			super();
			SSLSocketFactory ssf = new SSLSocketFactory(ssl_context, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = this.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		}
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
	public KeyStore getKeyStore(String cer_path, String password, String k_type) throws Exception {
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
	 * @Title: getHttpClient
	 * @Description: 获取 HttpClient
	 * @return HttpClient
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月7日 下午4:06:41
	 */
	public HttpClient getHttpClient() throws Exception {

		HttpClient http_client = null;
		if (http_flag) {
			// https请求
			if (Utils.isNull(ssl_context)) {
				// 使用默认实现
				http_client = new defalut_SSLClient();
			} else {
				// 用户自定义
				http_client = new user_SSLClient();
			}
		} else {

			// http请求
			http_client = new DefaultHttpClient();
		}
		return http_client;
	}

	/**
	 * @Title: get
	 * @Description: get请求
	 * @param urlvalue
	 *            请求地址
	 * @param charset
	 *            编码格式
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String get(String urlvalue, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		String result = doGet(urlvalue, charset, connectionTimeout, soTimeout);
		return result;
	}

	/**
	 * @Title: get
	 * @Description: GET请求 使用默认的连接超时时间、数据读取超时时间
	 * @param urlvalue
	 *            请求地址
	 * @param charset
	 *            编码格式
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String get(String urlvalue, String charset) throws IOException, Exception {
		String result = doGet(urlvalue, charset, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: get
	 * @Description: GET请求 使用默认的连接超时时间、数据读取超时时间、数据编码格式
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
	public String get(String urlvalue) throws IOException, Exception {
		String result = doGet(urlvalue, DEFAULT_CHARSET_UTF_8, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
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
	 *            编码格式
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            数据读取超时时间
	 * @return String
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String post(String urlvalue, Map<String, String> params, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		String result = doPost(urlvalue, params, charset, connectionTimeout, soTimeout);
		return result;
	}

	/**
	 * @Title: post
	 * @Description: POST请求 使用默认的连接超时时间、数据读取超时时间
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码格式
	 * @return String
	 * @throws Exception
	 * @author Mr.yang
	 * @date 2016年1月6日 下午5:07:56
	 */
	public String post(String urlvalue, Map<String, String> params, String charset) throws Exception {
		String result = doPost(urlvalue, params, charset, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_SO_TIME_OUT);
		return result;
	}

	/**
	 * @Title: post
	 * @Description: POST请求 使用默认的连接超时时间、数据读取超时时间、数据编码格式
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
	 *            编码格式
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
		HttpClient http_client = getHttpClient();

		// 设置连接和读取超时时间
		set_req_param(http_client, charset, connectionTimeout, soTimeout);

		// post请求
		HttpPost post = new HttpPost(urlvalue);
		/*
		 * 未设置消息体 httppost.addHeader(header); httppost.addHeaders(headers);
		 */
		// 参数设置
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (Utils.isNotNull(params)) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				formparams.add(new BasicNameValuePair(it.next(), params.get(it.next())));
			}
		}
		// 请求实体
		UrlEncodedFormEntity request_entity = new UrlEncodedFormEntity(formparams, charset);
		post.setEntity(request_entity);
		// 请求响应
		HttpResponse response = http_client.execute(post);

		// 请求响应实体类即消息体
		HttpEntity response_entity = response.getEntity();
		// 首行-状态行
		status_code = response.getStatusLine().getStatusCode();

		// 返回消息体字符串信息
		return EntityUtils.toString(response_entity, "utf-8");
	}

	/**
	 * @Title: doGet
	 * @Description: GET 请求
	 * @param urlvalue
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码格式
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
	private String doGet(String urlvalue, String charset, Integer connectionTimeout, Integer soTimeout) throws Exception {
		HttpClient http_client = getHttpClient();

		// 设置连接和读取超时时间
		set_req_param(http_client, charset, connectionTimeout, soTimeout);
		// GET请求
		HttpGet get = new HttpGet(urlvalue);
		// 请求响应
		HttpResponse response = http_client.execute(get);

		// 请求响应实体类即消息体
		HttpEntity response_entity = response.getEntity();
		// 首行-状态行
		status_code = response.getStatusLine().getStatusCode();

		// 返回消息体字符串信息
		return EntityUtils.toString(response_entity, "utf-8");
	}

	/**
	 * @Title: set_req_param
	 * @Description: 设置连接和读取超时时间
	 * @param http_client
	 *            连接客户端
	 * @param charset
	 *            编码
	 * @param connectionTimeout
	 *            连接超时时间
	 * @param soTimeout
	 *            读取超时时间
	 * @author Mr.yang
	 * @date 2016年1月7日 下午6:19:27
	 */
	private void set_req_param(HttpClient http_client, String charset, Integer connectionTimeout, Integer soTimeout) {
		HttpParams params = http_client.getParams();
		// 读取超时
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
		// 连接超时
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
		// 编码
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charset);

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
	 * @Title: isHttps
	 * @Description: 获取请求 false:http请求；true：https请求
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:01:04
	 */
	public boolean getHttp_flag() {
		return http_flag;
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
	 * @Description: 设置请求 false:http请求；true：https请求
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月7日 下午3:01:04
	 */
	public void setHttp_flag(boolean http_flag) {
		this.http_flag = http_flag;
	}

	public static void main(String[] args) {
		Map params = new HashMap();
		String url = "http://api.csai.cn/oauth2/access_token2";
		params.put("username", "444575003@qq.com");
		params.put("ip_address", "10.1.1.1");
		params.put("user_agent", "chrom");

		// 绑定回调
		try {
			HttpUtil42 client = new HttpUtil42();
			client.http_flag = true;
			String result = client.get("https://www.baidu.com");
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}