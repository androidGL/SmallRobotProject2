package com.pcare.common.net.url;

import okhttp3.HttpUrl;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description:
 */
public interface UrlParser {
    void init(RetrofitUrlManager retrofitUrlManager);

    /**
     *
     * @param domainUrl 用于替换的 URL 地址
     * @param oldUrl  旧 URL 地址
     * @return
     */
    HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl oldUrl);
}
