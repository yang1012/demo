package com.yang.tcp_ip.server.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @ClassName: EchoProtocol
 * @package com.yang.tcp_ip.server.protocol
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 杨森
 * @date 2015年12月16日 下午2:50:20
 * @version V1.0
 */
public class EchoProtocol implements Runnable {
	/** 静态常量：缓冲区大小 */
	private static final int BUFSIZE = 32;
	/** 客户端套接字 */
	private Socket clntSock;
	/** 日志记录 */
	private Logger logger;

	/**
	 * Title:构造函数 Description: 初始化客户端套接字，日志记录
	 * 
	 * @author : 杨森
	 * @param clntSock
	 * @param logger
	 */
	public EchoProtocol(Socket clntSock, Logger logger) {

		this.clntSock = clntSock;
		this.logger = logger;
	}

	/**
	 * @Title: handleEchoClient
	 * @Description: 该协议事例对于请求的信息未进行任何处理
	 *               只是将请求信息重新写入输出流。有关接收请求信息后，进行处理的操作流程可参考投票实例
	 * @param clntSock
	 * @param logger
	 * @author 杨森
	 * @date 2015年12月16日 下午3:35:09
	 */
	public static void handleEchoClient(Socket clntSock, Logger logger) {
		try {
			// 套接字输入流
			InputStream in = clntSock.getInputStream();
			// 套接字输出流
			OutputStream out = clntSock.getOutputStream();
			// 消息接收的长度
			int recvMsgSize;
			// 接收的字节数
			int totalBytesEchoed = 0;
			// 数据缓冲字节数组
			byte[] echoBuffer = new byte[BUFSIZE];
			// 读取输入流信息直到返回-1，每次读取记录读取的字节数，并将读取的信息写入其输出流。记录接收的总字节数

			while ((recvMsgSize = in.read(echoBuffer)) != -1) {
				out.write(echoBuffer, 0, recvMsgSize);
				totalBytesEchoed += recvMsgSize;
			}

			logger.info("Client " + clntSock.getRemoteSocketAddress() + ", echoed " + totalBytesEchoed + " bytes.");

		} catch (IOException ex) {
			logger.log(Level.WARNING, "Exception in echo protocol", ex);
		} finally {
			try {
				clntSock.close();
			} catch (IOException e) {
			}
		}
	}

	public void run() {
		handleEchoClient(clntSock, logger);
	}
}