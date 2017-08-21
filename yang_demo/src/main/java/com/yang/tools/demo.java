package com.yang.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

/**
 * @ClassName: demo
 * @package com.yang.tools
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Mr.yang
 * @date 2016年1月6日 下午9:23:42
 * @version V1.0
 */
public class demo {

	/**
	 * SSLContext 此类的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine
	 * 的工厂。用可选的一组密钥和信任管理器及安全随机字节源初始化此类。 1. 使用keytool生成公开密匙（不对称密匙）和证书。 2.
	 * 花钱请可信任的第三方（如：Comodo）认证你的证书。 3. 为你使用的算法创建一个SSLContext。 4.
	 * 为你要使用的证书源型创建一个TrustManagerFactory。 5.
	 * 为你要使用的密匙类型创建一个KeyManagerFactory（javax.net.ssl.X509KeyManager ）。 6.
	 * 为密匙和证书数据库创建一个KeyStor对象（Oralce默认使用的是JKS）。 7.
	 * 用密匙和证书填充KeyStore对象。例如，使用加密所用的口令短语从文件系统加载。 8.
	 * 用KeyStore及其口令短语初始化KeyManagerFactory。 9.
	 * 用KeyManagerFactory中的密匙管理器（必要）、TrustManagerFactory中的信任管理器和一个随机源来初始化上下文
	 * （如果愿意接受默认值，后两个可以为null）。
	 * 
	 * certmgr.msc 查看证书
	 */
	private void init_SSLContext() {
		try {
			// 创建一个SSLContext。
			SSLContext context = SSLContext.getInstance("SSL");

			// 为你要使用的证书源型创建一个TrustManagerFactory。 参考实现只支持X.509密匙
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

			// 为密匙和证书数据库创建一个KeyStor对象（Oralce默认使用的是JKS）。
			KeyStore ks = KeyStore.getInstance("JKS");

			// 出于安全考虑，每个密匙库都必须用口令短语加密，在从磁盘加载前必须提供口令。口令短语以char[]数组形式存储，所以可以很快的从内存中擦除
			// 等待垃圾回收
			char[] password = System.console().readPassword();

			// ks.load(param);
			// 用密匙和证书填充KeyStore对象。例如，使用加密所用的口令短语从文件系统加载。
			ks.load(new FileInputStream("jnp4e.keys"), password);

			// 用KeyStore及其口令短语初始化KeyManagerFactory
			kmf.init(ks, password);

			// 用KeyManagerFactory中的密匙管理器（必要）、TrustManagerFactory中的信任管理器和一个随机源来初始化上下文
			// （如果愿意接受默认值，后两个可以为null）。
			context.init(kmf.getKeyManagers(), null, null);
			// context.init(kmf.getKeyManagers(), null, new SecureRandom());

			// 擦除口令
			// Arrays.fill(password, 0);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		int[] num = { 3, 5, 5, 9 };
		String[] cal = { "+", "-", "*", "/" };
		String trace = "";
		int result = 24;
		int cal_result = 0;

		Set trace_set = new HashSet();

		// 当前取出来的数字
		for (int a = 0; a < num.length;) {
			for (int b = a + 1; b < num.length;) {
				for (int c = b + 1; c < num.length;) {
					for (int d = c + 1; d < num.length;) {

					}
				}
			}
		}

	}
}
