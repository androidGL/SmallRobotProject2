/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pcare.common.entity;

import androidx.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class GlucoseEntity {
	@Id(autoincrement = true)
	private Long gluId;//血糖记录ID
	private String userId;//用户ID
	private int sequenceNumber;//序列号
	private Date timeDate;//时间
	private String glucoseConcentration;//血糖值，默认0
	private int sampleType;//测量类型，默认0
	private int sampleLocation;//测量位置，默认0
	private int status = 1;//状态码，默认0

	public GlucoseEntity(String userId, int sequenceNumber, Date timeDate, String glucoseConcentration, int sampleType, int sampleLocation, int status) {
		this.userId = userId;
		this.sequenceNumber = sequenceNumber;
		this.timeDate = timeDate;
		this.glucoseConcentration = glucoseConcentration;
		this.sampleType = sampleType;
		this.sampleLocation = sampleLocation;
		this.status = status;
	}

	@Generated(hash = 651072775)
	public GlucoseEntity(Long gluId, String userId, int sequenceNumber, Date timeDate, String glucoseConcentration, int sampleType, int sampleLocation,
			int status) {
		this.gluId = gluId;
		this.userId = userId;
		this.sequenceNumber = sequenceNumber;
		this.timeDate = timeDate;
		this.glucoseConcentration = glucoseConcentration;
		this.sampleType = sampleType;
		this.sampleLocation = sampleLocation;
		this.status = status;
	}

	@Generated(hash = 1590621335)
	public GlucoseEntity() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof GlucoseEntity) {
			return this.getTimeDate().equals(((GlucoseEntity) obj).getTimeDate())
					&& this.getGlucoseConcentration() .equals (((GlucoseEntity) obj).getGlucoseConcentration());
		}
		return false;
	}

	@Override
	public String toString() {
		return "{\"userId\":\"" + getUserId() +
				"\",\"gluId\":\"" + getGluId() +
				"\",\"sequenceNumber\":\"" + getSequenceNumber() +
				"\",\"timeDate\":\"" + getTimeDate() +
				"\",\"glucoseConcentration\":\"" + getGlucoseConcentration() +
				"\",\"sampleType\":\"" + getSampleType() +
				"\",\"sampleLocation\":\"" + getSampleLocation() +
				"\",\"status\":\"" + getStatus() +
				"\"}";
	}

	public Long getGluId() {
		if(null == this.gluId)
			this.gluId = 0L;
		return this.gluId;
	}

	public void setGluId(Long gluId) {
		this.gluId = gluId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Date getTimeDate() {
		return this.timeDate;
	}

	public void setTimeDate(Date timeDate) {
		this.timeDate = timeDate;
	}

	public String getGlucoseConcentration() {
		return this.glucoseConcentration;
	}

	public void setGlucoseConcentration(String glucoseConcentration) {
		this.glucoseConcentration = glucoseConcentration;
	}

	public int getSampleType() {
		return this.sampleType;
	}

	public void setSampleType(int sampleType) {
		this.sampleType = sampleType;
	}

	public int getSampleLocation() {
		return this.sampleLocation;
	}

	public void setSampleLocation(int sampleLocation) {
		this.sampleLocation = sampleLocation;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
