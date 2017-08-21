package com.yang.tcp_ip.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.yang.tcp_ip.server.protocol.EchoProtocol;

/**
 * @ClassName: TCPEchoServerThread
 * @package com.yang.tcp_ip.server
 * @Description: 为每次客户端请求重新生成一个单独的线程来处理
 * @author 杨森
 * @date 2015年12月16日 下午3:37:43
 * @version V1.0
 */
public class TCPEchoServerThread {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			// 简单验证参数个数合法性
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		// 服务端口号 - 即套接字监听的端口号
		int echoServPort = Integer.parseInt(args[0]);

		// 创建一个服务端套接字接收客户端的连接请求
		ServerSocket servSock = new ServerSocket(echoServPort);

		Logger logger = Logger.getLogger("practical");

		// 循环执行，并为每一个请求连接生成一个新的单独的线程来执行
		while (true) {
			// 等待请求连接
			Socket clntSock = servSock.accept();
			// 生成一个新的线程处理连接请求
			Thread thread = new Thread(new EchoProtocol(clntSock, logger));
			thread.start();
			logger.info("Created and started Thread " + thread.getName());
		}
		/* NOT REACHED */

	}
}