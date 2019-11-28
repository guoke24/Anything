package com.guohao.anything.net.xtalker_self;

import java.util.Date;

/**
 * Auto-generated: 2019-11-27 23:3:56
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
public class JsonsRootBean<T> {

    private int code;
    private String message;
    private Date time;
    private T result;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public Date getTime() {
        return time;
    }

    public void setResult(T result) {
        this.result = result;
    }
    public T getResult() {
        return result;
    }

}
