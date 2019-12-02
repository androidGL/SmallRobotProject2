package com.pcare.common.net.url;

import java.util.Set;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description: 缓存
 */
public interface Cache<K, V> {

    //返回当前缓存已占用的总size
    int size();
    //返回当前缓存能允许的最大的size
    int getMaxSize();
    //返回缓存中对应的key的value
    V getValue(K key);
    //存储一个信息
    V put(K key, V value);
    //移除key
    V remove(K key);
    //判断是否包含key
    boolean containsKey(K key);
    //返回所有的key
    Set<K> keySet();
    //清除
    void clear();
}
