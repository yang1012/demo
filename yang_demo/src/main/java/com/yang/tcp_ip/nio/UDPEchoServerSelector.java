package com.yang.tcp_ip.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * @ClassName: UDPEchoServerSelector
 * @package com.yang.tcp_ip.nio
 * @Description: UDP通道 DatagramChannel 示例
 * @author Mr.yang
 * @date 2016年1月10日 下午9:21:31
 * @version V1.0
 */
public class UDPEchoServerSelector {

	/** 等待超时时间 */
	private static final int TIMEOUT = 3000;

	/** 最大接收数据 */
	private static final int ECHOMAX = 255;

	/**
	 * @Title: handleRead
	 * @Description: 读取处理
	 * @param key
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月10日 下午10:04:24
	 */
	public static void handleRead(SelectionKey key) throws IOException {
		// UDP通道
		DatagramChannel channel = (DatagramChannel) key.channel();

		// 选择键上的附件
		ClientRecord clntRec = (ClientRecord) key.attachment();

		/*
		 * capacity 缓冲区中的元素总数 （不可修改）; position 下一个要读/写的元素（从0开始）; limit
		 * 第一个不可读/写元素;mark 用户选定的position的前一个位置，或0
		 * clear()方法将position设置为0，并将limit设置为等于capacity
		 * ，从而使缓冲区准备好从缓冲区的put操作或信道的读操作接收新的数据。
		 * flip()方法用来将缓冲区准备为数据传出状态，这通过将limit设置为position的当前值，再将 position的值设为0来实。
		 * compact()方法将 position与limit之间的元素复制到缓冲区的开始位置，从而为后续的
		 * put()/read()调用让出空间。
		 * position的值将设置为要复制的数据的长度，limit的值将设置为capacity，mark则变成未定义
		 */
		clntRec.buffer.clear();
		// 获取目的地址
		clntRec.clientAddress = channel.receive(clntRec.buffer);
		if (clntRec.clientAddress != null) {

			// 设置该选择键感兴趣的选择键集
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}

	/**
	 * @Title: handleWrite
	 * @Description: 写入处理
	 * @param key
	 * @throws IOException
	 * @author Mr.yang
	 * @date 2016年1月10日 下午10:21:34
	 */
	public static void handleWrite(SelectionKey key) throws IOException {
		// UDP通道
		DatagramChannel channel = (DatagramChannel) key.channel();
		// 选择键上的附件
		ClientRecord clntRec = (ClientRecord) key.attachment();
		/*
		 * capacity 缓冲区中的元素总数 （不可修改）; position 下一个要读/写的元素（从0开始）; limit
		 * 第一个不可读/写元素;mark 用户选定的position的前一个位置，或0
		 * clear()方法将position设置为0，并将limit设置为等于capacity
		 * ，从而使缓冲区准备好从缓冲区的put操作或信道的读操作接收新的数据。
		 * flip()方法用来将缓冲区准备为数据传出状态，这通过将limit设置为position的当前值，再将 position的值设为0来实。
		 * compact()方法将 position与limit之间的元素复制到缓冲区的开始位置，从而为后续的
		 * put()/read()调用让出空间。
		 * position的值将设置为要复制的数据的长度，limit的值将设置为capacity，mark则变成未定义
		 */
		clntRec.buffer.flip();
		// 发送数据
		int bytesSent = channel.send(clntRec.buffer, clntRec.clientAddress);

		if (bytesSent != 0) {
			// 设置感兴趣的选择键集
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	static class ClientRecord {
		public SocketAddress clientAddress;
		public ByteBuffer buffer = ByteBuffer.allocate(ECHOMAX);
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 1)
			// 验证参数
			throw new IllegalArgumentException("Parameter(s): <Port>");

		int servPort = Integer.parseInt(args[0]);

		// 选择器
		Selector selector = Selector.open();

		DatagramChannel channel = DatagramChannel.open();
		// 非阻塞
		channel.configureBlocking(false);
		// 绑定一个本地地址、端口
		channel.socket().bind(new InetSocketAddress(servPort));
		// 注册选择器。添加附件
		channel.register(selector, SelectionKey.OP_READ, new ClientRecord());

		while (true) {
			// 选择就绪的键-直到超时进入下一次循环
			if (selector.select(TIMEOUT) == 0) {
				System.out.print(".");
				continue;
			}

			// 返回选择器的已选择键集的迭代器
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while (keyIter.hasNext()) {
				// 准备就绪的选择键
				SelectionKey key = keyIter.next();

				if (key.isReadable()) {
					// 可读
					handleRead(key);
				}

				if (key.isValid() && key.isWritable()) {

					// 选择键可用 && 可写
					handleWrite(key);
				}

				// 完成操作后，移除该选择键
				keyIter.remove();
			}
		}
	}

}