package com.yang.tcp_ip.vote.platform.tcp;

import java.io.OutputStream;
import java.net.Socket;

import com.yang.tcp_ip.vote.framer.Framer;
import com.yang.tcp_ip.vote.framer.LengthFramer;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgBinCoder;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteClientTCP
 * @package com.yang.tcp_ip.vote.platform.tcp
 * @Description: TCP投票客户端
 * @author 杨森
 * @date 2015年12月14日 下午3:39:26
 * @version V1.0
 */
public class VoteClientTCP {
	public static final int CANDIDATEID = 888;

	public static void main(String args[]) throws Exception {
		if (args.length != 2) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
		}
		// 目的地址
		String destAddr = args[0];
		// 目的地址端口
		int destPort = Integer.parseInt(args[1]);
		// TCP客户端套接字
		Socket sock = new Socket(destAddr, destPort);
		OutputStream out = sock.getOutputStream();

		// 基于二进制编码策略 对投票消息进行序列化和反序列化的方法。（构建和解析协议消息）
		VoteMsgCoder coder = new VoteMsgBinCoder();
		// 基于文本长度的成帧信息
		Framer framer = new LengthFramer(sock.getInputStream());

		// 创建一个查询的请求
		VoteMsg msg = new VoteMsg(false, true, CANDIDATEID, 0);

		// 对投票信息进行序列化操作
		byte[] encodedMsg = coder.toWire(msg);

		// 发送查询请求
		System.out.println("Sending Inquiry (" + encodedMsg.length + " bytes): ");
		System.out.println(msg);
		// 添加成帧信息并将指定消息输出到指定流
		framer.frameMsg(encodedMsg, out);

		// 现在发送一个投票请求
		msg.setInquiry(false);
		// 对投票信息进行序列化操作
		encodedMsg = coder.toWire(msg);
		System.out.println("Sending Vote (" + encodedMsg.length + " bytes): ");
		// 添加成帧信息并将指定消息输出到指定流
		framer.frameMsg(encodedMsg, out);

		// 接收查询请求
		// 扫描指定的流，从中抽取出下一条消息。
		encodedMsg = framer.nextMsg();
		// 反序列化
		msg = coder.fromWire(encodedMsg);
		System.out.println("Received Response (" + encodedMsg.length + " bytes): ");
		System.out.println(msg);

		// 接收投票请求
		// 对投票信息进行反序列化操作，//扫描指定的流，从中抽取出下一条消息。
		msg = coder.fromWire(framer.nextMsg());
		System.out.println("Received Response (" + encodedMsg.length + " bytes): ");
		System.out.println(msg);

		sock.close();
	}
}