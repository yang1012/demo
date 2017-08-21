package com.yang.tcp_ip.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yang.tcp_ip.server.protocol.EchoProtocol;

/**
 * @ClassName: TCPEchoServerPool
 * @package com.yang.tcp_ip.server
 * @Description: TCP 服务端线程池
 * @author 杨森
 * @date 2015年12月18日 下午1:18:45
 * @version V1.0
 */
public class TCPEchoServerPool {
	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			// 参数有效性
			throw new IllegalArgumentException("Parameter(s): <Port> <Threads>");
		}

		// 服务端端口
		int echoServPort = Integer.parseInt(args[0]);
		// 线程池大小
		int threadPoolSize = Integer.parseInt(args[1]);

		// TCP套接字服务端
		final ServerSocket servSock = new ServerSocket(echoServPort);

		final Logger logger = Logger.getLogger("practical");

		// 思考该创建线程池的替换为线程调度器创建线程池
		// Executor service = Executors.newFixedThreadPool(threadPoolSize);

		// 创建一个固定数量的线程 来接受客户端请求
		for (int i = 0; i < threadPoolSize; i++) {
			Thread thread = new Thread() {
				public void run() {
					while (true) {
						try {
							// 等待一个套接字连接请求
							Socket clntSock = servSock.accept();
							//
							EchoProtocol.handleEchoClient(clntSock, logger);

						} catch (IOException ex) {
							logger.log(Level.WARNING, "Client accept failed", ex);
						}
					}
				}
			};
			thread.start();

			logger.info("Created and started Thread = " + thread.getName());
		}
	}
}