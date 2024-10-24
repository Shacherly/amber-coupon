package com.trading.backend.controller;


import com.alibaba.fastjson.JSONObject;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.domain.CouponDescrTemplate;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.aceup.DescrTemplateCreateParam;
import com.trading.backend.http.request.aceup.DescrTemplateUpdateParam;
import com.trading.backend.http.response.desctemplate.DescrTemplateVO;
import com.trading.backend.service.ICouponAdminService;
import com.trading.backend.util.Converter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ~~ trading mu
 * @date 15:14 2022/03/21
 */
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/internal/v1/coupon-template")
@Api(tags = "ACEUP-优惠券描述模板接口")
public class AceUpCouponTemplateController {

    @Autowired
    private ICouponAdminService couponAdminService;


    @ApiOperation(value = "创建优惠券描述模板")
    @PostMapping({"/create"})
    public Response<Long> createTemplate(@RequestBody @Validated DescrTemplateCreateParam param) {
        CouponDescrTemplate source = Converter.fromRequest(param);
        Long id = couponAdminService.saveOrUpdate(source);
        return Response.ok(id);
    }

    @ApiOperation(value = "更新优惠券描述模板")
    @PostMapping({"/update"})
    public Response<Long> updateTemplate(@RequestBody @Validated DescrTemplateUpdateParam param) {
        CouponDescrTemplate source = Converter.fromRequest(param);
        Long id = couponAdminService.saveOrUpdate(source);
        return Response.ok(id);
    }

    @ApiOperation(value = "根据适用场景查询descr模板")
    @GetMapping("/description")
    public Response<JSONObject> getDescr(@Validated @RequestParam Integer apply_scene) {
        JSONObject template = couponAdminService.getDescrTemplate(apply_scene);
        return Response.ok(template);
    }

    @ApiOperation(value = "分页查询所有模板")
    @GetMapping("/page-list")
    public Response<PageResult<DescrTemplateVO>> getTemplatePage(Integer page, Integer page_size) {
        return Response.ok(couponAdminService.getTemplatePage());
    }
}
