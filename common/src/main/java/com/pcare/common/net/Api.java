package com.pcare.common.net;


import com.pcare.common.entity.NetResponse;
import com.pcare.common.entity.UserEntity;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    //成员变量,默认修饰符 public static final
    //成员方法,默认修饰符 public abstract
    String BASEURL = "https://api.apiopen.top";//测试api

    //header中添加 URL_KEY，表示这个请求是需要替换BaseUrl的
    String URL_KEY = "URL_KEY";
    //header中的value值，用于区分需要替换的BaseUrl是哪一个
    String URL_VALUE_SECOND = "URL_VALUE_SECOND";

    //人脸识别的URL
    String URL_VALUE_FACE = "URL_VALUE_FACE";
    String FACEURL = "http://192.168.2.181:8000/fl";

    //问诊的URL
    String URL_VALUE_QUESTION = "URL_VALUE_QUESTION";
    String QUESTIONURL = "http://192.168.2.180:8080";
    //天益本地问诊的URL
    String URL_VALUE_ASK = "URL_VALUE_ASK";
    String ASKURL = "http://192.168.2.219:8088";

    //这儿添加Headers是为了修改BaseURL的，key用于识别是不是需要修改BaseURL，value用来识别需要修改哪个BaseURL
    //使用时只需要在网络请求前添加： RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_SECOND,"https://new.address.com");
    @Headers({URL_KEY+":"+URL_VALUE_SECOND})
    @POST("login")
    Single<NetResponse<UserEntity>> login(@Query("userName") String key);

    @GET("novelSearchApi")
    Observable<ResponseBody> testNet(@Query("userName") String ip);//测试+Rxjava

    //注册用户
//    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("register")
    @FormUrlEncoded
    Single<NetResponse<UserEntity>> register(@Field("entity") UserEntity userEntity);

    //人脸注册
    @Headers({URL_KEY+":"+URL_VALUE_FACE})
    @POST("detect64")
    @FormUrlEncoded
    Single<ResponseBody> detectFace(@Field("usr_id") String userId,@Field("image_base64") String imageBase64,@Field("ugroup") String ugroup);

    //人脸识别
    @Headers({URL_KEY+":"+URL_VALUE_FACE})
    @POST("search64")
    @FormUrlEncoded
    Single<ResponseBody> compareFace(@Field("image_base64") String imageBase64,@Field("ugroup") String ugroup);

    //问诊问题
    @Headers({URL_KEY+":"+URL_VALUE_QUESTION})
    @POST("ask/")
    @FormUrlEncoded
    Single<ResponseBody> getAnswer(@Field("question") String question);

    @Headers({URL_KEY+":"+URL_VALUE_ASK})
    @POST("ask/")
    @FormUrlEncoded
    Single<ResponseBody> ask(@Field("query") String question);

    @POST("search64")
    Single<NetResponse> getUserList(@Query("image_base64") String imageBase64) ;


}
