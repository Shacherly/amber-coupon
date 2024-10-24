package com.trading.backend.client;

import com.trading.backend.common.model.user.req.UserInternalReq;
import com.trading.backend.common.model.user.res.UserInternalRes;
import com.trading.backend.common.model.web.PageResult;
import com.trading.backend.common.model.web.Response;
import com.trading.backend.user.UserApi;
import io.renren.commons.tools.feign.IgnoreHeaderInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trading-UserFeignClient", url = "${remote-call.domain}", path = "/user-center")
@RequestMapping(value = "/internal/v1/user", headers = {"origin_channel=${tt-fin.header.origin-channel}"})
@IgnoreHeaderInterceptor
public interface IUserApiClient extends UserApi {
    @Override
    @GetMapping({"/profile"})
    Response<UserInternalRes.UserProfileRes> getUserProfile(@RequestParam("uid") String uid);

    @Override
    @GetMapping({"/list"})
    Response<PageResult<UserInternalRes.UserListRes>> getUserList(@SpringQueryMap @Validated UserInternalReq.UserListReq req);

    @Override
    @GetMapping({"/profile_batch"})
    Response<List<UserInternalRes.UserProfileRes>> getUserProfileBatch(@SpringQueryMap @Validated UserInternalReq.UserProfileBatchReq req);

}