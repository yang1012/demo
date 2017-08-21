package com.yang.tcp_ip.nio;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 
 * @ClassName: TCPEchoClientNonblocking
 * @package com.yang.tcp_ip.nio
 * @Description: 非阻塞 式TCP回显客户端 在非阻塞式信道上调用一个方法总是会立即返回。这种调用的返回值指示了所请求的操作完成的程度。例如，
 *               在一个非阻塞式ServerSocketChannel上调用accept
 *               ()方法，如果有连接请求在等待，则返回客户端SocketChannel，否则返回null。
 *               通过使用非阻塞式信道，这些操作都将立即返回。我们必须反复调用这些操作，直到所有I/O操作都成功完成。
 * @author 杨森
 * @date 2015年12月25日 上午9:50:13
 * @version V1.0
 */
public class TCPEchoClientNonblocking {
	public static void main(String args[]) throws Exception {
		if ((args.length < 2) || (args.length > 3))
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): <Server> <Word> [<Port>]");

		// 服务端主机名或ip地址
		String server = args[0];
		// 使用系统平台默认的编码格式将输入转换为字节数组
		byte[] argument = args[1].getBytes();

		// 服务端端口
		int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;

		// 信道客户端
		SocketChannel clntChan = SocketChannel.open();
		// 配置阻塞行为-非阻塞
		clntChan.configureBlocking(false);

		/*
		 * 注意：由于该套接字是非阻塞式的，因此对connect()方法的调用可能会在连接建立之前返回，如果在返回前已经成功建立了连接，则返回true，
		 * 否则返回false。 对于后一种情况，任何试图发送或接收数据的操作都将抛出NotYetConnectedException异常。
		 * 同样的如果该套接字是阻塞式的，connect() 则在建立连接或发生 I/O 错误之前将阻塞此方法的调用。
		 * 
		 * 多个并发线程可安全地使用套接字通道。尽管在任意给定时刻最多只能有一个线程进行读取和写入操作，但数据报通道支持并发的读写。connect 和
		 * finishConnect 方法是相互同步的，如果正在调用其中某个方法的同时试图发起读取或写入操作，则在该调用完成之前该操作被阻塞。
		 */
		if (!clntChan.connect(new InetSocketAddress(server, servPort))) {
			/*
			 * 初始化连接到服务端，完成套接字通道的连接过程。--通过持续调用finishConnect()方法来"轮询"连接状态，
			 * 该方法在连接成功建立之前一直返回false。 注意：忙等待非常浪费系统资源
			 */
			while (!clntChan.finishConnect()) {
				// 程序还可以执行其他任务
				System.out.print("do something eles");
			}
		}
		// 创建读写缓冲区
		// 通过包装包含了要发送数据的byte[]数组 创建ByteBuffer实例
		ByteBuffer writeBuf = ByteBuffer.wrap(argument);
		// 调用allocate()方法，创建具有与前面byte[]数组大小相同缓冲区的ByteBuffer实例
		ByteBuffer readBuf = ByteBuffer.allocate(argument.length);

		// 读取的总字节数
		int totalBytesRcvd = 0;
		// 当前读取的字节数
		int bytesRcvd;

		// 反复循环直到发送和接收完所有字节
		while (totalBytesRcvd < argument.length) {
			// 只要输出缓冲区中还留有数据，就调用write()方法 输出写入到服务端
			if (writeBuf.hasRemaining()) {
				clntChan.write(writeBuf);
			}
			// 对read()方法的调用不会阻塞等待，但是当没有数据可读时该方法将返回0。读取服务端的信息
			if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
				throw new SocketException("Connection closed prematurely");
			}
			// 计算读取的字节总数
			totalBytesRcvd += bytesRcvd;
			// 强调说明 循环过程中还可以做其他事情
			System.out.print("do something else!");
		}
		// 转换成当前系统默认平台的输出
		System.out.println("Received: " + new String(readBuf.array(), 0, totalBytesRcvd));

		// 关闭客户端信道
		clntChan.close();

	}
}