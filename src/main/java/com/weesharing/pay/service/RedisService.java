package com.weesharing.pay.service;

/**
 * redis操作Service,
 * Desc: 对象和数组都以json形式进行存储
 * Created by zp on 2018/8/7.
 */
public interface RedisService {
    /**
     * 存储数据
     */
    void set(String key, String value);
    
    void set(String key, String value, long expire);

    /**
     * 获取数据
     */
    String get(String key);

    /**
     * 设置超期时间
     */
    boolean expire(String key, long expire);

    /**
     * 删除数据
     */
    void remove(String key);

    /**
     * 自增操作
     * @param delta 自增步长
     */
    Long increment(String key, long delta);

    /**
     * 自减操作
     * @param key
     * @param delta
     * @return
     */
	Long decrement(String key, long delta);
	
}
