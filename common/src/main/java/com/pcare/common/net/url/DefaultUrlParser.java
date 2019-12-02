package com.pcare.common.net.url;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description: 构建用于替换BaseURL的实体类
 */
public class DefaultUrlParser implements UrlParser{
    private Cache<String, String> mCache;
    @Override
    public void init(RetrofitUrlManager retrofitUrlManager) {
        //根据LRU规则，初始化一个用于管理诸多BaseURL的列表
        this.mCache = new CacheLRU<>(20);
    }

    /**
     * 主要操作，用来替换BaseURL
     * @param domainUrl 用于替换的 URL 地址
     * @param oldUrl  旧 URL 地址
     * @return
     */
    @Override
    public HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl oldUrl) {
        if(null == domainUrl)
            return oldUrl;
        HttpUrl.Builder builder = oldUrl.newBuilder();
        if(TextUtils.isEmpty(mCache.getValue(getKey(domainUrl,oldUrl)))){
            for(int i = 0;i<oldUrl.pathSize();i++){
                builder.removePathSegment(0);
            }
            List<String> newPathSegments = new ArrayList<>();
            newPathSegments.addAll(domainUrl.encodedPathSegments());
            newPathSegments.addAll(oldUrl.encodedPathSegments());

            for(String PathSegment : newPathSegments) {
                builder.addEncodedPathSegment(PathSegment);
            }
        }else {
            builder.encodedPath(mCache.getValue(getKey(domainUrl,oldUrl)));
        }
        HttpUrl httpUrl = builder
                .scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();
        if(TextUtils.isEmpty(mCache.getValue(getKey(domainUrl,oldUrl))))
            mCache.put(getKey(domainUrl,oldUrl),httpUrl.encodedPath());

        return httpUrl;

    }

    private String getKey(HttpUrl domainUrl, HttpUrl oldUrl){
        return domainUrl.encodedPath() + oldUrl.encodedPath();
    }
}
