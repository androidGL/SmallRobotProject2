package com.pcare.common.net.url;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description: Least Recently Used 最近最少使用：缓存满了，会优先淘汰那些最不常访问的数据
 */
public class CacheLRU<K,V> implements Cache<K,V>  {
    //第一个参数为初始容量，默认16，扩展因子默认0.75f,assessOrder为false表示为插入排序，true为访问排序。因为是LRU，所以设为true
    private final LinkedHashMap<K,V> cache = new LinkedHashMap<>(16,0.75f,true);
    private final int initialMaxSize;
    private int maxSize;
    private int currentSize = 0;

    public CacheLRU(int size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }
    /**
     * 设置一个系数应用于当时构造函数中所传入的 size, 从而得到一个新的 {@link #maxSize}
     * 并会立即调用 {@link #evict} 开始清除满足条件的条目
     *
     * @param multiplier 系数
     */
    public synchronized void setSizeMultiplier(float multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        maxSize = Math.round(initialMaxSize * multiplier);
        evict();
    }

    /**
     * 当缓存中已占用的总 size 大于所能允许的最大 size ,会使用  {@link #trimToSize(int)} 开始清除满足条件的条目
     */
    private void evict() {
        trimToSize(maxSize);
    }
    /**
     * 当指定的 size 小于当前缓存已占用的总 size 时,会开始清除缓存中最近最少使用的条目
     * @param maxSize {@code size}
     */
    private synchronized void trimToSize(int maxSize) {
        Map.Entry<K,V> last;
        while (currentSize > maxSize){
            last = cache.entrySet().iterator().next();
            final V toRemove = last.getValue();
            currentSize -= getItemSize(toRemove);
            final K key = last.getKey();
            cache.remove(key);
            onItemEvicter(key,toRemove);
        }
    }

    //当缓存中有被驱逐的条目时,会回调此方法,默认空实现,子类可以重写这个方法
    protected void onItemEvicter(K key, V toRemove) {
    }

    //返回每个 {@code item} 所占用的 size,默认为1,这个 size 的单位必须和构造函数所传入的 size 一致
    protected int getItemSize(V toRemove) {
        return 1;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public V getValue(K key) {
        return cache.get(key);
    }

    @Override
    public V put(K key, V value) {
        final int itemSize = getItemSize(value);
        if(itemSize>=maxSize){
            onItemEvicter(key,value);
            return null;
        }
        //如果cache中存在这个key，则返回oldValue,如果是新加的，则返回null
        final V result = cache.put(key,value);
        if(null != value){
            currentSize += getItemSize(value);
        }
        if(null != result){
            currentSize -= getItemSize(result);
        }
        evict();
        return result;
    }

    @Override
    public synchronized V remove(K key) {
        final V value =  cache.remove(key);
        //如果value为null，表示这个key可能不存在
        if(null != value){
            currentSize -= getItemSize(value);
        }
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public Set<K> keySet() {
        return cache.keySet();
    }

    @Override
    public void clear() {
        trimToSize(0);
    }
}
