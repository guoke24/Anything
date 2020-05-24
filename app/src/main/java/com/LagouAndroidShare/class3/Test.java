package com.LagouAndroidShare.class3;

import java.io.Serializable;

public class Test implements Serializable, Cloneable{
    private int num = 1;
    String str = "abc";

    public int add(int i) {
        int j = 10;
        num = num + i;
        return num;
    }
}