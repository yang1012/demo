package com.yang.tcp_ip.vote.framer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @ClassName: DelimFramer
 * @package com.yang.tcp_ip
 * @Description: frameMsg()方法用来添加成帧信息并将指定消息输出到指定流， nextMsg()方法则扫描指定的流，从中抽取出下一条消息
 * @author 杨森
 * @date 2015年12月10日 上午9:46:04
 * @version V1.0
 */
public interface Framer {

	// 添加成帧信息并将指定消息输出到指定流
	void frameMsg(byte[] message, OutputStream out) throws IOException;

	// nextMsg()方法则扫描指定的流，从中抽取出下一条消息。
	byte[] nextMsg() throws IOException;
}