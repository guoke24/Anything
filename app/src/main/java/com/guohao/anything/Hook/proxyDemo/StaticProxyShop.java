package com.guohao.anything.Hook.proxyDemo;

/**
 * 静态代理类
 */
public class StaticProxyShop implements IShop {

    private IShop mIShop;

    public StaticProxyShop(IShop iShop){
        mIShop = iShop;
    }

    @Override
    public void buy() {
        if(mIShop != null)
            mIShop.buy();
    }
}
