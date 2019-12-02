package com.pcare.common.net.url;

import android.text.TextUtils;
import android.util.Log;

import com.pcare.common.net.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description: 修改BaseURL的工具类
 */
public class RetrofitUrlManager {
    private static final String TAG = "RetrofitUrlManager";
    private static final String GLOBAL_DOMAIN_NAME = "URL_VALUE_DEFAULT";

    private final Map<String, HttpUrl> mDomainNameHub = new HashMap<>();
    private final Interceptor mInterceptor;
    private final List<onUrlChangeListener> mListeners = new ArrayList<>();
    private UrlParser mUrlParser;

    private RetrofitUrlManager() {
        UrlParser urlParser = new DefaultUrlParser();
        urlParser.init(this);
        setUrlParser(urlParser);
        this.mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(processRequest(chain.request()));
            }
        };
    }

    //单例模式
    private static class RetrofitUrlManagerHolder {
        private static final RetrofitUrlManager INSTANCE = new RetrofitUrlManager();
    }

    public static final RetrofitUrlManager getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    //添加拦截器
    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        checkNotNull(builder, "builder cannot be null");
        return builder.addInterceptor(mInterceptor);
    }

    //对request进行修改
    public Request processRequest(Request request) {
        if (request == null) return request;

        Request.Builder newBuilder = request.newBuilder();
        //检测header中是否存在需要更换BaseUrl的key值，如果存在则返回对应的value值
        String domainName = obtainDomainNameFromHeaders(request);

        HttpUrl httpUrl;

        Object[] listeners = listenersToArray();

        // 如果有 header,获取 header 中 domainName 所映射的 url,若没有,则检查全局的 BaseUrl,未找到则为null
        if (!TextUtils.isEmpty(domainName)) {
            notifyListener(request, domainName, listeners);
            httpUrl = fetchDomain(domainName);
            newBuilder.removeHeader(Api.URL_KEY);
        } else {
            notifyListener(request, GLOBAL_DOMAIN_NAME, listeners);
            httpUrl = getGlobalDomain();
        }

        if (null != httpUrl) {
            HttpUrl newUrl = mUrlParser.parseUrl(httpUrl, request.url());
                Log.d(RetrofitUrlManager.TAG, "The new url is { " + newUrl.toString() + " }, old url is { " + request.url().toString() + " }");

            if (listeners != null) {
                for (int i = 0; i < listeners.length; i++) {
                    ((onUrlChangeListener) listeners[i]).onUrlChanged(newUrl, request.url()); // 通知监听器此 Url 的 BaseUrl 已被切换
                }
            }

            return newBuilder
                    .url(newUrl)
                    .build();
        }

        return newBuilder.build();

    }

    /**
     * 通知所有监听器的  onUrlChangeListener#onUrlChangeBefore(HttpUrl, String)方法
     * @param request    {@link Request}
     * @param domainName 域名的别名
     * @param listeners  监听器列表
     */
    private void notifyListener(Request request, String domainName, Object[] listeners) {
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ((onUrlChangeListener) listeners[i]).onUrlChangeBefore(request.url(), domainName);
            }
        }
    }

    /**
     * 全局动态替换 BaseUrl, 优先级: Header中配置的 BaseUrl > 全局配置的 BaseUrl
     * 除了作为备用的 BaseUrl, 当您项目中只有一个 BaseUrl, 但需要动态切换
     * 这种方式不用在每个接口方法上加入 Header, 就能实现动态切换 BaseUrl
     *
     * @param globalDomain 全局 BaseUrl
     */
    public void setGlobalDomain(String globalDomain) {
        checkNotNull(globalDomain, "globalDomain cannot be null");
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(GLOBAL_DOMAIN_NAME, checkUrl(globalDomain));
        }
    }
    private HttpUrl checkUrl(String url) {
        HttpUrl parseUrl = HttpUrl.parse(url);
        if (null == parseUrl) {
            throw new RuntimeException("You've configured an invalid url : "+url);
        } else {
            return parseUrl;
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    public synchronized HttpUrl getGlobalDomain() {
        return mDomainNameHub.get(GLOBAL_DOMAIN_NAME);
    }

    /**
     * 移除全局 BaseUrl
     */
    public void removeGlobalDomain() {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(GLOBAL_DOMAIN_NAME);
        }
    }

    /**
     * 存放 Domain(BaseUrl) 的映射关系
     *
     * @param domainName
     * @param domainUrl
     */
    public void putDomain(String domainName, String domainUrl) {
        checkNotNull(domainName, "domainName cannot be null");
        checkNotNull(domainUrl, "domainUrl cannot be null");
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(domainName, checkUrl(domainUrl));
        }
    }

    /**
     * 取出对应 {@code domainName} 的 Url(BaseUrl)
     *
     * @param domainName
     * @return
     */
    public synchronized HttpUrl fetchDomain(String domainName) {
        checkNotNull(domainName, "domainName cannot be null");
        return mDomainNameHub.get(domainName);
    }

    /**
     * 移除某个 {@code domainName}
     *
     * @param domainName {@code domainName}
     */
    public void removeDomain(String domainName) {
        checkNotNull(domainName, "domainName cannot be null");
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(domainName);
        }
    }

    /**
     * 清理所有 Domain(BaseUrl)
     */
    public void clearAllDomain() {
        mDomainNameHub.clear();
    }

    /**
     * 存放 Domain(BaseUrl) 的容器中是否存在这个 {@code domainName}
     *
     * @param domainName {@code domainName}
     * @return {@code true} 为存在, {@code false} 为不存在
     */
    public synchronized boolean haveDomain(String domainName) {
        return mDomainNameHub.containsKey(domainName);
    }

    /**
     * 存放 Domain(BaseUrl) 的容器, 当前的大小
     *
     * @return 容量大小
     */
    public synchronized int domainSize() {
        return mDomainNameHub.size();
    }

    /**
     * 可自行实现 {@link UrlParser} 动态切换 Url 解析策略
     *
     * @param parser {@link UrlParser}
     */
    public void setUrlParser(UrlParser parser) {
        checkNotNull(parser, "parser cannot be null");
        this.mUrlParser = parser;
    }

    /**
     * 注册监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    public void registerUrlChangeListener(onUrlChangeListener listener) {
        checkNotNull(listener, "listener cannot be null");
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    /**
     * 注销监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    public void unregisterUrlChangeListener(onUrlChangeListener listener) {
        checkNotNull(listener, "listener cannot be null");
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private Object[] listenersToArray() {
        Object[] listeners = null;
        synchronized (mListeners) {
            if (mListeners.size() > 0) {
                listeners = mListeners.toArray();
            }
        }
        return listeners;
    }

   //检测header中是否存在需要更换BaseUrl的key值
    private String obtainDomainNameFromHeaders(Request request) {
        //查询header中是否包含Api.URL_KEY，如果包含则表示这个request需要替换BaseUrl
        List<String> headers = request.headers(Api.URL_KEY);
        if (headers == null || headers.size() == 0)
            return null;
        //header中应该只有一个Api.URL_KEY
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Domain-Name in the headers");
        //返回header中对应Api.URL_KEY的value值
        return request.header(Api.URL_KEY);
    }
    private <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
