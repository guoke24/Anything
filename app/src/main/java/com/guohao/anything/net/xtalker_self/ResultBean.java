package com.guohao.anything.net.xtalker_self;

public class ResultBean {

    private User user;
    private String account;
    private String token;
    //@JsonProperty("isBind")
    private boolean isbind;
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getAccount() {
        return account;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setIsbind(boolean isbind) {
        this.isbind = isbind;
    }
    public boolean getIsbind() {
        return isbind;
    }

}
