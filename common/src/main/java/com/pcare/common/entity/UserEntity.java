package com.pcare.common.entity;

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
    private String userName;//昵称或真实姓名
    private String userStature;//身高: 173cm
    private String userWeight;//体重：50kg
    private String userRebotId;//用户使用的机器人ID号或设备号
    private boolean currentUser;//当前使用用户
    @Generated(hash = 1974957160)
    public UserEntity(Long id, String userId, int userType, int userBirthYear,
            int userGender, String userName, String userStature, String userWeight,
            String userRebotId, boolean currentUser) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.userBirthYear = userBirthYear;
        this.userGender = userGender;
        this.userName = userName;
        this.userStature = userStature;
        this.userWeight = userWeight;
        this.userRebotId = userRebotId;
        this.currentUser = currentUser;
    }
    @Generated(hash = 1433178141)
    public UserEntity() {
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
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserStature() {
        return this.userStature;
    }
    public void setUserStature(String userStature) {
        this.userStature = userStature;
    }
    public String getUserWeight() {
        return this.userWeight;
    }
    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }
    public String getUserRebotId() {
        return this.userRebotId;
    }
    public void setUserRebotId(String userRebotId) {
        this.userRebotId = userRebotId;
    }
    @Override
    public String toString() {
        return "{\"userId\":\""+getUserId() +
                "\",\"userType\":"+getUserType()+
                ",\"userBirthYear\":"+getUserBirthYear()+
                ",\"userName\":\""+getUserName()+
                "\",\"userStature\":\""+getUserStature()+
                "\",\"userWeight\":\""+getUserWeight()+"\"}";
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
}
