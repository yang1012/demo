package yang_demo;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsPost_wb {
// 日志初始化
private static Logger logger = LoggerFactory.getLogger(HttpsPost_wb.class);
 /**
  * 获得KeyStore.
  * @param keyStorePath 密钥库路径
  * @param password 密码
  * @return 密钥库
  * @throws Exception
  */
 public static KeyStore getKeyStore(String password, String keyStorePath)
   throws Exception {
  // 实例化密钥库
  KeyStore ks = KeyStore.getInstance("JKS");
  // 获得密钥库文件流
  FileInputStream is = new FileInputStream(keyStorePath);
  // 加载密钥库
  ks.load(is, password.toCharArray());
  // 关闭密钥库文件流
  is.close();
  return ks;
 }

 /**
  * 获得SSLSocketFactory.
  * @param password  密码
  * @param keyStorePath 密钥库路径
  * @param trustStorePath  信任库路径
  * @return SSLSocketFactory
  * @throws Exception
  */
 public static SSLContext getSSLContext(String password,
   String keyStorePath, String trustStorePath) throws Exception {
  // 实例化密钥库
  KeyManagerFactory keyManagerFactory = KeyManagerFactory
    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
  // 获得密钥库
  KeyStore keyStore = getKeyStore(password, keyStorePath);
  // 初始化密钥工厂
  keyManagerFactory.init(keyStore, password.toCharArray());

  // 实例化信任库
  TrustManagerFactory trustManagerFactory = TrustManagerFactory
    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
  // 获得信任库
  KeyStore trustStore = getKeyStore(password, trustStorePath);
  // 初始化信任库
  trustManagerFactory.init(trustStore);
  // 实例化SSL上下文
  SSLContext ctx = SSLContext.getInstance("TLS");
  // 初始化SSL上下文
  ctx.init(keyManagerFactory.getKeyManagers(),
    trustManagerFactory.getTrustManagers(), null);
  // 获得SSLSocketFactory
  return ctx;
 }

 /**
  * 初始化HttpsURLConnection.
  * @param password  密码
  * @param keyStorePath  密钥库路径
  * @param trustStorePath  信任库路径
  * @throws Exception
  */
 public static void initHttpsURLConnection(String password,
   String keyStorePath, String trustStorePath) throws Exception {
  // 声明SSL上下文
  SSLContext sslContext = null;
  // 实例化主机名验证接口
  HostnameVerifier hnv = new MyHostnameVerifier();
  try {
   sslContext = getSSLContext(password, keyStorePath, trustStorePath);
  } catch (GeneralSecurityException e) {
   e.printStackTrace();
  }
  if (sslContext != null) {
   HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
  }
  HttpsURLConnection.setDefaultHostnameVerifier(hnv);
 }

 /**
  * 发送请求.
  * @param httpsUrl 请求的地址
  * @param method 设定请求的方法为"POST"，默认是GET 
  * @param xmlStr 请求的数据
  */
 public static String post(String httpsUrl,String method, String xmlStr) {
  HttpsURLConnection urlCon = null;
  StringBuilder sb = new StringBuilder();
  try {
   urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
   // 设置是否从httpUrlConnection读入，默认情况下是true;
   urlCon.setDoInput(true);
   // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;   
   urlCon.setDoOutput(true);
   // 设定请求的方法为"POST"，默认是GET   
   urlCon.setRequestMethod(method);
   // 设定传送的内容类型是可序列化的java对象   
   // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) 
   urlCon.setRequestProperty("Content-type", "application/json"); 
   urlCon.setRequestProperty("Content-Length",  String.valueOf(xmlStr.getBytes().length));
   //设置连接主机超时（单位：毫秒） 
   urlCon.setConnectTimeout(50000);
   //设置从主机读取数据超时（单位：毫秒） 
   urlCon.setReadTimeout(50000); 
   // Post 请求不能使用缓存
   urlCon.setUseCaches(false);
   //设置为utf8可以解决服务器接收时读取的数据中文乱码问题
   // 向对象输出流写出数据，这些数据将存到内存缓冲区中 
   urlCon.getOutputStream().write(xmlStr.getBytes("utf8"));
   // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
   urlCon.getOutputStream().flush();
   // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,   
   // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器 
   urlCon.getOutputStream().close();
   BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
   char[] buff = new char[512]; 
   int length = 0; 
   while ((length = in.read(buff)) != -1) { 
           sb.append(new String(buff, 0, length));
   } 
  } catch (MalformedURLException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  }
  return sb.toString();
 }
 /**
  * 发送请求.
  * @param httpsUrl 请求的地址
  * @param xmlStr 请求的数据
  */
 public static String get(String httpsUrl) {
  HttpsURLConnection urlCon = null;
  StringBuilder sb = new StringBuilder();
  try {
   urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
   // 设置是否从httpUrlConnection读入，默认情况下是true;
   urlCon.setDoInput(true);
   // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;   
   urlCon.setDoOutput(true);
   // 设定传送的内容类型是可序列化的java对象   
   // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) 
   urlCon.setRequestProperty("Content-type", "application/json"); 
   //设置连接主机超时（单位：毫秒） 
   urlCon.setConnectTimeout(5000);
   //设置从主机读取数据超时（单位：毫秒） 
   urlCon.setReadTimeout(5000); 
   // Post 请求不能使用缓存
   urlCon.setUseCaches(false);
   BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
   char[] buff = new char[512]; 
   int length = 0; 
   while ((length = in.read(buff)) != -1) { 
           sb.append(new String(buff, 0, length));
   } 
  } catch (MalformedURLException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  }
  return sb.toString();
 }

}