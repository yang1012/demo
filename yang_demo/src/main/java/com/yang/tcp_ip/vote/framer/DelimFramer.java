package com.yang.tcp_ip.vote.framer;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName: DelimFramer
 * @package com.yang.tcp_ip
 * @Description: 实现了基于定界符的成帧方法，其定界符为"换行"符（"\n", 字节值为10）。
 *               frameMethod()方法并没有实现填充，当成帧的字节序列中包含有定界符时
 *               ，它只是简单地抛出异常。（扩展该方法以实现填充功能将作为练习留给读者
 *               ）nextMsg()方法扫描流，直到读取到了定界符，并返回定界符前面的所有字符
 *               ，如果流为空则返回null。如果累积了一个消息的不少字符
 *               ，但直到流结束也没有找到定界符，程序将抛出一个异常来指示成帧错误。(该类只在输出流末尾添加了定界符
 *               ，也可在输出流中间部分添加定界符，甚至可以设置多个不同值作为定界符，关键在于有关成帧信息的添加和解析的具体操作)
 * @author 杨森
 * @date 2015年12月10日 上午9:46:04
 * @version V1.0
 */
public class DelimFramer implements Framer {
	private InputStream in; // data source

	// 定界符为"换行"符（"\n", 字节值为10）。
	private static final byte DELIMITER = 10; // message delimiter

	public DelimFramer(InputStream in) {
		this.in = in;
	}

	// 添加成帧信息并将指定消息输出到指定流(基于回车换行的定界符成帧信息)
	public void frameMsg(byte[] message, OutputStream out) throws IOException {
		// ensure that the message does not contain the delimiter
		for (byte b : message) {
			if (b == DELIMITER) {
				throw new IOException("Message contains delimiter");
			}
		}
		out.write(message);
		out.write(DELIMITER);
		out.flush();
	}

	public byte[] nextMsg() throws IOException {
		ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
		int nextByte;

		// fetch bytes until find delimiter
		while ((nextByte = in.read()) != DELIMITER) {
			if (nextByte == -1) { // end of stream?
				if (messageBuffer.size() == 0) { // if no byte read
					return null;
				} else { // if bytes followed by end of stream: framing error
					throw new EOFException("Non-empty message without delimiter");
				}
			}
			messageBuffer.write(nextByte); // write byte to buffer

		}

		return messageBuffer.toByteArray();
	}
}