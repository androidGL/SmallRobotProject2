package com.pcare.common.entity;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @Author: gl
 * @CreateDate: 2019/11/20
 * @Description:
 */
@Entity
public class UserEntity {
    @Id(autoincrement = true)
    private Long id;
    private String userId;//用户ID
    private int userType;//用户类型：爷爷:0，奶奶:1，叔叔:2，阿姨:3等
    private int userBirthYear;//出生年份: 1999
    private int userGender;//性别 男：0，女：1
    private String username;//昵称或真实姓名
    private String userStature;//身高: 173cm
    private String userWeight;//体重：50kg
    private String userRobotId;//用户使用的机器人ID号或设备号
    private boolean currentUser;//当前使用用户
    private String password;

    public UserEntity(Long id, String userId, int userType, int userBirthYear,
            int userGender, String username, String userStature, String userWeight,
            String userRobotId, boolean currentUser) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.userBirthYear = userBirthYear;
        this.userGender = userGender;
        this.username = username;
        this.userStature = userStature;
        this.userWeight = userWeight;
        this.userRobotId = userRobotId;
        this.currentUser = currentUser;
    }
    public UserEntity() {
    }
    @Generated(hash = 1185072161)
    public UserEntity(Long id, String userId, int userType, int userBirthYear,
            int userGender, String username, String userStature, String userWeight,
            String userRobotId, boolean currentUser, String password) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.userBirthYear = userBirthYear;
        this.userGender = userGender;
        this.username = username;
        this.userStature = userStature;
        this.userWeight = userWeight;
        this.userRobotId = userRobotId;
        this.currentUser = currentUser;
        this.password = password;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getUserType() {
        return this.userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
    public int getUserBirthYear() {
        return this.userBirthYear;
    }
    public void setUserBirthYear(int userBirthYear) {
        this.userBirthYear = userBirthYear;
    }
    public int getUserGender() {
        return this.userGender;
    }
    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }
    public String getUserName() {
        return this.username;
    }
    public void setUserName(String username) {
        this.username = username;
    }
    public String getUserStature() {
        if(TextUtils.isEmpty(this.userStature))
            this.userStature = "0";
        return this.userStature;
    }
    public void setUserStature(String userStature) {
        this.userStature = userStature;
    }
    public String getUserWeight() {
        if(TextUtils.isEmpty(this.userWeight))
            this.userWeight = "0";
        return this.userWeight;
    }
    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }
    public String getUserRobotId() {
        if(TextUtils.isEmpty(this.userRobotId))
            this.userRobotId = "0";
        return this.userRobotId;
    }
    public void setUserRobotId(String userRobotId) {
        this.userRobotId = userRobotId;
    }

    public String getPassword(){
        return "123456";
    }
    public int getStatus(){
        return 1;
    }

    @Override
    public String toString() {
        return "{\"userId\":\""+getUserId() +
                "\",\"userType\":"+getUserType()+
                ",\"userGender\":"+getUserGender()+
                ",\"userBirthYear\":"+getUserBirthYear()+
                ",\"username\":\""+getUserName()+
                "\",\"password\":\""+getPassword()+
                "\",\"userStature\":\""+getUserStature()+
                "\",\"userWeight\":\""+getUserWeight()+
                "\",\"status\":"+getStatus()+
                ",\"userRobotId\":\""+getUserRobotId()+"\"}";
    }
    public boolean getCurrentUser() {
        return this.currentUser;
    }
    public UserEntity setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
        return this;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
