package com.lion.redis.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SYSClientRedisDao extends BaseRedisDao {
	private final String SYS_ID_KEY_PROFIX = "sys_";
	
	@Autowired
	private RedisTemplate<Serializable, Serializable> redisTemplate;

	@Override
	protected RedisTemplate<Serializable, Serializable> getRedisTemplate() {
		return redisTemplate;
	}
	
	public void set(String sys,List<String> contractList){
		listPutAll(SYS_ID_KEY_PROFIX+sys, contractList);
	}
	
	public List<String> get(String key){
		return ListGetAll(SYS_ID_KEY_PROFIX+key);
	}
	
	public void del(String key){
		ListDelAll(SYS_ID_KEY_PROFIX+key);
	}
}
