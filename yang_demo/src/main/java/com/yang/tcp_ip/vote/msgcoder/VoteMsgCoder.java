package com.yang.tcp_ip.vote.msgcoder;

import java.io.IOException;

import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteMsgCoder
 * @package com.yang.tcp_ip.vote
 * @Description: 对投票消息进行序列化和反序列化的方法。（构建和解析协议消息）通读序列化和反序列化的具体操作
 *               ，理解基于简单实现的构建消息和解析消息 的构建和解析的概念
 * @author 杨森
 * @date 2015年12月10日 下午12:48:49
 * @version V1.0
 */
public interface VoteMsgCoder {
	/**
	 * @Title: toWire
	 * @Description: 根据一个特定的协议，将投票消息转换成一个字节序列（序列化-构建消息）
	 * @param msg
	 * @return
	 * @throws IOException
	 * @author 杨森
	 * @date 2015年12月10日 下午12:49:13
	 */
	byte[] toWire(VoteMsg msg) throws IOException;

	/**
	 * @Title: fromWire
	 * @Description: 根据相同的协议，对给定的字节序列进行解析，并根据信息的内容构造出消息类的一个实例（反序列化-解析消息）
	 * @param input
	 * @return
	 * @throws IOException
	 * @author 杨森
	 * @date 2015年12月10日 下午12:49:31
	 */
	VoteMsg fromWire(byte[] input) throws IOException;
}
