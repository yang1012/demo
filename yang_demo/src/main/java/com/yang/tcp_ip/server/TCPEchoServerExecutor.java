package com.yang.tcp_ip.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.yang.tcp_ip.server.protocol.EchoProtocol;

/**
 * @ClassName: TCPEchoServerExecutor
 * @package com.yang.tcp_ip.server
 * @Description: 基于调度器的多线程套接字服务端管理
 * @author 杨森
 * @date 2015年12月18日 下午1:37:59
 * @version V1.0
 */
public class TCPEchoServerExecutor {
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		// 服务端口
		int echoServPort = Integer.parseInt(args[0]);

		// 套接字服务端接受客户端请求
		ServerSocket servSock = new ServerSocket(echoServPort);
		Logger logger = Logger.getLogger("practical");

		// 线程调度器
		Executor service = Executors.newCachedThreadPool();
		// Executor service = Executors.newFixedThreadPool(threadPoolSize);
		// Executor service = Executors.newSingleThreadExecutor();

		// 循环执行，等待客户端套接字请求，创建一个新的线程分配给线程调度器
		while (true) {
			// 阻塞 等待客户端套接字请求
			
			Socket clntSock = servSock.accept();
			service.execute(new EchoProtocol(clntSock, logger));
			// 超时的协议
			// service.execute(new TimeLimitEchoProtocol(clntSock, logger));
		}
		/* NOT REACHED */
	}
}