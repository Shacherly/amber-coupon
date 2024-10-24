package com.trading.backend.service;

import com.trading.backend.common.http.PageResult;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.http.request.event.EventApprovalParam;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.request.event.EventListParam;
import com.trading.backend.http.response.event.EventDetailVO;
import com.trading.backend.http.response.event.EventListVO;

import java.util.List;
import java.util.Set;

public interface IAceUpEventService {


    List<String> extractUids(EventCreateParam param);

    CouponEvent createEvent(EventCreateParam param);

    PageResult<EventListVO> getEventPage(EventListParam param);

    EventDetailVO getEventDetail(Long eventId);

    void eventApproval(EventApprovalParam param);

    Set<Long> checkApprovalPendingCoupons(Long eventId, List<Long> toHanleCoupons);

    CouponEvent getPreStartEvent();

    CouponEvent getEvent(Long eventId);

    void lockEvent(Long eventId);

    void eventDiscard(Long eventId, String reason);

    void eventAbort(Long eventId, String reason);

    void eventAchieved(Long eventId);

    boolean hasIssuedRecords(Long eventId);



}
