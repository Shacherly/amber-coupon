package com.trading.backend.client;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUserServiceApi {

    boolean clientKycPassed(String uid);

    List<JSONObject> getMulitUserKyc(List<String> uids);

    Set<String> getMultiPassedUser(List<String> uids);

    /**
     * 批量获取用户详情
     * @param uids
     * @return
     */
    Map<String, JSONObject> batchGetUser(Set<String> uids);

    Map<String, JSONObject> multiUserProfiles(Set<String> uids);

    void messageNofity(String uid, List<String> templates, Map<String, String> templateParams, List<Integer> types);

    void kafkaNotify(String uid, List<String> templates, Map<String, Object> templateParams, List<Integer> types, String language);

    long getUserTotal();

    List<JSONObject> getUserList(int page, int pageSize);

    List<String> getUidList(int page, int pageSize);
}
