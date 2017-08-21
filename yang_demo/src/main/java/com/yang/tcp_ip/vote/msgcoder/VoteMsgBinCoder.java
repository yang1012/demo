package com.yang.tcp_ip.vote.msgcoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteMsgTextCoder
 * @package com.yang.tcp_ip.vote
 * @Description: 基于二进制的VoteMsg编码方法（构建和解析协议消息） 通读序列化和反序列化的具体操作
 *               ，理解基于简单实现的构建消息和解析消息 的构建和解析的概念
 * @author 杨森
 * @date 2015年12月10日 下午12:56:35
 * @version V1.0
 */
public class VoteMsgBinCoder implements VoteMsgCoder {

	// manifest constants for encoding
	public static final int MIN_WIRE_LENGTH = 4;
	public static final int MAX_WIRE_LENGTH = 16;
	/** 十六进制 64512 二进制 1111 1100 0000 0000 */
	public static final int MAGIC = 0xfc00;

	/** 十六进制 64512 magic 掩码 1111 1100 0000 0000 */
	public static final int MAGIC_MASK = 0xfc00;
	public static final int MAGIC_SHIFT = 8;

	/** 十六进制 512 二进制 0000 0010 0000 0000 */
	public static final int RESPONSE_FLAG = 0x0200;

	/** 十六进制 256 二进制 0000 0001 0000 0000 */
	public static final int INQUIRE_FLAG = 0x0100;

	/**
	 * @Title: toWire
	 * @Description: 根据一个特定的协议，将投票消息转换成一个字节序列（序列化-构建消息）
	 * @param msg
	 * @return
	 * @throws IOException
	 * @author : 杨森
	 * @date 2015年12月16日 上午9:34:34
	 * @see com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder#toWire(com.yang.tcp_ip.vote.vo.VoteMsg)
	 */
	public byte[] toWire(VoteMsg msg) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteStream);

		// int 类型 的MAGIC 截位
		short magicAndFlags = (short) MAGIC;
		if (msg.isInquiry()) {
			// 查询请求 或操作 1111 1100 0000 0000 | 0000 0001 0000 0000 = 1111 1101
			// 0000 0000
			magicAndFlags |= INQUIRE_FLAG;
		}
		if (msg.isResponse()) {
			// 返回响应 或操作 1111 1100 0000 0000 | 0000 0010 0000 0000 = 1111 1110
			// 0000 0000
			magicAndFlags |= RESPONSE_FLAG;
		}
		// 输出 查询/响应的 标识
		out.writeShort(magicAndFlags);

		// 已知 候选人ID 取值范围 [0,1000]
		out.writeShort((short) msg.getCandidateID());
		if (msg.isResponse()) {
			// 响应 输出 投票总数
			out.writeLong(msg.getVoteCount());
		}
		// 刷新输出流
		out.flush();

		byte[] data = byteStream.toByteArray();
		// 返回序列化字节数组
		return data;
	}

	/**
	 * @Title: fromWire
	 * @Description: 根据相同的协议，对给定的字节序列进行解析，并根据信息的内容构造出消息类的一个实例（反序列化-解析消息）
	 * @param input
	 * @return
	 * @throws IOException
	 * @author : 杨森
	 * @date 2015年12月16日 上午9:34:50
	 * @see com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder#fromWire(byte[])
	 */
	public VoteMsg fromWire(byte[] input) throws IOException {

		if (input.length < MIN_WIRE_LENGTH) {
			// 最小的解析内容长度
			throw new IOException("Runt message");
		}
		ByteArrayInputStream bs = new ByteArrayInputStream(input);

		DataInputStream in = new DataInputStream(bs);

		int magic = in.readShort();
		if ((magic & MAGIC_MASK) != MAGIC) {
			// 序列化操作中(即在构建的消息中) 第一个short信息 为maigc和请求信息类型或操作的结果值 magicAndFlags |=
			// INQUIRE_FLAG ，magicAndFlags |= RESPONSE_FLAG;
			throw new IOException("Bad Magic #: " + ((magic & MAGIC_MASK) >> MAGIC_SHIFT));
		}

		// 响应标识 magic = 1111 1101 0000 0000 1111 1101 0000 0000 & 0000 0001 0000
		// 0000 = 0000 0001 0000 0000 = RESPONSE_FLAG
		boolean resp = ((magic & RESPONSE_FLAG) != 0);
		// magic = 1111 1110 0000 0000 1111 1110 0000 0000 & 0000 0010 0000 0000
		// = 0000 0010 0000 0000 = INQUIRE_FLAG
		boolean inq = ((magic & INQUIRE_FLAG) != 0);

		int candidateID = in.readShort();
		if (candidateID < 0 || candidateID > 1000
		// 不进行任何操作
		) {
			throw new IOException("Bad candidate ID: " + candidateID);
		}
		long count = 0;
		if (resp) {
			// 响应
			count = in.readLong();
			if (count < 0) {
				throw new IOException("Bad vote count: " + count);
			}
		}
		// Ignore any extra bytes
		return new VoteMsg(resp, inq, candidateID, count);
	}

	public static void main(String[] args) {
		int tmp = 0xfc00;
		System.out.println(tmp + ": " + Integer.toBinaryString(tmp));
	}
}