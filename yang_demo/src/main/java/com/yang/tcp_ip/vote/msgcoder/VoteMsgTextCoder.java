package com.yang.tcp_ip.vote.msgcoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteMsgTextCoder
 * @package com.yang.tcp_ip.vote
 * @Description: 基于文本的VoteMsg编码方法 （构建和解析协议消息）通读序列化和反序列化的具体操作 ，理解基于简单实现的构建消息和解析消息
 *               的构建和解析的概念
 * @author 杨森
 * @date 2015年12月10日 下午12:56:35
 * @version V1.0
 */
public class VoteMsgTextCoder implements VoteMsgCoder {
	/*
	 * Wire Format "VOTEPROTO" <"v"|"i"> [<RESPFLAG>] <CANDIDATE> [<VOTECNT>]
	 * Charset is fixed by the wire format.
	 */

	// Manifest constants for encoding
	public static final String MAGIC = "Voting";
	/** 投票消息 */
	public static final String VOTESTR = "v";
	/** 查询消息 */
	public static final String INQSTR = "i";
	/** 消息的状态，即是否为服务器的响应b */
	public static final String RESPONSESTR = "R";
	/** 编码格式 */
	public static final String CHARSETNAME = "US-ASCII";
	public static final String DELIMSTR = " ";
	public static final int MAX_WIRE_LENGTH = 2000;
	private Scanner sc;

	/**
	 * @Title: toWire
	 * @Description: 根据一个特定的协议，将投票消息转换成一个字节序列（序列化-构建消息）
	 * @param msg
	 * @return
	 * @throws IOException
	 * @author : 杨森
	 * @date 2015年12月10日 下午1:00:43
	 * @see com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder#toWire(com.yang.tcp_ip.vote.vo.VoteMsg)
	 */
	public byte[] toWire(VoteMsg msg) throws IOException {
		// 用于接收者快速将投票协议的消息和网络中随机到来的垃圾消息区分开
		String msgString = MAGIC + DELIMSTR + (msg.isInquiry() ? INQSTR : VOTESTR) + DELIMSTR + (msg.isResponse() ? RESPONSESTR + DELIMSTR : "")
				+ Integer.toString(msg.getCandidateID()) + DELIMSTR + Long.toString(msg.getVoteCount());
		byte data[] = msgString.getBytes(CHARSETNAME);
		return data;
	}

	/**
	 * @Title: fromWire
	 * @Description: 根据相同的协议，对给定的字节序列进行解析，并根据信息的内容构造出消息类的一个实例（反序列化-解析消息）
	 * @param message
	 * @return
	 * @throws IOException
	 * @author : 杨森
	 * @date 2015年12月10日 下午1:01:06
	 * @see com.yang.tcp_ip.vote.msgcoder.VoteMsgCoder#fromWire(byte[])
	 */
	public VoteMsg fromWire(byte[] message) throws IOException {

		ByteArrayInputStream msgStream = new ByteArrayInputStream(message);
		boolean isInquiry;
		boolean isResponse;
		int candidateID;
		long voteCount;
		String token;
		// Scanner一个可以使用正则表达式来解析基本类型和字符串的简单文本扫描器,使用分隔符模式将其输入分解为标记，默认情况下该分隔符模式与空白匹配。然后可以使用不同的
		// next 方法将得到的标记转换为不同类型的值。

		sc = new Scanner(new InputStreamReader(msgStream, CHARSETNAME));

		try {
			token = sc.next();
			if (!token.equals(MAGIC)) {
				throw new IOException("Bad magic string: " + token);
			}
			token = sc.next();
			if (token.equals(VOTESTR)) {
				isInquiry = false;
			} else if (!token.equals(INQSTR)) {
				throw new IOException("Bad vote/inq indicator: " + token);
			} else {
				isInquiry = true;
			}

			token = sc.next();
			if (token.equals(RESPONSESTR)) {
				isResponse = true;
				token = sc.next();
			} else {
				isResponse = false;
			}
			// Current token is candidateID
			// Note: isResponse now valid
			candidateID = Integer.parseInt(token);
			if (isResponse) {
				token = sc.next();
				voteCount = Long.parseLong(token);
			} else {
				voteCount = 0;
			}
		} catch (IOException ioe) {
			throw new IOException("Parse error...");
		}

		return new VoteMsg(isResponse, isInquiry, candidateID, voteCount);
	}
}