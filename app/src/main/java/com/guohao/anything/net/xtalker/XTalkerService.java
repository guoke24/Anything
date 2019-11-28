package com.guohao.anything.net.xtalker;


import com.guohao.anything.net.xtalker_self.JsonsRootBean;
import com.guohao.anything.net.xtalker_self.PostBean;
import com.guohao.anything.net.xtalker_self.ResultBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface XTalkerService {

    /**
     * 请求首页的函数
     * @return
     */
    @GET("/")
    Call<String> indnex();

    /**
     * 注册接口
     *
     * @param model 传入的是 Json 格式字串
     * @return 返回的是RspModel<AccountRspModel>
     */
    @Headers("Content-Type:application/json")
    @POST("/api/account/register")
    Call<String> accountRegister(@Body String model);

    /**
     * 注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("/api/account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);


    /**
     * 注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("/api/account/register")
    Call<JsonsRootBean<ResultBean>> accountRegisterSelf(@Body PostBean model);

}
