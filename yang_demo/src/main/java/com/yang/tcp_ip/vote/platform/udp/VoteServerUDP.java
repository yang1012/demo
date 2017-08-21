package com.yang.tcp_ip.vote.platform.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgTextCoder;
import com.yang.tcp_ip.vote.service.VoteService;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteServerUDP
 * @package com.yang.tcp_ip.vote.platform.udp
 * @Description: UDP服务端
 * @author 杨森
 * @date 2015年12月16日 下午1:32:56
 * @version V1.0
 */
public class VoteServerUDP {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			// 验证参数个数合法性
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		// 接收地址端口
		int port = Integer.parseInt(args[0]);
		// 接收端套接字
		DatagramSocket sock = new DatagramSocket(port); //
		byte[] inBuffer = new byte[VoteMsgTextCoder.MAX_WIRE_LENGTH];

		//
		VoteMsgCoder coder = new VoteMsgTextCoder();
		VoteService service = new VoteService();

		while (true) {
			DatagramPacket packet = new DatagramPacket(inBuffer, inBuffer.length);
			// 接收UDP消息
			sock.receive(packet);
			byte[] encodedMsg = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
			System.out.println("Handling request from " + packet.getSocketAddress() + " (" + encodedMsg.length + " bytes)");

			try {
				// 反序列化-解析消息
				VoteMsg msg = coder.fromWire(encodedMsg);
				msg = service.handleRequest(msg);
				// 序列化-构建消息
				packet.setData(coder.toWire(msg));
				System.out.println("Sending response (" + packet.getLength() + " bytes):");
				System.out.println(msg);
				// 发送UDP响应消息
				sock.send(packet);
			} catch (IOException ioe) {
				System.err.println("Parse error in message: " + ioe.getMessage());
			}
		}

	}
}