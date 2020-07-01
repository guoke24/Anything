package com.main.test;

public class TestBuilder {

    int i1,i2,i3;

    public TestBuilder setName(int i){
        i1 = i;
        return this;
    }

    public TestBuilder setName2(int i){
        i2 = i;
        return this;
    }
    public TestBuilder setName3(int i){
        i3 = i;
        return this;
    }

    public final void test(int i ){

    }

    int test(int i ,int q){
        return 8;
    }
}
