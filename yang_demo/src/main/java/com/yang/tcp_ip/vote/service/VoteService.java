package com.yang.tcp_ip.vote.service;

import java.util.HashMap;
import java.util.Map;

import com.yang.tcp_ip.vote.vo.VoteMsg;

/**
 * @ClassName: VoteService
 * @package com.yang.tcp_ip.vote.service
 * @Description: 根据请求，返回封装的信息
 * @author 杨森
 * @date 2015年12月16日 下午12:52:35
 * @version V1.0
 */
public class VoteService {
	// Map of candidates to number of votes
	private Map<Integer, Long> results = new HashMap<Integer, Long>();

	/**
	 * @Title: handleRequest
	 * @Description: 根据请求，返回封装的信息
	 * @param msg
	 * @return
	 * @author 杨森
	 * @date 2015年12月14日 下午3:36:47
	 */
	public VoteMsg handleRequest(VoteMsg msg) {
		if (!msg.isResponse()) {
			// 响应信息，直接返回
			return msg;
		}
		// 设置为响应信息
		msg.setResponse(true);
		// 候选人ID
		int candidate = msg.getCandidateID();
		// 候选人投票总数
		Long count = results.get(candidate);
		if (count == null) {
			// 不存在情况下，设置默认候选人ID = 0;
			count = 0L;
		}
		if (!msg.isInquiry()) {
			// 投票请求，投票总数+1
			results.put(candidate, ++count);
		}

		msg.setVoteCount(count);

		return msg;
	}
}