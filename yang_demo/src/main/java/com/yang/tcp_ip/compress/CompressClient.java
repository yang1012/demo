package com.yang.tcp_ip.compress;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @ClassName: CompressClient
 * @package com.yang.tcp_ip.compress
 * @Description: 压缩客户端示例程序 WARNING: this code can deadlock if a large file (more
 *               than a few 8 * 's of thousands of bytes) is sent 压缩协议的客户端
 * @author 杨森
 * @date 2015年12月23日 下午2:54:29
 * @version V1.0
 */
public class CompressClient {

	/** 读取缓冲 */
	public static final int BUFSIZE = 256;

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): <Server> <Port> <File>");
		}

		// 服务端名或者ip地址
		String server = args[0];
		// 服务端端口
		int port = Integer.parseInt(args[1]);
		// 源文件
		String filename = args[2];

		// 文件输入流
		FileInputStream fileIn = new FileInputStream(filename);
		// 文件输出流
		FileOutputStream fileOut = new FileOutputStream(filename + ".gz");

		// 客户端套接字
		Socket sock = new Socket(server, port);

		// 发送没有压缩的字节到服务端
		sendBytes(sock, fileIn);

		// 从服务端接受经过压缩的字节流信息
		InputStream sockIn = sock.getInputStream();
		int bytesRead;
		// 字节缓冲区
		byte[] buffer = new byte[BUFSIZE];
		while ((bytesRead = sockIn.read(buffer)) != -1) {
			//
			fileOut.write(buffer, 0, bytesRead);
			System.out.print("R");
		}
		System.out.println();

		// 关闭客户端套接字
		sock.close();
		// 关闭文件输入流
		fileIn.close();
		// 关闭文件输出流
		fileOut.close();
	}

	/**
	 * @Title: sendBytes
	 * @Description: 发送字节流
	 * @param sock
	 * @param fileIn
	 * @throws IOException
	 * @author 杨森
	 * @date 2015年12月23日 下午2:58:26
	 */
	private static void sendBytes(Socket sock, InputStream fileIn) throws IOException {
		// 套接字输出流
		OutputStream sockOut = sock.getOutputStream();
		// 读取的文件字节数
		int bytesRead;
		// 字节缓冲区
		byte[] buffer = new byte[BUFSIZE];
		while ((bytesRead = fileIn.read(buffer)) != -1) {
			// 输入流中读取数据，发送字节流信息
			sockOut.write(buffer, 0, bytesRead);
			// 标志
			System.out.print("W");
		}
		// 客户端关闭输出流，服务端输入流仍可读取数据，服务端无数据读取时返回-1
		sock.shutdownOutput();
	}
}