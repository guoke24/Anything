package com.guohao.anything.Hook.proxyDemo;

public class Guoke implements IShop {
    @Override
    public void buy() {
        System.out.println("buy");
    }
}
