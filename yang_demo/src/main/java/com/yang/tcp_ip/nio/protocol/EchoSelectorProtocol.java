package com.yang.tcp_ip.nio.protocol;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * @ClassName: EchoSelectorProtocol
 * @package com.yang.tcp_ip.nio.protocol
 * @Description: 实现了TCPProtocol接口，每个实例都包含了将要为每个客户端信道创建的缓冲区大小。
 * @author Mr.yang
 * @date 2016年1月1日 下午4:09:38
 * @version V1.0
 */
public class EchoSelectorProtocol implements TCPProtocol {

	/** 缓冲区大小 */
	private int bufSize;

	public EchoSelectorProtocol(int bufSize) {
		this.bufSize = bufSize;
	}

	/**
	 * @Title: handleAccept
	 * @Description: 从键中获取信道，并接受连接
	 * @param key
	 * @throws IOException
	 * @author : Mr.yang
	 * @date 2016年1月1日 下午4:13:21
	 * @see com.yang.tcp_ip.nio.protocol.TCPProtocol#handleAccept(java.nio.channels.SelectionKey)
	 */
	public void handleAccept(SelectionKey key) throws IOException {
		/*
		 * channel()方法返回注册时用来创建键的Channel。（我们知道该Channel是一个ServerSocketChannel，
		 * 因为这是我们注册的惟一一种支持"accept"操作的信道。）accept()方法为传入的连接返回一个SocketChannel实例。
		 */
		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		// 设置为非阻塞模式,再次提醒，这里无法注册阻塞式信道。
		clntChan.configureBlocking(false);
		/*
		 * 为信道注册选择器
		 * 可以通过SelectionKey类的selector()方法来获取相应的Selector。我们根据指定大小创建了一个新的ByteBuffer实例
		 * ，并将其作为参数传递给register()方法。它将作为附件，与register()方法所返回的SelectionKey实例相关联。（
		 * 在此我们忽略了返回的键，但当信道准备好读数据的I/O操作时，可以通过选出的键集对其进行访问。）
		 */
		clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));
	}

	public void handleRead(SelectionKey key) throws IOException {

		// 获取键关联的信道
		SocketChannel clntChan = (SocketChannel) key.channel();
		// 获取键关联的缓冲区,连接建立后，有一个ByteBuffer附加到该SelectionKey实例上。
		ByteBuffer buf = (ByteBuffer) key.attachment();
		// 从信道中读数据
		long bytesRead = clntChan.read(buf);
		if (bytesRead == -1) {
			// 检查数据流的结束并关闭信道,关闭信道时，将从选择器的各种集合中移除与该信道关联的键。
			clntChan.close();
		} else if (bytesRead > 0) {
			// 如果接收完数据，将其标记为可写,注意，这里依然保留了信道的可读操作，虽然缓冲区中可能已经没有剩余空间了
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

	public void handleWrite(SelectionKey key) throws IOException {

		// 获取包含数据的缓冲区,附加到SelectionKey上的ByteBuffer包含了之前从信道中读取的数据。
		ByteBuffer buf = (ByteBuffer) key.attachment();
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
		/*
		 * Buffer的内部状态指示了在哪里放入下一批数据，以及缓冲区还剩多少空间。flip()方法用来修改缓冲区的内部状态，以指示write()
		 * 操作从什么地方获取数据，以及还有剩余多少数据。该方法的作用是使写数据的操作开始消耗由读操作产生的数据。
		 */
		buf.flip();
		SocketChannel clntChan = (SocketChannel) key.channel();
		clntChan.write(buf);
		if (!buf.hasRemaining()) {
			// 如果缓冲区为空，则标记为不再写数据,如果缓冲区中之前接收的数据已经没有剩余，则修改该键关联的操作集，指示其只能进行读操作。
			key.interestOps(SelectionKey.OP_READ);
		}
		/*
		 * 如果缓冲区中还有剩余数据，该操作则将其移动到缓冲区的前端，以使下次迭代能够读入更多的数据 在任何情况下
		 * ，该操作都将重置缓冲区的状态，因此缓冲区又变为可读。注意，除了在handleWrite()方法内部，与信道关联的缓冲区始终是设置为可读的
		 */
		buf.compact();
	}

}
