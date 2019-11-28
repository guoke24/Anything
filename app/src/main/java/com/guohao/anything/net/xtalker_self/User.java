package com.guohao.anything.net.xtalker_self;

import java.util.Date;

public class User {

    private String id;
    private String name;
    private String phone;
    private String portrait;
    private String desc;
    private int sex;
    private int follows;
    private int following;
    //@JsonProperty("isFollow")
    private boolean isfollow;
    //@JsonProperty("modifyAt")
    private Date modifyat;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
    public String getPortrait() {
        return portrait;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
    public int getSex() {
        return sex;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }
    public int getFollows() {
        return follows;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
    public int getFollowing() {
        return following;
    }

    public void setIsfollow(boolean isfollow) {
        this.isfollow = isfollow;
    }
    public boolean getIsfollow() {
        return isfollow;
    }

    public void setModifyat(Date modifyat) {
        this.modifyat = modifyat;
    }
    public Date getModifyat() {
        return modifyat;
    }

}
