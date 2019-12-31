package com.pcare.common.net;

import android.util.Log;

import com.pcare.common.entity.NetResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description:
 */
//需要拦截api及返回结果配置类
public class NetTest {
    private static List<String> pathList = new ArrayList();
    private static NetResponse netResponse = new NetResponse();

    /**
     * 判断这个URL是不是需要拦截，如果需要拦截就返回interceptApi，即apiResponse的key值
     * @param url 需要拦截的URL
     * @return
     */
    public static String isExist(HttpUrl url,Object data) {
        //初始化apiResponse，添加键值对
        init();
        if(pathList.contains(url.encodedPath())){
            netResponse.setStatus(1);
            netResponse.setMsg("调用成功");
            netResponse.setData(data);
            return netResponse.toString();
        }
        return null;
    }

    public static void init() {
        if (pathList.size() > 0) {
            return;
        }
        pathList.add("/register");
//        apiResponse.put("searchAuthors", "{\"result\":\"000000\",\"msg\":\"调用成功\",\"data\":{\"userid\":\"TY6XFGLSUICQ2\",\"empno\":\"Y6XFGLSUICQ2\",\"userName\":\"18938856298\",\"sex\":\"未知\",\"status\":\"普通群员\",\"idType\":\"身份证\",\"empIdNo\":\"不详\",\"mobileNo\":\"18938856298\",\"homeAddr\":null,\"claimBankNo\":null,\"accountNo\":null,\"wechat\":\"微信\",\"occupation\":null,\"nickname\":\"昵称\"}}");
//        apiResponse.put("register", "{\"status\":1,\"msg\":\"调用成功\",\"data\":{\"userId\":\"20191118\",\"userType\":0,\"userBirthYear\":1994,\"userName\":\"王二狗\",\"userStature\":\"185cm\",\"userWeight\":\"50kg\"}}");
    }
}
