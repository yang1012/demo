package com.yang.tcp_ip.vote.platform.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgTextCoder;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteClientUDP
 * @package com.yang.tcp_ip.vote.platform.udp
 * @Description: UDP客户端（UDP协议不需要成帧信息，协议本身存在消息的边界信息）
 * @author 杨森
 * @date 2015年12月16日 下午1:23:55
 * @version V1.0
 */
public class VoteClientUDP {
	public static void main(String args[]) throws IOException {

		if (args.length != 3) {
			// 验证参数个数
			throw new IllegalArgumentException("Parameter(s): <Destination>" + " <Port> <Candidate#>");
		}

		// IP 目的地址
		InetAddress destAddr = InetAddress.getByName(args[0]);
		// 目的地址端口
		int destPort = Integer.parseInt(args[1]);
		// 候选人ID [0,1000]
		int candidate = Integer.parseInt(args[2]);
		// UDP服务类
		DatagramSocket sock = new DatagramSocket();
		sock.connect(destAddr, destPort);

		// 创建一个投票请求
		VoteMsg vote = new VoteMsg(false, false, candidate, 0);

		//
		VoteMsgCoder coder = new VoteMsgTextCoder();

		// 序列化操作-构建消息 发送请求
		byte[] encodedVote = coder.toWire(vote);
		System.out.println("Sending Text-Encoded Request (" + encodedVote.length + " bytes): ");
		System.out.println(vote);
		// 创建UDP消息
		DatagramPacket message = new DatagramPacket(encodedVote, encodedVote.length);
		sock.send(message);

		// 序列化操作-构建消息 发送请求
		message = new DatagramPacket(new byte[VoteMsgTextCoder.MAX_WIRE_LENGTH], VoteMsgTextCoder.MAX_WIRE_LENGTH);
		// 创建UDP消息
		sock.receive(message);
		encodedVote = Arrays.copyOfRange(message.getData(), 0, message.getLength());

		System.out.println("Received Text-Encoded Response (" + encodedVote.length + " bytes): ");
		vote = coder.fromWire(encodedVote);
		System.out.println(vote);
	}
}