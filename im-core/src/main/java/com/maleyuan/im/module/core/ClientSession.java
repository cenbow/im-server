package com.maleyuan.im.module.core;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * Created by wwj on 16.1.26.
 */
public class ClientSession implements Serializable, Comparable<ClientSession> {
	private static final long serialVersionUID = 6343002778924717776L;

	public ClientSession() {
		super();
	}
	private Integer sid;
	private long userId;
	private Channel channel;

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public int compareTo(ClientSession o) {
		if (o == null || o.getSid() == null)
			return 0;
		return this.sid - o.getSid();
	}
}
