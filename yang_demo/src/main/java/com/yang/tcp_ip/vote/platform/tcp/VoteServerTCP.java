package com.yang.tcp_ip.vote.platform.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.yang.tcp_ip.vote.framer.Framer;
import com.yang.tcp_ip.vote.framer.LengthFramer;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgBinCoder;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder;
import com.yang.tcp_ip.vote.service.VoteService;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteServerTCP
 * @package com.yang.tcp_ip.vote.platform.tcp
 * @Description: TCP投票服务端
 * @author 杨森
 * @date 2015年12月16日 上午10:09:52
 * @version V1.0
 */
public class VoteServerTCP {
	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			// 验证参数长度的正确性
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		// 服务器套接字端口
		int port = Integer.parseInt(args[0]);

		ServerSocket servSock = new ServerSocket(port);
		// 基于二进制的VoteMsg 构建和解析消息协议
		VoteMsgCoder coder = new VoteMsgBinCoder();
		// 根据请求，返回封装的信息服务端
		VoteService service = new VoteService();

		while (true) {
			// 接收套接字请求
			Socket clntSock = servSock.accept();
			System.out.println("Handling client at " + clntSock.getRemoteSocketAddress());

			// 基于长度成帧信息
			Framer framer = new LengthFramer(clntSock.getInputStream());
			try {
				byte[] req;
				while ((req = framer.nextMsg()) != null) {
					System.out.println("Received message (" + req.length + " bytes)");
					// 反序列化-解析消息 并发送到服务处理请求
					VoteMsg responseMsg = service.handleRequest(coder.fromWire(req));
					// 将服务端返回的信息添加成帧信息返回给客户端
					framer.frameMsg(coder.toWire(responseMsg), clntSock.getOutputStream());
				}
			} catch (IOException ioe) {
				System.err.println("Error handling client: " + ioe.getMessage());
			} finally {
				System.out.println("Closing connection");
				clntSock.close();
			}
		}
	}
}