package com.hong.processors.impl;

import org.springframework.stereotype.Service;

import com.hong.commons.domain.event.EventInMessage;
import com.hong.commons.processors.EventMessageProcessor;

@Service("unsubscribeMessageProcessor")
public class UnsubscribeEventMessageProcessor implements EventMessageProcessor {

	@Override
	public void onMessage(EventInMessage msg) {
		// 把用户的数据删除，或者标记为已经取消关注即可
	}
}
