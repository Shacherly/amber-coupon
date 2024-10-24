package com.trading.backend.service;

import com.alibaba.fastjson.JSONObject;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponDescrTemplate;
import com.trading.backend.http.request.aceup.CouponListReq;
import com.trading.backend.http.request.aceup.IssueListReq;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.response.aceup.CouponDetatilRes;
import com.trading.backend.http.response.aceup.CouponListRes;
import com.trading.backend.http.response.aceup.IssueDetailRes;
import com.trading.backend.http.response.aceup.IssueListRes;
import com.trading.backend.http.response.desctemplate.DescrTemplateVO;

import java.util.List;

public interface ICouponAdminService {

    /**
     * 保存或更新券
     * @param source
     */
    Long saveOrUpdate(Coupon source);

    void absoluteSave(Coupon source);

    /**
     * 分页查询券
     */
    PageResult<CouponListRes> queryCouponList(CouponListReq param);

    /**
     * 查询券详情
     * @param id
     * @return
     */
    CouponDetatilRes queryCouponDetail(Long id);

    /**
     * 分页查询发放明细
     * @param param
     * @return
     */
    PageResult<IssueListRes> queryCouponPossessList(IssueListReq param);

    /**
     * 查询发放明细详情
     * @param id
     * @return
     */
    IssueDetailRes queryConsumeDetail(Long id);

    /**
     * 多张券详情
     * @param ids
     * @return
     */
    List<CouponDetatilRes> queryCouponDetails(List<Long> ids);

    /**
     * 撤回券
     * @param id
     */
    void revokeCoupon(Long id);

    Long createEvent(EventCreateParam param);

    Long saveOrUpdate(CouponDescrTemplate source);

    JSONObject getDescrTemplate(Integer applyScene);

    PageResult<DescrTemplateVO> getTemplatePage();

    boolean descrTemplatExist(Integer applyScene);
}
