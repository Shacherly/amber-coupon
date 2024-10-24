package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.trading.backend.client.IUserTagServiceApi;
import com.trading.backend.common.util.Maps;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.RemoteCallException;
import com.trading.backend.http.Response;
import com.trading.backend.util.RemoteCaller;
import io.renren.commons.tools.page.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author ~~ trading.s
 * @date 16:38 11/19/21
 */
@Service @Slf4j
public class UserTagServiceApiImpl implements IUserTagServiceApi {

    @Autowired
    private RemoteCaller caller;
    @Value("${remote-call.aceup.server.single-tag-user-page}")
    private String singleTagUri;
    @Value("${remote-call.aceup.server.multi-tag-user-page}")
    private String multiTagUri;


    @Override
    public PageData<String> getTagUidPage(int page, int pageSize, String tagCode) {
        Map<String, String> params = Maps.of("page", String.valueOf(page), "limit", String.valueOf(pageSize), "tagCode", tagCode);
        String s = caller.authExchange(singleTagUri, params, null, HttpMethod.GET);
        Response<PageData<String>> pageDataResponse = JSONObject.parseObject(s, new TypeReference<Response<PageData<String>>>() {});
        if (pageDataResponse.getCode() == 0) {
            return pageDataResponse.getData();
        }
        throw new RemoteCallException(pageDataResponse.getCode(), pageDataResponse.getMsg(), ExceptionEnum.REMOTE_SERVER_ERROR);
    }

    @Override
    public PageData<String> getTagsUidPage(int page, int pageSize, String tags) {
        JSONObject filter = JSONObject.parseObject(tags);
        Map<String, Object> body = Maps.of("page", String.valueOf(page), "limit", String.valueOf(pageSize), "filter", filter);
        String s = caller.authExchange(multiTagUri, null, body, HttpMethod.POST);
        Response<PageData<String>> pageDataResponse = JSONObject.parseObject(s, new TypeReference<Response<PageData<String>>>() {});
        if (pageDataResponse.getCode() == 0) {
            return pageDataResponse.getData();
        }
        throw new RemoteCallException(pageDataResponse.getCode(), pageDataResponse.getMsg(), ExceptionEnum.REMOTE_SERVER_ERROR);
    }

    @Override
    public long getTagTotal(String tagCode) {
        PageData<String> pageData = getTagUidPage(1, 0, tagCode);
        return pageData.getTotal();
    }

    @Override
    public long getTagsTotal(String tags) {
        PageData<String> pageData = getTagsUidPage(1, 0, tags);
        return pageData.getTotal();
    }
}
