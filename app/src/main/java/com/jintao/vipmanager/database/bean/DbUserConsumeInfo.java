package com.jintao.vipmanager.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: jintao
 * CreateDate: 2023/8/23 17:23
 * Description:
 */
@Entity(nameInDb = "consume_record_info")
public class DbUserConsumeInfo {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "uid")
    private int uid = 0;

    @Property(nameInDb = "content")
    private String content = "";

    @Property(nameInDb = "image_path")
    private String imagePath = "";

    @Property(nameInDb = "consume_time")
    private String consumeTime = "";

    @Property(nameInDb = "consume_amount")
    private float consumeAmount = 0;

    @Property(nameInDb = "consume_integral")
    private float consumeIntegral = 0;

    @Property(nameInDb = "conver_ratio")
    private String converRatio = "1";

    @Property(nameInDb = "consume_type")
    private boolean consumeType = false;


    @Generated(hash = 480178616)
    public DbUserConsumeInfo(Long id, int uid, String content, String imagePath,
            String consumeTime, float consumeAmount, float consumeIntegral,
            String converRatio, boolean consumeType) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.imagePath = imagePath;
        this.consumeTime = consumeTime;
        this.consumeAmount = consumeAmount;
        this.consumeIntegral = consumeIntegral;
        this.converRatio = converRatio;
        this.consumeType = consumeType;
    }

    @Generated(hash = 547378404)
    public DbUserConsumeInfo() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getConsumeTime() {
        return this.consumeTime;
    }

    public void setConsumeTime(String consumeTime) {
        this.consumeTime = consumeTime;
    }

    public float getConsumeIntegral() {
        return this.consumeIntegral;
    }

    public void setConsumeIntegral(float consumeIntegral) {
        this.consumeIntegral = consumeIntegral;
    }

    public boolean getConsumeType() {
        return this.consumeType;
    }

    public void setConsumeType(boolean consumeType) {
        this.consumeType = consumeType;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(float consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public boolean isConsumeType() {
        return consumeType;
    }

    public String getConverRatio() {
        return converRatio;
    }

    public void setConverRatio(String converRatio) {
        this.converRatio = converRatio;
    }
}
