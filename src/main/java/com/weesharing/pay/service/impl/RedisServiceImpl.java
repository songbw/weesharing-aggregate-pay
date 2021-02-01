package com.weesharing.pay.service.impl;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.weesharing.pay.service.RedisService;

/**
 * redis操作Service的实现类
 * Created by macro on 2018/8/7.
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean expire(String key, long expire) {
        return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key,delta);
    }

    @Override
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key,delta);
    }

	@Override
	public void set(String key, String value, long expire) {
		stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);

	}

    @Override
    public boolean
    lock(String lockKey,String timeStamp,long seconds){
        if(StringUtils.isBlank(lockKey) || StringUtils.isBlank(timeStamp)){
            return false;
        }

        if(stringRedisTemplate.opsForValue().setIfAbsent(lockKey,timeStamp, Duration.ofSeconds(seconds))){
            return true;
        }

        ///下面操作，用于防止解锁操作异常导致的死锁
        String lockedValue = stringRedisTemplate.opsForValue().get(lockKey);
        if(StringUtils.isNotBlank(lockedValue) && Long.parseLong(lockedValue) < System.currentTimeMillis() - seconds*1000L){
            String preLock = stringRedisTemplate.opsForValue().getAndSet(lockKey,timeStamp);
            if(StringUtils.isNotBlank(preLock) && preLock.equals(lockedValue)){
                //如果有多个线程执行，由于getAndSet,只有第一个执行的可以获得锁
                return true;
            }
        }
        return false;
    }

    @Override
    public void
    unLock(String lockKey,String timeStamp){
        try{
            String lockedValue = stringRedisTemplate.opsForValue().get(lockKey);
            if(StringUtils.isNotEmpty(lockedValue) && lockedValue.equals(timeStamp)){
                stringRedisTemplate.opsForValue().getOperations().delete(lockKey);
            }
        }catch (Exception e){
            log.error("解锁异常 key="+lockKey);
        }
    }
}
