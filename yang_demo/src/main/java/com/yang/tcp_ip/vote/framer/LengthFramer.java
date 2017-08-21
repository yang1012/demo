package com.yang.tcp_ip.vote.framer;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName: LengthFramer
 * @package com.yang.tcp_ip.Framer
 * @Description: LengthFramer.java类实现了基于长度的成帧方法，适用于长度小于65535 (216 ?
 *               1)字节的消息。发送者首先给出指定消息的长度
 *               ，并将长度信息以big-endian顺序存入两个字节的整数中，再将这两个字节放在完整的消息内容前
 *               ，连同消息一起写入输出流。在接收端，我们使用DataInputStream以读取整型的长度信息；readFully()
 *               方法将阻塞等待，直到给定的
 *               数组完全填满，这正是我们需要的。值得注意的是，使用这种成帧方法，发送者不需要检查要成帧的消息内容，而只需要检查消息的长度是否超出了限制
 *               。
 * @author 杨森
 * @date 2015年12月10日 上午9:51:34
 * @version V1.0
 */
public class LengthFramer implements Framer {

	/** 最大消息的长度不能超过65535 */
	public static final int MAXMESSAGELENGTH = 65535;
	// 十六进制 一字节 8位 1111 1111
	public static final int BYTEMASK = 0xff;
	// 十六进制 两字节 16位 1111 1111 1111 1111
	public static final int SHORTMASK = 0xffff;

	public static final int BYTESHIFT = 8;

	private DataInputStream in;

	public LengthFramer(InputStream in) throws IOException {
		this.in = new DataInputStream(in);
	}

	/**
	 * @Title: frameMsg
	 * @Description: 基于长度的成帧方法
	 * @param message
	 * @param out
	 * @throws IOException
	 * @author : 杨森
	 * @date 2015年12月15日 上午9:36:08
	 * @see com.yang.tcp_ip.vote.framer.Framer#frameMsg(byte[],
	 *      java.io.OutputStream)
	 */
	public void frameMsg(byte[] message, OutputStream out) throws IOException {
		if (message.length > MAXMESSAGELENGTH) {
			throw new IOException("message too long");
		}
		// BIG-ENDIAN 高字节位于低位，低字节位于高位
		// 添加长度信息（无符号short型整数）前缀，输出消息的字节数。(分别输出无符号short型整数前高8位 ，后8位)
		// （无符号short型整数）前缀 文本长度高位字节
		out.write((message.length >> BYTESHIFT) & BYTEMASK);
		// 文本长度低位字节
		out.write(message.length & BYTEMASK);
		// write message
		out.write(message);
		out.flush();
	}

	// 从输入流中提取下一帧
	public byte[] nextMsg() throws IOException {
		int length;
		try {
			// 读取两个字节，将它们作为big-endian整数进行解释，并以int型整数返回它们的值。信息长度
			length = in.readUnsignedShort(); // read 2 bytes
		} catch (EOFException e) { // no (or 1 byte) message
			return null;
		}
		// 0 <= length <= 655355

		byte[] msg = new byte[length];
		// readfully() 将阻塞等待，直到接收到足够的字节来填满指定的数组。
		in.readFully(msg); // if exception, it's a framing error.
		return msg;
	}

	public static void main(String args[]) {
		byte[] message = new byte[65535];
		// 十六进制 一字节 8位 1111 1111
		int BYTEMASK = 0xff;
		// 十六进制 两字节 16位 1111 1111 1111 1111
		int SHORTMASK = 0xffff;
		System.out.println(message.length >> BYTESHIFT);
		System.out.println((message.length >> BYTESHIFT) & BYTEMASK);
		System.out.println(message.length & BYTEMASK);
		System.out.println(message.length & SHORTMASK);

	}
}