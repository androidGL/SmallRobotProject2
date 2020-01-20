package com.pcare.common.net;

import android.util.Log;

import com.pcare.common.net.url.RetrofitUrlManager;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.Protocol.HTTP_1_1;

/**
 * @Author: gl
 * @CreateDate: 2019/10/30
 * @Description:
 */
public class RetrofitHelper {
    private static volatile RetrofitHelper instance;
    private final Retrofit retrofit;
    private static final int READ_TIMEOUT = 60;//读取超时时间（秒）
    private static final int CONN_TIMEOUT = 60;//连接超时时间（秒）

    /**
     * 在生成OKHTTP的builder时，RetrofitUrlManager.getInstance()得到的是用于修改BaseURL的类的实例，with返回的是OkHttpClient.Builder
     * 如果不需要修改BaseURL，可以去掉RetrofitUrlManager.getInstance().with()，直接new OkHttpClient().newBuilder()即可
     */
    private RetrofitHelper() {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Api.BASEURL)
                .client(RetrofitUrlManager.getInstance()
                        .with(new OkHttpClient().newBuilder())
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(new simulateResponse())
                        .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(100, TimeUnit.SECONDS)
                        .build())
                .build();
    }

    /**
     * 单例模式
     * @return
     */
    public static RetrofitHelper getInstance() {
        if (instance == null) {
            synchronized (RetrofitHelper.class) {
                if (instance == null) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * 打印日志的拦截器
     */
    private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            try {
                message = message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                showLog("retrofitLogging: "+URLDecoder.decode(message, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    });

    /**
     * 用于模拟API响应数据的拦截器
     * 应用场景：后台还没写好接口，没法儿测？根本没后台，自己想做个展示类Demo。还有好多场景
     */
    private class simulateResponse implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            final HttpUrl url = request.url();//获取路径
            //判断是否需要拦截，如果URL是需要拦截的URL，则会根据关键字apiNname进行拦截
            String apiNname = null;
            if("POST".equals(request.method())) {
                if (request.body() instanceof FormBody) {
                    FormBody oldFormBody = (FormBody) request.body();
                    for (int i = 0; i < oldFormBody.size(); i++) {
                        if (oldFormBody.encodedName(i).equals("entity")) {
                            apiNname = NetTest.isExist(url,URLDecoder.decode(oldFormBody.encodedValue(i), "UTF-8"));
                            break;
                        }
                    }
                }
            }else {
                apiNname = NetTest.isExist(url,url.queryParameter("entity"));
            }



            if(null != apiNname){
                //返回需要拦截的请求要进行模拟的数据
                return new Response.Builder()
                        .code(200)
                        .message("模拟响应")
                        .body(ResponseBody.create(MediaType.parse("UTF-8"),apiNname))
                        .request(request)
                        .protocol(HTTP_1_1)
                        .build();
            }
            return chain.proceed(request);
        }
    }
    private void showLog(String msg) {
        Log.i(getClass().getName(), msg);
    }
}
