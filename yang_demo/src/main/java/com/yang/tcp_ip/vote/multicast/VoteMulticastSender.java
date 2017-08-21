package com.yang.tcp_ip.vote.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder;
import com.yang.tcp_ip.vote.msgcoder.VoteMsgTextCoder;
import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteMulticastSender
 * @package com.yang.tcp_ip.vote.multicast
 * @Description: 
 *               IPv4中的多播地址属于IPv4的D类地址，范围是224.0.0.0到239.255.255.255。IPv6中的多播地址是任何由FF开头的地址
 *               。Java中多播应用程序主要通过MulticastSocke实例进行通信，它是DatagramSocket的一个子类。
 *               重点需要理解的是，一个MulticastSocket实例实际上就是一个UDP套接字（DatagramSocket）。
 *               --投票信息的多播发送者
 * @author 杨森
 * @date 2015年12月22日 上午9:57:29
 * @version V1.0
 */
public class VoteMulticastSender {

	/** 候选人ID */
	public static final int CANDIDATEID = 475;

	public static void main(String args[]) throws IOException {

		if ((args.length < 2) || (args.length > 3)) {
			// 验证参数合法性
			throw new IllegalArgumentException("Parameter(s): []");
		}

		// 目的地址 InetAddress 类具有一个缓存，用于存储成功及不成功的主机名解析 默认情况下，当为了防止 DNS
		// 哄骗攻击安装了安全管理器时，正主机名解析的结果会永远缓存。当未安装安全管理器时，默认行为将缓存一段有限（与实现相关）时间的条目。不成功主机名解析的结果缓存非常短的时间（10
		// 秒）以提高性能。 InetAddress.getByName会调用本地DNS
		InetAddress destAddr = InetAddress.getByName(args[0]);
		if (!destAddr.isMulticastAddress()) {
			// 验证是否是多播地址
			throw new IllegalArgumentException("Not a multicast address");
		}

		// 目的地址端口
		int destPort = Integer.parseInt(args[1]);
		// 超时时间
		int TTL = (args.length == 3) ? Integer.parseInt(args[2]) : 1;

		MulticastSocket sock = new MulticastSocket();
		// 为多播数据报文设置了初始的TTL值（生命周期， Time To
		// Live）。每个IP数据报文中都包含了一个TTL，它被初始化为某个默认值，并在每个路由器转发该报文时递减（通常是减1）。
		// 当TTL值减为0时，就丢弃该数据报文。通过设置TTL的初始值，我们可以限制数据包从发送者开始所能传递到的最远距离
		sock.setTimeToLive(TTL);

		VoteMsgCoder coder = new VoteMsgTextCoder();

		VoteMsg vote = new VoteMsg(true, true, CANDIDATEID, 1000001L);

		// 创建并发送一个DatagramPacket数据包
		byte[] msg = coder.toWire(vote);
		DatagramPacket message = new DatagramPacket(msg, msg.length, destAddr, destPort);
		System.out.println("Sending Text-Encoded Request (" + msg.length + " bytes): ");
		System.out.println(vote);
		sock.send(message);

		sock.close();
	}

}