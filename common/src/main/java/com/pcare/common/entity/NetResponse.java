package com.pcare.common.entity;

import androidx.annotation.NonNull;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description:
 */
public class NetResponse<T> {
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    private int status;
    private String msg;
    private T data;

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }


    @NonNull
    @Override
    public String toString() {
        return "{\"status\":" + getStatus()
                + ",\"msg\":\"" + getMsg()
                + "\",\"data\":" + getData().toString() + "}";
    }
}
