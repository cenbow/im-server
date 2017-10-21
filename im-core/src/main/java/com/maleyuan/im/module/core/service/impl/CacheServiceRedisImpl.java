package com.maleyuan.im.module.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.maleyuan.im.module.core.service.CacheService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wwj on 16.1.26.
 */
public class CacheServiceRedisImpl implements CacheService {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public String get(String key) {
		final String newKey = key;
		String value = stringRedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				String obj = null;
				if (connection.exists(newKey.getBytes())) {
					byte[] values = connection.get(newKey.getBytes());
					if (values != null && values.length > 0) {
						obj = new String(values);
					}
				}
				return obj;
			}
		});
		return value;
	}

	@Override
	public <T> T get(String key, Class<T> type) {
		String result = get(key);
		if (result == null) {
			return null;
		}
		return (T) JSON.parseObject(result, type);
	}

	@Override
	public boolean set(String key, String value, int expire) {
		try {
			final String newKey = key;
			final String newValue = value;
			final int newexpire = expire;
			boolean success = stringRedisTemplate.execute(new RedisCallback<Boolean>() {
				@Override
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bs = newValue.getBytes();
					if (connection.exists(newKey.getBytes())) {
						return false;
					}
					connection.setEx(newKey.getBytes(), newexpire, bs);
					return true;
				}
			});
			return success;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}

	}

	@Override
	public <T> String set(String key, T value) {
		return set(key, JSON.toJSON(value).toString());
	}

	@Override
	public Long del(String key) {
		if (StringUtils.isEmpty(key)) {
			logger.error("key不允许为null或空串!");
			throw new RuntimeException("key不允许为null或空串!");
		}
		stringRedisTemplate.execute(new RedisCallback<Void>() {
			@Override
			public Void doInRedis(RedisConnection connection) throws DataAccessException {
				if (connection.exists(key.getBytes())) {
					connection.del(key.getBytes());
				}
				return null;
			}
		});
		return null;
	}

	@Override
	public String hget(String key, String field) {
		HashOperations<String, String, String> bashOperations = stringRedisTemplate.opsForHash();
		return bashOperations.get(key, field);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T hget(String key, String field, Class<?> type) {
		return (T) JSON.parseObject(hget(key, field), type);
	}

	@Override
	public Long hset(String key, String field, String value) {
		HashOperations<String, String, String> bashOperations = stringRedisTemplate.opsForHash();
		Map<String, String> hashMap = new HashMap<String, String>();
		hashMap.put(key, field);
		bashOperations.putAll(key, hashMap );
		return null;
	}

	@Override
	public <T> Long hset(String key, String field, T value) {
		return hset(key, field, JSON.toJSON(value));
	}

	@Override
	public Boolean exists(String key) {
		if (StringUtils.isEmpty(key)) {
			logger.error("key不允许为null或空串!");
			throw new RuntimeException("key不允许为null或空串!");
		}
		boolean ifExist = stringRedisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
		return ifExist;
	}

	@Override
	public Boolean hexists(String key, String field) {
		HashOperations<String, String, String> bashOperations = stringRedisTemplate.opsForHash();
		return bashOperations.hasKey(key, field);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		HashOperations<String, String, String> bashOperations = stringRedisTemplate.opsForHash();
		List<String> list = Arrays.asList(fields);
		return bashOperations.multiGet(key, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> hmget(String key, Class<?> type, String... fields) {
		List<String> reslut = hmget(key, fields);
		List<T> list = new ArrayList<T>(reslut.size());
		for (String jsonStr : reslut) {
			list.add((T) JSON.parseObject(jsonStr, type));
		}
		return list;
	}

	@Override
	public Long hdel(String key, String field) {
		HashOperations<String, String, String> bashOperations = stringRedisTemplate.opsForHash();
		bashOperations.delete(key, field);
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		if (StringUtils.isEmpty(key)) {
			logger.error("key不允许为null或空串!");
			throw new RuntimeException("key不允许为null或空串!");
		}
		stringRedisTemplate.execute(new RedisCallback<Void>() {
			@Override
			public Void doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                byte[] name = serializer.serialize(key);
				if (connection.exists(key.getBytes())) {
					connection.expire(name, seconds);
				}
				return null;
			}
		});
		return null;
	}
}
