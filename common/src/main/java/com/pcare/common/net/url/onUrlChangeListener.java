package com.pcare.common.net.url;

import okhttp3.HttpUrl;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description: 当BASE_URL发生变化时的监听
 */
public interface onUrlChangeListener {
    /**
     *
     * @param oldUrl
     * @param domainName
     */
    void onUrlChangeBefore(HttpUrl oldUrl, String domainName);

    void onUrlChanged(HttpUrl newUrl, HttpUrl oldUrl);

}
