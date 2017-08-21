 package com.yang.tcp_ip.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import com.yang.tcp_ip.nio.protocol.EchoSelectorProtocol;
import com.yang.tcp_ip.nio.protocol.TCPProtocol;

public class TCPServerSelector {
	/** 缓冲区size */
	private static final int BUFSIZE = 256;
	/** 超时时间 */
	private static final int TIMEOUT = 3000;

	public static void main(String[] args) throws IOException { 

		if (args.length < 1) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): <Port> ...");
		}

		// Create a selector to multiplex listening sockets and connections
		//
		Selector selector = Selector.open();

		// Create listening socket channel for each port and register selector
		for (String arg : args) {
			// 服务器套接字通道 -- 新通道的套接字最初是未绑定的；可以接受连接之前，必须通过它的某个套接字的 bind
			// 方法将其绑定到具体的地址
			ServerSocketChannel listnChannel = ServerSocketChannel.open();

			/*
			 * 将 ServerSocket 绑定到特定地址（IP 地址和端口号）。 如果地址为
			 * null，则系统将挑选一个临时端口和一个有效本地地址来绑定套接字。
			 */

			listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(arg)));
			// 配置阻塞行为-非阻塞
			listnChannel.configureBlocking(false);
			// 只有非阻塞信道才可以注册选择器，因此需要将其配置为适当的状态。为信道注册选择器
			listnChannel.register(selector, SelectionKey.OP_ACCEPT);
		}

		// 协议操作器
		TCPProtocol protocol = new EchoSelectorProtocol(BUFSIZE);

		// 反复循环，等待I/O，调用操作器
		while (true) {
			// select()方法将阻塞等待，直到有准备好I/O操作的信道，或直到发生了超时。该方法将返回准备好的信道数
			if (selector.select(TIMEOUT) == 0) {
				System.out.print(".");
				continue;
			}

			// 获取所选择的键集,集合中包含了每个准备好某一I/O操作的信道的SelectionKey（在注册时创建）
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next();
				// 对于每个键，检查其是否准备好进行accep()操作
				if (key.isAcceptable()) {
					protocol.handleAccept(key);
				}
				// 是否可读或可写
				if (key.isReadable()) {
					protocol.handleRead(key);
				}
				// 可以检测一个键的有效性 & 是否可写
				if (key.isValid() && key.isWritable()) {
					protocol.handleWrite(key);
				}
				/*
				 * 由于select()操作只是向Selector所关联的键集合中添加元素，因此，如果不移除每个处理过的键，
				 * 它就会在下次调用select()方法是仍然保留在集合中，而且可能会有无用的操作来调用它。
				 */
				keyIter.remove();
			}
		}
	}
}