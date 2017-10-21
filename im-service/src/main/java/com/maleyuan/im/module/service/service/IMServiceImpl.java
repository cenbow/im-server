package com.maleyuan.im.module.service.service;

import com.maleyuan.im.module.core.ServerSession;
import com.maleyuan.im.module.core.service.BrokerService;
import com.maleyuan.im.module.core.service.CacheService;
import com.maleyuan.im.module.core.service.IMService;

/**
 * Created by wwj on 16.1.26.
 */
public class IMServiceImpl implements IMService {
	private CacheService cacheService;
	private BrokerService brokerService;

	private String getSessionKey(long userId) {
		return String.format("USERID:%d", userId);
	}

	@Override
	public void connect(long cid, int sid, long userId) {
		ServerSession session = new ServerSession();
		session.setCid(cid);
		session.setSid(sid);
		session.setUserId(userId);
		String key = getSessionKey(session.getUserId());
		cacheService.set(key, session);
		cacheService.expire(key, 3600);
	}

	@Override
	public void publish(long userId, String msg) {
		String key = getSessionKey(userId);
		ServerSession toSession = cacheService.get(key, ServerSession.class);
		brokerService.push(toSession.getCid(), toSession.getSid(), msg);
	}

	@Override
	public void disconnect(long userId) {
		String key = getSessionKey(userId);
		cacheService.del(key);
	}

	public CacheService getCacheService() {
		return cacheService;
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public BrokerService getBrokerService() {
		return brokerService;
	}

	public void setBrokerService(BrokerService brokerService) {
		this.brokerService = brokerService;
	}
}
