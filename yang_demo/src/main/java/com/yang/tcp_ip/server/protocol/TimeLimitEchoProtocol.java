package com.yang.tcp_ip.server.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @ClassName: TimelimitEchoProtocol
 * @package com.yang.tcp_ip.server.protocol
 * @Description: 默认情况下，DatagramSocket类的receive()方法将无限期地阻塞等待一个数据报文
 * @author 杨森
 * @date 2015年12月18日 下午2:26:41
 * @version V1.0
 */
class TimelimitEchoProtocol implements Runnable {
	/** 缓冲区大小 */
	private static final int BUFSIZE = 32;
	/** 默认超时时间（单位：毫秒）（10秒） */
	private static final String TIMELIMIT = "10000";
	private static final String TIMELIMITPROP = "Timelimit"; // Property

	private static int timelimit;
	private Socket clntSock;
	private Logger logger;

	public TimelimitEchoProtocol(Socket clntSock, Logger logger) {
		this.clntSock = clntSock;
		this.logger = logger;
		// 获取属性文件设置的超时时间，如果不存在，则默认为 TIMELIMIT
		timelimit = Integer.parseInt(System.getProperty(TIMELIMITPROP, TIMELIMIT));
	}

	public static void handleEchoClient(Socket clntSock, Logger logger) {

		try {
			// 套接字输入流
			InputStream in = clntSock.getInputStream();
			// 套接字输出流
			OutputStream out = clntSock.getOutputStream();
			// 单次-已经接收的数组大小
			int recvMsgSize;
			// 合计-已经接收的数组大小
			int totalBytesEchoed = 0;
			// 缓冲区
			byte[] echoBuffer = new byte[BUFSIZE];
			// 服务截止时间 = 当前系统时间 + 超时时间
			long endTime = System.currentTimeMillis() + timelimit;
			int timeBoundMillis = timelimit;

			// 套接字超时时间
			clntSock.setSoTimeout(timeBoundMillis);
			// 接收数据直到客户端套接字关闭连接
			while ((timeBoundMillis > 0) && // catch zero values
					((recvMsgSize = in.read(echoBuffer)) != -1)) {
				// 缓冲区中的数据写入输出流
				out.write(echoBuffer, 0, recvMsgSize);
				// 记录读取的总字节数
				totalBytesEchoed += recvMsgSize;
				// 计算服务剩余时间 = 服务截止时间 - 当前系统时间
				timeBoundMillis = (int) (endTime - System.currentTimeMillis());
				// 设置超时时间 = 服务剩余时间
				clntSock.setSoTimeout(timeBoundMillis);
			}

			logger.info("Client " + clntSock.getRemoteSocketAddress() + ", echoed " + totalBytesEchoed + " bytes.");
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Exception in echo protocol", ex);
		}
	}

	public void run() {
		handleEchoClient(this.clntSock, this.logger);
	}
}