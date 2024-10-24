package com.trading.backend.task;


import com.trading.backend.annotation.Traceable;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.client.IUserTagServiceApi;
import com.trading.backend.common.enums.EventObjectEnum;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.service.IAceUpEventService;
import com.trading.backend.service.ICouponService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动发券任务
 */
@Slf4j
@Component
public class EventIssueTask {

    @Autowired
    private IAceUpEventService eventService;
    @Autowired
    private IUserTagServiceApi tagServiceApi;
    @Autowired
    private ExecutorConfigurer executorConfigurer;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IUserServiceApi userServiceApi;


    @XxlJob("scanEvents") @Traceable
    // @Scheduled(fixedRate = /*60000L*/3000L)
    public void scanEvents() {
        log.info("EventScan to issue coupons");
        CouponEvent event = eventService.getPreStartEvent();
        // CouponEvent event = eventService.getEvent(2172L);
        if (Objects.isNull(event)) return;
        eventService.lockEvent(event.getId());

        String couponArray = event.getCouponIds();
        String[] split = StringUtils.split(couponArray, ",");
        if (split == null || split.length == 0) {
            // throw new BusinessException(nu);
            eventService.eventDiscard(event.getId(), "No coupons to be issued");
            return;
        }
        List<Long> couponIds = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
        try {

            List<Coupon> coupons = couponService.getAndPresentCheck(couponIds);
            couponService.statusCheck(coupons);
            long total = 0;
            BiFunction<Integer, Integer, List<String>> biProducer = null;
            Function<List<String>, Integer> consumer = uids -> couponService.eventCouponIssue(uids, coupons, event.getId()).size();

            if (EventObjectEnum.sinleTag(event.getObjectType())) {
                total = tagServiceApi.getTagTotal(event.getObjectAttaches());
                biProducer = (page, pageSize) -> tagServiceApi.getTagUidPage(page, pageSize, event.getObjectAttaches()).getList();
            }
            else if (EventObjectEnum.multiTag(event.getObjectType())) {
                total = tagServiceApi.getTagsTotal(event.getObjectAttaches());
                biProducer = (page, pageSize) -> tagServiceApi.getTagsUidPage(page, pageSize, event.getObjectAttaches()).getList();
            }
            else if (EventObjectEnum.entire(event.getObjectType())) {
                total = userServiceApi.getUserTotal();
                biProducer = (page, pageSize) -> userServiceApi.getUidList(page, pageSize);
            }

            if (total == 0) {
                eventService.eventDiscard(event.getId(), "There is no one in condition of " + event.getObjectAttaches());
                return;
            }
            else {
                couponService.remainCheck4Event(coupons, total);
            }

            // why? Remote server error, code: 259,999, reason: page_size#must be less than or equal to 1000.
            if (EventObjectEnum.entire(event.getObjectType()))
                executorConfigurer.serial(total, coupons.size() == 1 ? 2 : coupons.size(), biProducer, Function.identity(), consumer);
            else
                executorConfigurer.serial(total, coupons.size(), biProducer, Function.identity(), consumer);

            eventService.eventAchieved(event.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (eventService.hasIssuedRecords(event.getId())) {
                eventService.eventAbort(event.getId(), e.getMessage());
            }
            else {
                eventService.eventDiscard(event.getId(), e.getMessage());
            }
        }


    }
}
