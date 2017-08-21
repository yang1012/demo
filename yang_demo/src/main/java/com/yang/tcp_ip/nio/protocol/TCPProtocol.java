package com.yang.tcp_ip.nio.protocol;

/** 
 * @ClassName: TCPProtocol 
 * @package com.yang.tcp_ip.nio.protocol
 * @Description: 定义了通用TCPSelectorServer类与特定协议之间
 * @author Mr.yang
 * @date 2015年12月30日 下午9:31:34 
 * @version V1.0
 */
import java.nio.channels.SelectionKey;
import java.io.IOException;

public interface TCPProtocol {
	void handleAccept(SelectionKey key) throws IOException;

	void handleRead(SelectionKey key) throws IOException;

	void handleWrite(SelectionKey key) throws IOException;
}
