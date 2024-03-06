package com.jintao.vipmanager.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: jintao
 * CreateDate: 2024/1/3 16:45
 * Description:兑换记录表
 */
@Entity(nameInDb = "convert_goods_info")
public class DbConvertGoodsInfo {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "uid")
    private int uid = 0;

    @Property(nameInDb = "goods_name")
    private String goodsName = "";

    @Property(nameInDb = "use_integral")
    private int useIntegral = 0;

    @Property(nameInDb = "desc")
    private String desc = "";

    @Property(nameInDb = "time")
    private String time = "";

    @Generated(hash = 120458840)
    public DbConvertGoodsInfo(Long id, int uid, String goodsName, int useIntegral,
            String desc, String time) {
        this.id = id;
        this.uid = uid;
        this.goodsName = goodsName;
        this.useIntegral = useIntegral;
        this.desc = desc;
        this.time = time;
    }

    @Generated(hash = 1817933744)
    public DbConvertGoodsInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(int useIntegral) {
        this.useIntegral = useIntegral;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
