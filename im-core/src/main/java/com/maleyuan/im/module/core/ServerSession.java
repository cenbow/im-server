package com.maleyuan.im.module.core;

import java.io.Serializable;

/**
 * client session Created by wwj on 16.1.26.
 */
public class ServerSession implements Serializable, Comparable<ServerSession> {
	private static final long serialVersionUID = -7575880965546063273L;

	public ServerSession() {
		super();
	}
	private long cid;
	private Integer sid;
	private long userId;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {

		this.userId = userId;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	@Override
	public int compareTo(ServerSession serverSession) {
		if (serverSession == null || serverSession.getSid() == null)
			return 0;
		return this.sid - serverSession.getSid();
	}
}
