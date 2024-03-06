package com.jintao.vipmanager.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: jintao
 * CreateDate: 2023/7/14 23:08
 * Description:兑换记录，兑换时间，消费记录，消费积分，剩余积分
 */
@Entity(nameInDb = "vip_user_info")
public class DbVipUserInfo {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "user_name")
    private String userName = "";

    @Property(nameInDb = "user_sex")
    private String userSex = "";

    @Property(nameInDb = "phone_number")
    private String phoneNumber = "";

    @Property(nameInDb = "uid")
    private int uid = 0;

    //消费次数
    @Property(nameInDb = "consume_number")
    private int consumeNumber = 0;

    //最后消费
    @Property(nameInDb = "last_amount")
    private float lastAmount = 0;

    //总积分
    @Property(nameInDb = "total_integral")
    private float totalIntegral = 0;

    //可用积分
    @Property(nameInDb = "user_integral")
    private float userIntegral = 0;

    //总消费金额
    @Property(nameInDb = "total_amount")
    private float totalAmount = 0;

    //扣除消费积分后，剩余积分对应的消费金额
    @Property(nameInDb = "current_amount")
    private float currentAmount = 0;

    //最近消费时间
    @Property(nameInDb = "consume_time")
    private long consumeTime = 0;

    @Property(nameInDb = "register_time")
    private String registerTime = "";

    @Generated(hash = 1169819871)
    public DbVipUserInfo(Long id, String userName, String userSex,
            String phoneNumber, int uid, int consumeNumber, float lastAmount,
            float totalIntegral, float userIntegral, float totalAmount,
            float currentAmount, long consumeTime, String registerTime) {
        this.id = id;
        this.userName = userName;
        this.userSex = userSex;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.consumeNumber = consumeNumber;
        this.lastAmount = lastAmount;
        this.totalIntegral = totalIntegral;
        this.userIntegral = userIntegral;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.consumeTime = consumeTime;
        this.registerTime = registerTime;
    }

    @Generated(hash = 1394531373)
    public DbVipUserInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getUserIntegral() {
        return this.userIntegral;
    }

    public void setUserIntegral(float userIntegral) {
        this.userIntegral = userIntegral;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getConsumeNumber() {
        return consumeNumber;
    }

    public void setConsumeNumber(int consumeNumber) {
        this.consumeNumber = consumeNumber;
    }

    public float getLastAmount() {
        return lastAmount;
    }

    public void setLastAmount(float lastAmount) {
        this.lastAmount = lastAmount;
    }

    public float getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(float totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(float currentAmount) {
        this.currentAmount = currentAmount;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}