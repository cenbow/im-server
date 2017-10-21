package com.maleyuan.im.module.broker.service;

import com.maleyuan.im.module.broker.session.SessionManager;
import com.maleyuan.im.module.core.ClientSession;
import com.maleyuan.im.module.core.service.BrokerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import java.io.UnsupportedEncodingException;

/**
 * Created by wwj on 16.1.26.
 */
public class BrokerServiceImpl implements BrokerService {
	private SessionManager sessionManager;

	@Override
	public void push(long cid, int sid, String msg) {
		ClientSession session = sessionManager.getSession(sid);
		ByteBuf mesg = null;
		try {
			mesg = Unpooled.copiedBuffer(msg.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		MqttPublishVariableHeader varHeader = new MqttPublishVariableHeader("chat", 20);
		MqttFixedHeader fixheader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false,
				mesg.readableBytes());
		MqttPublishMessage pubMsg = new MqttPublishMessage(fixheader, varHeader, mesg);
		session.getChannel().writeAndFlush(pubMsg);
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
