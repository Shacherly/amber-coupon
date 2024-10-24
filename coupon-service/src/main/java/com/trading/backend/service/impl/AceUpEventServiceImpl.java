package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.enums.EventApprovalEnum;
import com.trading.backend.common.enums.EventObjectEnum;
import com.trading.backend.common.enums.EventStageEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.http.request.event.EventApprovalParam;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.request.event.EventListParam;
import com.trading.backend.http.response.event.EventDetailVO;
import com.trading.backend.http.response.event.EventListVO;
import com.trading.backend.mapper.CouponEventMapper;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.service.IAceUpEventService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.util.Builder;
import com.trading.backend.util.Converter;
import com.trading.backend.util.PageContext;
import com.trading.backend.util.ReflectionUtil;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ~~ trading.s
 * @date 15:34 11/04/21
 */
@Service @Slf4j
public class AceUpEventServiceImpl implements IAceUpEventService {

    @Autowired
    private CouponEventMapper couponEventMapper;
    // @Autowired
    // private IUserTagApiClient userTagApiClient;
    // @Autowired
    // private IUserApiClient userApiClient;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private RemoteCaller remoteCaller;
    @Value("${remote-call.aceup.server.multi-tag-user-page}")
    private String multiTagUserUri;


    @Override
    public List<String> extractUids(EventCreateParam param) {
        List<String> uids = new ArrayList<>();
        // if (Objects.equals(param.getUserRangeType(), EventObjectEnum.TAGGED.getProperty())) {
        //     MultiConditionTagUserPageReq remoteReq = new MultiConditionTagUserPageReq();
        //     remoteReq.setPage(1);
        //     remoteReq.setLimit(Integer.MAX_VALUE);
        //     remoteReq.setFilter(param.getMultiTagCondition());
        //     String exchange = remoteCaller.authExchange(multiTagUserUri, null, remoteReq, HttpMethod.POST);
        //     Result<PageData<String>> pageDataResult = JSONObject.parseObject(exchange, new TypeReference<Result<PageData<String>>>() {});
        //     // Result<PageData<String>> pageDataResult = userTagApiClient.multiConditionTagUserPage(remoteReq);
        //     if (pageDataResult.success() && pageDataResult.getData() != null && pageDataResult.getData().getTotal() > 0) {
        //         UserInternalReq.UserProfileBatchReq userProfileBatchReq = new UserInternalReq.UserProfileBatchReq();
        //         userProfileBatchReq.setUids(pageDataResult.getData().getList());
        //         // TODO 过滤清退用户
        //         Response<List<UserInternalRes.UserProfileRes>> userListResp =
        //                 userApiClient.getUserProfileBatch(userProfileBatchReq);
        //
        //         if (userListResp.getCode() == 0) {
        //             if (CollectionUtil.isNotEmpty(userListResp.getData())) {
        //                 List<String> collect = userListResp.getData().stream().map(UserInternalRes.UserProfileRes::getUid).collect(Collectors.toList());
        //                 uids.addAll(collect);
        //             }
        //         }
        //     }
        //     throw new BusinessException(pageDataResult.getCode(), pageDataResult.getMsg(), ExceptionEnum.REMOTE_SERVER_ERROR);
        // }
        // else if (Objects.equals(param.getUserRangeType(), EventObjectEnum.IMPORTED.getProperty())
        //         || Objects.equals(param.getUserRangeType(), EventObjectEnum.CHOSEN.getProperty())) {
        //     // uids.addAll(param.getUids());
        // }
        // else if (Objects.equals(param.getUserRangeType(), EventObjectEnum.ENTIRE.getProperty())) {
        //
        // }
        return uids;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponEvent createEvent(EventCreateParam param) {
        CouponEvent source = Converter.fromRequest(param);
        try {
            couponEventMapper.insertSelective(source);
            // // 更新 eventId
            // CouponPossess toUpdate = new CouponPossess().setSourceId(source.getId());
            // Example example = new Example(CouponPossess.class);
            // example.createCriteria().andIn("id", possessIds);
            // possessMapper.updateByExampleSelective(toUpdate, example);
        } catch (Exception e) {
            log.error("createEventError, cause {}", e.getMessage(), e);
            throw new BusinessException(ExceptionEnum.INSERT_ERROR);
        }
        List<Long> possessIds = new ArrayList<>();
        List<String> uids = new ArrayList<>();
        // 全量用户和标签用户不用检查券数量和持券上限  ,先存储标签值，再通过任务发放
        if (source.getApprovalEvent() && Objects.equals(param.getUserRangeType(), EventObjectEnum.SINGLE.getProperty())) {
            log.info("ApprovalEventCreating, objectType={}, uid={}", param.getUserRangeType(), param.getUserRangeParam());
            if (StringUtils.isBlank(param.getUserRangeParam()))
                throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "UserRangeParam");
            uids.add(param.getUserRangeParam());
            // 查询某个券是否已经在审批中
            // https://www.teambition.com/task/61dba12bf792f7003f7642b9
            // 上面的优化需求去掉pending检测
            // checkApprovalPendingCoupons(source.getId(), param.getCouponIds());

            // 提前锁定coupon.pre_lock 放在receiveMultiUser里面
            // couponService.couponPreLock(param.getCouponIds(), (long) 1);

            // 生成预发放记录->审核通过直接更新预发放状态为正常状态，审核不通过则删除预发放记录
            PossessSourceEnum possSource = param.getEventType() == 3 ? PossessSourceEnum.BWC_APPROVAL_COUPON : PossessSourceEnum.EVENTS_GRANT;
            List<BasalExportPossessBO> exportPossessBo = couponService.receiveMultiUser(
                    uids, param.getCouponIds(), source.getId(), possSource, source.getApprovalEvent());
            possessIds.addAll(Functions.toList(exportPossessBo, BasalExportPossessBO::getPossesssId));
        }
        // if (Objects.equals(param.getUserRangeType(), EventObjectEnum.ENTIRE.getProperty())
        //         || Objects.equals(param.getUserRangeType(), EventObjectEnum.TAGGED.getProperty())
        //         || Objects.equals(param.getUserRangeType(), EventObjectEnum.IMPORTED.getProperty())) {
        //     uids = extractUids(param);
        //
        // }
        // else if (Objects.equals(param.getUserRangeType(), EventObjectEnum.CHOSEN.getProperty())) {
        //
        //     uids = extractUids(param);
        // }
        return source;
    }

    @Override
    public PageResult<EventListVO> getEventPage(EventListParam param) {
        Example example = ReflectionUtil.getExample(param, CouponEvent.class);
        PageResult<CouponEvent> page = PageContext.selectPage(() -> couponEventMapper.selectByExample(example), "CTIME DESC, ID DESC");
        PageResult<EventListVO> result = PageResult.of(page);
        List<CouponEvent> items = page.getItems();
        if (CollectionUtil.isEmpty(items)) return result;
        result.setItems(Functions.toList(items, this::mapping));
        return result;
    }

    @Override
    public EventDetailVO getEventDetail(Long eventId) {
        CouponEvent couponEvent = couponEventMapper.selectByPrimaryKey(eventId);
        if (couponEvent == null) return null;
        return mapping1(couponEvent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eventApproval(EventApprovalParam param) {
        Example example = new Example(CouponEvent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", param.getEventId())
                .andEqualTo("approvalEvent", true);

        CouponEvent approvalUpdate = new CouponEvent().setId(param.getEventId());
        if (EventApprovalEnum.getByStage(param.getResult()) == EventApprovalEnum.PENGIND) {
            criteria.andEqualTo("approvalStage", EventApprovalEnum.UNCOMMITTED.getStage())
                    .andEqualTo("eventStage", EventStageEnum.PRE_START.getStage());

            approvalUpdate.setApprovalStage(EventApprovalEnum.PENGIND.getStage());
            approvalUpdate.setEventStage(EventStageEnum.RUNNING.getStage());
        }
        else if (EventApprovalEnum.getByStage(param.getResult()) == EventApprovalEnum.DENIED) {
            criteria.andEqualTo("approvalStage", EventApprovalEnum.PENGIND.getStage())
                    .andEqualTo("eventStage", EventStageEnum.RUNNING.getStage());

            approvalUpdate.setApprovalStage(EventApprovalEnum.DENIED.getStage());
            approvalUpdate.setEventStage(EventStageEnum.ALL_FAILED.getStage());

            possesService.releasePrelock(param.getEventId(), false);
        }
        else if (EventApprovalEnum.getByStage(param.getResult()) == EventApprovalEnum.APPROVALED) {
            criteria.andEqualTo("approvalStage", EventApprovalEnum.PENGIND.getStage())
                    .andEqualTo("eventStage", EventStageEnum.RUNNING.getStage());

            approvalUpdate.setApprovalStage(EventApprovalEnum.APPROVALED.getStage());
            approvalUpdate.setEventStage(EventStageEnum.ALL_GRANTED.getStage());

            possesService.releasePrelock(param.getEventId(), true);
        }
        int effectEvents = couponEventMapper.updateByExampleSelective(approvalUpdate, example);
        if (effectEvents != 1)
            throw new BusinessException("Update event stage error!", ExceptionEnum.UPDATE_ERROR);
        // CouponEvent couponEvent = couponEventMapper.selectByPrimaryKey(param.getEventId());
    }

    @Override
    public Set<Long> checkApprovalPendingCoupons(Long eventId, List<Long> toHanleCoupons) {
        Example example = new Example(CouponEvent.class);
        example.createCriteria()
               .andEqualTo("approvalEvent", true)
               .andEqualTo("approvalStage", EventApprovalEnum.PENGIND.getStage());

        // example.selectProperties("couponIds");

        List<CouponEvent> pendingEvents = couponEventMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(pendingEvents)) return Collections.emptySet();
        Set<Long> allPendingCoupons = new HashSet<>(64);
        for (CouponEvent event : pendingEvents) {
            if (Objects.equals(eventId, event.getId())) continue;
            Set<Long> collect = Arrays.stream(event.getCouponIds().split(",")).map(Long::parseLong).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(collect)) {
                allPendingCoupons.addAll(collect);
            }
        }
        Collection<Long> intersection = CollectionUtil.intersection(allPendingCoupons, toHanleCoupons);
        if (allPendingCoupons.containsAll(intersection))
            throw new VisibleException(ExceptionEnum.COUPON_IN_PROCESS_APPROVAL, intersection);
        return allPendingCoupons;
    }


    @Override
    public CouponEvent getPreStartEvent() {
        Example example = new Example(CouponEvent.class);
        example.createCriteria()
               .andEqualTo("eventStage", EventStageEnum.PRE_START.getStage())
               .andEqualTo("approvalEvent", false);
        example.setOrderByClause("ctime ASC LIMIT 1");
        return couponEventMapper.selectOneByExample(example);
    }

    @Override
    public CouponEvent getEvent(Long eventId) {
        return couponEventMapper.selectByPrimaryKey(eventId);
    }

    @Override
    public void lockEvent(Long eventId) {
        Objects.requireNonNull(eventId);
        CouponEvent toUpdate = new CouponEvent().setId(eventId);
        toUpdate.setEventStage(EventStageEnum.RUNNING.getStage());
        couponEventMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    public void eventDiscard(Long eventId, String reason) {
        Objects.requireNonNull(eventId);
        CouponEvent toUpdate = new CouponEvent().setId(eventId).setResultPhase(reason);
        toUpdate.setEventStage(EventStageEnum.ALL_FAILED.getStage());
        couponEventMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    public void eventAbort(Long eventId, String reason) {
        Objects.requireNonNull(eventId);
        CouponEvent toUpdate = new CouponEvent().setId(eventId).setResultPhase(reason);
        toUpdate.setEventStage(EventStageEnum.PARTLY_GRANTED.getStage());
        couponEventMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    public void eventAchieved(Long eventId) {
        Objects.requireNonNull(eventId);
        CouponEvent toUpdate = new CouponEvent().setId(eventId).setResultPhase("ALL SUCCESSED");
        toUpdate.setEventStage(EventStageEnum.ALL_GRANTED.getStage());
        couponEventMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    public boolean hasIssuedRecords(Long eventId) {
        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("sourceId", eventId)
               .andGreaterThanOrEqualTo("possessStage", PossessStageEnum.ENABLE.getCode());
        example.selectProperties("id");
        List<CouponPossess> possessList = possessMapper.selectByExample(example);
        return CollectionUtil.isNotEmpty(possessList);
    }

    private EventListVO mapping(CouponEvent source) {
        Builder<EventListVO> builder = Builder.of(EventListVO::new);
        builder.with(EventListVO::setId, source.getId())
               .with(EventListVO::setEventType, source.getType())
               .with(EventListVO::setApprovalNeed, source.getApprovalEvent())
               .with(EventListVO::setApprovalStage, source.getApprovalStage())
               .with(EventListVO::setEventStage, source.getEventStage())
               .with(EventListVO::setEventName, source.getName())
               .with(EventListVO::setEventDescr, source.getDescr())
               .with(EventListVO::setStartTime, TemporalUtil.toEpochMilli(source.getStartTime()))
               .with(EventListVO::setCreateTime, TemporalUtil.toEpochMilli(source.getCtime()))
               .with(EventListVO::setUpdateTime, TemporalUtil.toEpochMilli(source.getUtime()));
        return builder.build();
    }

    private EventDetailVO mapping1(CouponEvent source) {
        Builder<EventDetailVO> builder = Builder.of(EventDetailVO::new);
        builder.with(EventDetailVO::setId, source.getId())
               .with(EventDetailVO::setName, source.getName())
               .with(EventDetailVO::setDescr, source.getDescr())
               .with(EventDetailVO::setCouponIds, source.getCouponIds())
               .with(EventDetailVO::setType, source.getType())
               .with(EventDetailVO::setEventStage, source.getEventStage())
               .with(EventDetailVO::setApprovalEvent, source.getApprovalEvent())
               .with(EventDetailVO::setApprovalStage, source.getApprovalStage())
               .with(EventDetailVO::setObjectType, source.getObjectType())
               .with(EventDetailVO::setObjectAttaches, source.getObjectAttaches())
               .with(EventDetailVO::setRemark, source.getRemark())
               .with(EventDetailVO::setStartTime, TemporalUtil.toEpochMilli(source.getStartTime()))
               .with(EventDetailVO::setCtime, TemporalUtil.toEpochMilli(source.getCtime()))
               .with(EventDetailVO::setResultPhase, source.getResultPhase());
        return builder.build();
    }
}
