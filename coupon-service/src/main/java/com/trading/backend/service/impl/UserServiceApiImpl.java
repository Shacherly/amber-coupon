package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.Maps;
import com.trading.backend.common.util.Predicator;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.kafka.message.UserTouchModel;
import com.trading.backend.kafka.publisher.KafkaPublisher;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserServiceApiImpl implements IUserServiceApi {

    @Autowired
    private RemoteCaller caller;
    @Autowired
    private RemoteServerProperty serverProperty;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${topics.user-touch}")
    private String userTouchTopic;
    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Override
    public boolean clientKycPassed(String uid) {
        if (StringUtils.isBlank(uid))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "uid");
        // if (StringUtils.startsWithIgnoreCase(uid, "test-")) {
        //     log.info("Testing uid {} kyc pased", uid);
        //     return true;
        // }
        JSONObject jsonObject = caller.get4JSONObject(
                serverProperty.getDomain() + serverProperty.getUserServer().getKycStatus(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Collections.singletonMap("uid", uid)
        );
        return Objects.equals(jsonObject.getInteger("kyc_status"), 2);
    }

    @Override
    public List<JSONObject> getMulitUserKyc(List<String> uids) {
        if (CollectionUtil.isEmpty(uids))
            return Collections.emptyList();
        return caller.post4JSONArray(
                serverProperty.getDomain() + serverProperty.getUserServer().getBatchKycStatus(),
                Collections.singletonMap("uids", uids),
                Collections.singletonMap("origin_channel", "BACKEND")
        );
    }

    @Override
    public Set<String> getMultiPassedUser(List<String> uids) {
        List<JSONObject> mulitUserKycs = getMulitUserKyc(uids);
        List<String> filted = Functions.filter(
                mulitUserKycs,
                Predicator.isEqual(json -> json.getInteger("kyc_status"), 2),
                json -> json.getString("user_id")
        );
        return Sets.newHashSet(filted);
    }

    /**
     * @param uid
     * @param templates
     * @param templateParams
     * @param types  0:email、 1:sms 、2:极光 、3:站内信 、4:toast
     */
    @Override
    public void messageNofity(String uid, List<String> templates, Map<String, String> templateParams, List<Integer> types) {
        // String url = serverProperty.getDomain() + serverProperty.getUserServer().getMessageInform();
        // Map<String, Object> body = new HashMap<>();
        // body.put("system_id", "coupon");
        // body.put("uid", uid);
        // body.put("template_code_list", templates);
        // body.put("type", types);
        // if (CollectionUtil.isNotEmpty(templateParams)) body.put("params", templateParams);
        //
        // log.info("SendMessageNofity to uid {}, template {}, params {}, type {}", uid, templates, templateParams, types);
        // caller.post4JSONObject(url, body, null);
    }

    /**
     * @param uid
     * @param templates
     * @param templateParams
     * @param types 0:email、 1:sms 、2:极光 、3:站内信 、4:toast
     * @param language
     */
    @Override
    public void kafkaNotify(String uid, List<String> templates, Map<String, Object> templateParams, List<Integer> types, String language) {
        UserTouchModel model = new UserTouchModel();
        if (StringUtils.isNotBlank(language)) model.setLanguage(language);
        model.setUid(uid);
        model.setTemplate_code_list(templates);
        model.setParams(templateParams);
        model.setType(types);
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String prefix = uid + "_";
        model.setRequest_id(prefix + timestamp);
        model.setUser_params(Collections.singletonMap("account", "email"));
        kafkaPublisher.publish(userTouchTopic, model);
    }


    @Override
    public Map<String, JSONObject> multiUserProfiles(Set<String> uids) {
        JSONArray jsonArray = caller.get4JSONArray(
                serverProperty.getDomain() + serverProperty.getUserServer().getProfileBatch(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Collections.singletonMap("uids", String.join(",", uids))
        );
        return jsonArray.stream().map(JSONObject.class::cast).collect(Collectors.toMap(obj -> obj.getString("uid"), Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public long getUserTotal() {
        JSONObject jsonObject = caller.get4JSONObject(
                serverProperty.getDomain() + serverProperty.getUserServer().getUserList(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Maps.of("page", "1", "page_size", "1")
        );
        return Optional.ofNullable(jsonObject.getLong("count")).orElse(0L);
    }

    @Override
    public List<JSONObject> getUserList(int page, int pageSize) {
        JSONObject jsonObject = caller.get4JSONObject(
                serverProperty.getDomain() + serverProperty.getUserServer().getUserList(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Maps.of("page", String.valueOf(page), "page_size", String.valueOf(pageSize))
        );
        return Functions.toList(jsonObject.getJSONArray("list"), JSONObject.class::cast);
    }

    @Override
    public List<String> getUidList(int page, int pageSize) {
        return Functions.toList(getUserList(page, pageSize), obj -> obj.getString("uid"));
    }

    @Override
    public Map<String, JSONObject> batchGetUser(Set<String> uids) {

        //result
        Map<String, JSONObject> result = new HashMap<>();

        if (CollectionUtil.isEmpty(uids)){
            return result;
        }

        //get url
        String url = serverProperty.getDomain() + serverProperty.getUserServer().getProfileBatch();

        HttpHeaders headers = new HttpHeaders();
        headers.add("origin_channel","APP");
        headers.add("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity(headers);
        String param = String.join(",", uids);

        try {
            ResponseEntity<String> entity = restTemplate.exchange(url+String.format("?uids=%s",param), HttpMethod.GET, httpEntity, String.class);
            if (entity.getStatusCode().equals(HttpStatus.OK)){
                JSONObject object = JSONObject.parseObject(entity.getBody());
                if (object.getInteger("code") == 0){
                    JSONArray data = object.getJSONArray("data");
                    if (data != null && data.size() > 0){
                        for (Object user : data) {
                            JSONObject userObject = JSONObject.parseObject(user.toString());
                            result.put(userObject.getString("uid"),userObject);
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("请求用户中心-batchGetUser-错误，uids:{}",uids,e);
        }

        return result;
    }
}
