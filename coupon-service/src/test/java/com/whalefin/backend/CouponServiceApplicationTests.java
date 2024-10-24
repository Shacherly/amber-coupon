package com.trading.backend;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.trading.backend.CouponServiceApplication;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.client.IAssetServiceApi;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.util.Functions;
import com.trading.backend.config.CouponAlarmProperty;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CommonCoinRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.mapper.CouponEventMapper;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.DataTransTask;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.ICouponAdminService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.service.ItradingAlarm;
import com.trading.backend.service.TestService;
import com.trading.backend.task.CouponWorthGrantTask;
import com.trading.backend.task.DisposableTask;
import com.trading.backend.task.PossessLifecycleTask;
import com.trading.backend.util.NumberCriteria;
import com.trading.backend.util.RemoteCaller;
import io.renren.tag.client.dto.dto.FilterCondition;
import io.renren.tag.client.dto.dto.MultiConditionTagUserPageReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ActiveProfiles("localsit")
@SpringBootTest(classes = CouponServiceApplication.class)
public class CouponServiceApplicationTests {

    @Autowired
    private TestService testService;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private ICouponAdminService couponAdminService;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private DataTransTask transferService;
    @Autowired
    private RedisService redisService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private CouponWorthGrantTask grantTask;
    @Autowired
    private IEarnServiceApi earnServiceApi;
    @Autowired
    private IAssetServiceApi assetServiceApi;
    @Autowired
    private IUserServiceApi userServiceApi;
    @Autowired
    private ISymbolServiceApi symbolServiceApi;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private RemoteCaller remoteCaller;
    @Autowired
    private RemoteServerProperty serverProperty;
    @Autowired
    private ItradingAlarm tradingAlarm;
    @Autowired
    private CouponAlarmProperty alarmProperty;
    @Autowired
    private CouponEventMapper couponEventMapper;
    @Autowired
    private DisposableTask disposableTask;
    @Autowired
    private PossessLifecycleTask lifecycleTask;
    @Autowired
    private NoviceProperty noviceProperty;


    @Test
    public void tttt() {
        ArrayList<Long> objects = new ArrayList<>();
        objects.add(-100L);
        List<PossessDO> clubCoupons = possessDao.getClubCoupons(objects);
        System.out.println();
    }

    @Test
    public void testExtPossessMapper() {
        List<PossessDO> possList = possessDao.getUserPossess("tradingmu", LocalDateTime.now());
        System.out.println();
    }

    @Test
    public void testSaveCoupon() {
        Coupon coupon = new Coupon();
        coupon.setName("test_inert");
        coupon.setTitle("{\"zh_TW\":\"加息券標題5\",\"ko_KR\":\"이자 추가 권 제목5\",\"en_US\":\"Title of coupon5\",\"zh_CN\":\"加息券标题5\"}");
        coupon.setStatus((short) 1);
        coupon.setDescr("{\"ko_KR\":\"한국어\\nonono\",\"zh_CN\":\"中文啊\\n中文牛啊\",\"pt_PT\":\"葡萄牙语\\n啥玩意？\"}");
        coupon.setOverlay(false);
        coupon.setTotal(1000L);
        coupon.setType(0);
        // coupon.setIssue(10);
        coupon.setPossessLimit(1);
        coupon.setExprInDays(10);
        coupon.setExprAtStart(null);
        coupon.setExprAtEnd(null);
        coupon.setApplyScene(0);

        CommonCoinRule coinRule1 = new CommonCoinRule();
        coinRule1.setApplyCoin("btc").setMaxAmount("100").setMinAmount("10");
        CommonCoinRule coinRule2 = new CommonCoinRule();
        coinRule2.setApplyCoin("usd").setMaxAmount("100").setMinAmount("10");
        CommonCoinRule coinRule3 = new CommonCoinRule();
        coinRule3.setApplyCoin("eth").setMaxAmount("100").setMinAmount("10");
        InterCouponRule interCouponRule = new InterCouponRule();
        interCouponRule.setInterDays(10).setMinSubscrDays(10)
                       .setMaxSubscrDays(15).setCoinRules(Lists.newArrayList(coinRule1, coinRule2, coinRule3));

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(interCouponRule));
        // JSONObject interRules = new JSONObject();
        // interRules.put(Property.InterRuleField.INTER_RATE, NumberCriteria.defaultScale("0.001"));
        // interRules.put(Property.InterRuleField.INTER_DAYS, 10);
        // interRules.put(Property.InterRuleField.MIN_SUBSCR_DAYS, 10);
        // interRules.put(Property.InterRuleField.MAX_SUBSCR_DAYS, 10);
        // interRules.put("min_require_amount", NumberCriteria.defaultScale(new BigDecimal("10")));
        coupon.setRule(jsonObject);
        // coupon.setCtime(LocalDateTime.now());
        // coupon.setUtime(LocalDateTime.now());
        // Map<String, String>
        testService.saveCoupon0(coupon);
    }

    @Test
    public void testSelectJSONObject() {
        List<Coupon> coupons = couponMapper.selectAll();
        Coupon coupon = new Coupon();
        JSONObject rules = new JSONObject();
        // rules.put("min_subsrc_days", 10);
        // rules.put("allow_transfer", true);
        rules.put("min_require_amount", NumberCriteria.defaultScale(new BigDecimal("8")));
        coupon.setRule(rules);

        Coupon coupon1 = couponMapper.selectByPrimaryKey(9L);
        InterCouponRule interCouponRule = JSON.toJavaObject(coupon1.getRule(), InterCouponRule.class);
        InterCouponRule interCouponRule1 = JSONObject.toJavaObject(coupon1.getRule(), InterCouponRule.class);

        JSONObject o = (JSONObject) JSON.toJSON(interCouponRule);
        JSONObject o1 = (JSONObject) JSONObject.toJSON(interCouponRule);
        JSONObject.toJSONString(coupon1.getRule());
        JSONObject rules1 = coupon1.getRule();

        Example example = new Example(Coupon.class);
        example.createCriteria().andEqualTo("id", 1L);
        // couponMapper.updateByExampleSelective(coupon, example);

        System.out.println();
    }

    @Test
    public void testExcepHandle() {

        // couponAdminService.saveOrUpdate(null);
    }


    @Test
    public void testRedis() {
        // Boolean add = stringRedisTemplate.opsForZSet().add(RedisKey.POSSESS_EXPR_SET, "5", 123123213D);
        // redisTemplate.opsForValue().set("value:key123", "test123");
        PossessDO poss = possessDao.getByPossessId(57579L);
        redisTemplate.opsForValue().set("possess:57579", poss);
        JSON o = (JSON) redisTemplate.opsForValue().get("possess:57579");
        PossessDO possessDO = JSONObject.toJavaObject(o, PossessDO.class);
        System.out.println();

        JSONObject.toJSONString(poss, SerializeConfig.globalInstance);
    }

    @Test
    public void testBuffer() {
        Object cacheObject = redisService.getCacheObject("buffered:possess:by_id:57579");
        PossessDO cacheObject1 = redisService.getCacheObject("buffered:possess:by_id:57579", PossessDO.class);

        List<BigDecimal> testList = redisService.getCacheList("testList", BigDecimal.class);
        PossessDO possessDO = possesService.getByPossessId(57579L);
        System.out.println();

        List<Coupon> available = couponService.getCoupons(Lists.newArrayList(180L, 240L));
        Map<String, Coupon> collect = available.stream().collect(Collectors.toMap(v -> v.getId().toString(), Function.identity(), (v1, v2) -> v1));
        redisService.setCacheMap(RedisKey.BUFFERED_COUPONS, collect);

        couponService.issuesIncrease(Functions.toList(available, Coupon::getId));

        CouponPossess update = new CouponPossess();
        update.setId(possessDO.getPossessId());
        update.setCtime(LocalDateTime.of(1900, 8, 8, 8, 8));
        // possesService.testUpdatePossess(update, );

        System.out.println();
    }

    @Test
    public void testTask() {
        grantTask.putToGrants();
        grantTask.takeToGrants();
    }

    @Test
    public void rpc() {
        /*CashEarnAcquireVO cashEarnAcquireVO = earnServiceApi.getCashEarnAcquireVO(null);
        assetServiceApi.activityDeposit("asda", "2", "usd");
        boolean asdas = userServiceApi.clientKycPassed("asdas");
        BigDecimal usdt = symbolServiceApi.exchange("USDT", new BigDecimal("2"));
        System.out.println();*/
        System.out.println("===:"+symbolServiceApi.getIndexPrice("BTC"));
    }

    @Test
    public void testClub() {
        couponService.clubRecevCheck("123", 123L);
        System.out.println();
    }

    @Test
    public void testUserTag() {
        String con = "{\"condition\":[{\"clean_no\":[\"clean_no-null\"]}],\"relation\":\"and\"}";
        FilterCondition condition = JSONUtil.toBean(con, FilterCondition.class);

        MultiConditionTagUserPageReq req = new MultiConditionTagUserPageReq();
        req.setPage(1);
        req.setLimit(Integer.MAX_VALUE);
        req.setFilter(condition);
        remoteCaller.authExchange("/server/tag/tag-user-ref/tag-user/multi-condition/page",null , req, HttpMethod.POST);
        // Result<PageData<String>> pageDataResult = userTagApiClient.multiConditionTagUserPage(req);
        System.out.println();
    }

    @Test
    public void testKyc() {
        JSONObject jsonObject = remoteCaller.get4JSONObject(serverProperty.getDomain() + serverProperty.getUserServer().getKycStatus(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Collections.singletonMap("uid", "619324ef3eb262c6099da413"));
        System.out.println();
    }

    @Test
    public void testInform() {
        userServiceApi.messageNofity(
                "6194ae7f6b38e1855e16986d",
                Collections.singletonList("MSG07_000008"),
                null,
                Collections.singletonList(3)
        );
    }

    @Test
    public void testAlarm() {
        CouponAlarmProperty.AlarmBody quantityRemain = alarmProperty.getQuantityRemain();
        String msg = quantityRemain.getMsg();
        String format = MessageFormat.format(msg, "VIP资产券", "80");
        quantityRemain.setMsg(format);
        tradingAlarm.alarm(quantityRemain);
    }

    @Test
    public void selectOne() {
        Example example = new Example(CouponEvent.class);
        CouponEvent couponEvent = couponEventMapper.selectOneByExample(example);
        // couponEventMapper.se
        System.out.println();
    }

    @Test
    public void testMultiUserProfiles() {
        userServiceApi.multiUserProfiles(Sets.newHashSet("6197220901940383a48e3849", "617e9b1ca796ef4736a2ee17"));
        System.out.println();
    }

    @Test
    public void transferActivityData() {
        transferService.transferActivityData();
    }

    @Test
    public void testUserList() {
        List<JSONObject> userList = userServiceApi.getUserList(1, 100);
        System.out.println();
    }

    @Test
    public void testCouponRedeem() {
        // disposableTask.couponRedeem();
        // disposableTask.discard(1);
        disposableTask.modifyCouponDays();
    }

    @Test
    public void testKycRedeem() {
        lifecycleTask.reActivate();
    }

    @Test
    public void testRedisssss() {
        disposableTask.removeInvalid2Grants();
    }

    @Test
    public void testActivate() {
        // disposableTask.activate("tradingmu,51");
        disposableTask.modifyCoupnRule();
    }


    @Autowired
    private ICashCouponService iCashCoupService;

    @Test
    public void testMultiActivate() {
        // String ss = "{\"uid\":\"trading\",\"orderId\":\"trading.sh_\",\"positionId\":\"trading.sh_\",\"holdingCoin\":\"ETH\",\"holdingSize\":\"10.0000000000000000\",\"subscrPeriod\":5,\"earnBusinessType\":1}";
        // EarnConsumeModel earnConsumeModel = JSONObject.parseObject(ss, EarnConsumeModel.class);
        // iCashCoupService.activOnUserSubscribe(earnConsumeModel, iCashCoupService.postback());

        String sss = "{\"uid\":\"trading\",\"depositCoin\":\"USDT\",\"arrivedAmount\":\"10\",\"transactionType\":\"1\"}";
        DepositInfoModel model = JSONObject.parseObject(sss, DepositInfoModel.class);
        iCashCoupService.activOnDepositSettle(model, iCashCoupService.postback());
    }

    @Test
    public void testSymbol() {
        Map<String, BigDecimal> prices = symbolServiceApi.getPrices();
        System.out.println();

    }

    @Test
    public void testMultiReceivePost() {
        List<BasalExportPossessBO> trading = couponService.receiveMultiUser(
                Lists.newArrayList("yyf1"), Lists.newArrayList(370L, 371L),
                -100L, PossessSourceEnum.INITIATIVE_RECEIVE, false
        );
        System.out.println();
    }

    @Test
    public void testPrelock() {
        couponService.couponPreLock(Lists.newArrayList(358L), 1L);
    }

    @Test
    public void testMultiReceive() {
        couponService.receiveMultiUser(
                Lists.newArrayList("trading"),
                Lists.newArrayList(358L),
                99999999L, PossessSourceEnum.EVENTS_GRANT, true
        );
    }

    @Test
    public void testProperty() {
        List<Long> registCouponV2 = noviceProperty.getRegistCouponV2();

    }

    @Test
    public void testEarnCouponReward() {
        disposableTask.rewardEarnReedemCoupon("6213118421329ee3628c5d1a");

    }

    @Test
    public void testBwcids() {
        Collection<Long> blueCouponIds = couponService.getBlueCouponIds();
        System.out.println();
    }

    @Test
    public void testBwcApprovals() {
        List<PossessDO> userPossBySource = possessDao.getUserPossBySource("622b19fba81ce03dd17b734c", 8);
        System.out.println();
    }
}
