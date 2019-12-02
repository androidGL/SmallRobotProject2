package com.pcare.common.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author: gl
 * @CreateDate: 2019/11/26
 * @Description: 血压实体类
 */
@Entity
public class BPMEntity {
    private String userId;//用户ID
    private String bpmId;//血压记录ID
    private String systolicData; //高血压
    private String diastolicData;//低血压
    private String meanAPData;   //平均压
    private String unit;  //血压单位
    private String pulseData; //脉搏
    private Date timeData;//时间
    @Generated(hash = 2030758059)
    public BPMEntity(String userId, String bpmId, String systolicData,
            String diastolicData, String meanAPData, String unit, String pulseData,
            Date timeData) {
        this.userId = userId;
        this.bpmId = bpmId;
        this.systolicData = systolicData;
        this.diastolicData = diastolicData;
        this.meanAPData = meanAPData;
        this.unit = unit;
        this.pulseData = pulseData;
        this.timeData = timeData;
    }
    @Generated(hash = 549838386)
    public BPMEntity() {
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSystolicData() {
        return this.systolicData;
    }
    public void setSystolicData(String systolicData) {
        this.systolicData = systolicData;
    }
    public String getDiastolicData() {
        return this.diastolicData;
    }
    public void setDiastolicData(String diastolicData) {
        this.diastolicData = diastolicData;
    }
    public String getMeanAPData() {
        return this.meanAPData;
    }
    public void setMeanAPData(String meanAPData) {
        this.meanAPData = meanAPData;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getPulseData() {
        return this.pulseData;
    }
    public void setPulseData(String pulseData) {
        this.pulseData = pulseData;
    }
    public Date getTimeData() {
        return this.timeData;
    }
    public void setTimeData(Date timeData) {
        this.timeData = timeData;
    }
    public String getBpmId() {
        return this.bpmId;
    }
    public void setBpmId(String bpmId) {
        this.bpmId = bpmId;
    }

    @Override
    public String toString() {
        return "BPMEntity{" +
                "userId='" + userId + '\'' +
                ", bpmId='" + bpmId + '\'' +
                ", systolicData='" + systolicData + '\'' +
                ", diastolicData='" + diastolicData + '\'' +
                ", meanAPData='" + meanAPData + '\'' +
                ", unit='" + unit + '\'' +
                ", pulseData='" + pulseData + '\'' +
                ", timeData=" + timeData +
                '}';
    }
}
