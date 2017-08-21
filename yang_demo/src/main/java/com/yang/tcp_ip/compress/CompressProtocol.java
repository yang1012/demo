package com.yang.tcp_ip.compress;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName: CompressProtocol
 * @package com.yang.tcp_ip.compress
 * @Description: 压缩协议 -使用GZIP压缩算法实现了服务器端的压缩协议
 * @author 杨森
 * @date 2015年12月24日 下午3:39:08
 * @version V1.0
 */
public class CompressProtocol implements Runnable {

	/** 缓冲区 大小 */
	public static final int BUFSIZE = 1024;
	/** 客户端socket */
	private Socket clntSock;
	/** 日志 */
	private Logger logger;

	/**
	 * Title: Description: 构造函数
	 * 
	 * @author : 杨森
	 * @param clntSock
	 * @param logger
	 */
	public CompressProtocol(Socket clntSock, Logger logger) {
		this.clntSock = clntSock;
		this.logger = logger;
	}

	/**
	 * @Title: handleCompressClient
	 * @Description:从客户端读取未压缩字节并将压缩后的字节写回客户端。
	 * @param clntSock
	 * @param logger
	 * @author 杨森
	 * @date 2015年12月24日 下午3:41:40
	 */
	public static void handleCompressClient(Socket clntSock, Logger logger) {
		try {
			// 套接字中获取文件输入流
			InputStream in = clntSock.getInputStream();
			// 套接字中获取文件输出流
			GZIPOutputStream out = new GZIPOutputStream(clntSock.getOutputStream());

			// 缓冲区
			byte[] buffer = new byte[BUFSIZE];
			int bytesRead;
			// 接收客户端数据
			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);
			// 关闭GZIPOutputStream之前需要刷新提交可能被压缩算法缓存的字节
			out.finish();

			logger.info("Client " + clntSock.getRemoteSocketAddress() + " finished");
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Exception in echo protocol", ex);
		}

		try {
			// 关闭客户端套接字
			clntSock.close();
		} catch (IOException e) {
			logger.info("Exception = " + e.getMessage());
		}
	}

	public void run() {
		handleCompressClient(this.clntSock, this.logger);
	}
}