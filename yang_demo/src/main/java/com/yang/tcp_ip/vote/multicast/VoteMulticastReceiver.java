package com.yang.tcp_ip.vote.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import com.yang.tcp_ip.vote.msgcoder.VoteMsgTextCoder;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteMulticastReceiver
 * @package com.yang.tcp_ip.vote.multicast
 * @Description: 与广播不同，网络多播只将消息副本发送给指定的一组接收者。这组接收者叫做多播组（multicast
 *               group），通过共享的多播（
 *               组）地址确定。接收者需要一种机制来通知网络它对发送到某一特定地址的消息感兴趣，以使网络将数据包转发给它
 *               。这种通知机制叫做加入一组（joining a
 *               group），可以由MulticastSocket类的joinGroup()方法实现
 *               。我们的多播接收者加入了一个特定的组，接收并打印该组的一条多播消息，然后退出 。
 *               一个多播消息接收者必须使用MulticastSocket来接收数据，因为它需要用到MulticastSocket加入组的功能。
 *               -- 多播接收者。
 * @author 杨森
 * @date 2015年12月22日 下午12:19:57
 * @version V1.0
 */
public class VoteMulticastReceiver {
	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): ");
		}

		// 多播地址
		InetAddress address = InetAddress.getByName(args[0]);
		if (!address.isMulticastAddress()) {
			// 验证是否是多播地址
			throw new IllegalArgumentException("Not a multicast address");
		}

		// 多播地址端口
		int port = Integer.parseInt(args[1]);

		// 多播套接字（UDP套接字）
		MulticastSocket sock = new MulticastSocket(port);
		// 加入一个多播租
		sock.joinGroup(address);

		VoteMsgTextCoder coder = new VoteMsgTextCoder();

		// 接收多播消息
		DatagramPacket packet = new DatagramPacket(new byte[VoteMsgTextCoder.MAX_WIRE_LENGTH], VoteMsgTextCoder.MAX_WIRE_LENGTH);
		sock.receive(packet);

		// 反序列化消息
		VoteMsg vote = coder.fromWire(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));

		System.out.println("Received Text-Encoded Request (" + packet.getLength() + " bytes): ");
		System.out.println(vote);

		sock.close();
	}
}