package com.trading.backend.util;


import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trading.backend.common.http.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * @author ~~ trading.s
 * @date 16:34 09/29/21
 */
public class PageContext {

    static private final Integer PAGE_NUM = 1;
    static private final Integer PAGE_SIZE = 10;
    static private final String DEFAULT_ORDER = "CTIME DESC";


    public static <E> PageResult<E> selectPage(ISelect select) {
        Page<E> pageItems = PageHelper.startPage(getPageNum(), getPageSize(), DEFAULT_ORDER).doSelectPage(select);
        return PageResult.ofPage(pageItems);
    }

    public static <E> PageResult<E> selectPage(ISelect select, String orderBy) {
        Page<E> pageItems = PageHelper.startPage(getPageNum(), getPageSize(), orderBy).doSelectPage(select);
        return PageResult.ofPage(pageItems);
    }

    public static <E> PageResult<E> selectPage(ISelect select, int page, int pageSize, String orderBy) {
        Page<E> pageItems = PageHelper.startPage(page, pageSize, orderBy).doSelectPage(select);
        return PageResult.ofPage(pageItems);
    }

    public static <E> List<E> selectList(ISelect select, int page, int pageSize, String orderBy) {
        Page<E> pageItems = PageHelper.startPage(page, pageSize, orderBy).doSelectPage(select);
        return pageItems.getResult();
    }

    public static <E> List<E> seletList(ISelect select) {
        PageResult<E> page = selectPage(select);
        return page.getItems();
    }


    public static Integer getPageNum() {
        return Optional.ofNullable(ServletHolder.getParameter("page", Integer::parseInt, 1)).orElse(PAGE_NUM);
    }


    public static Integer getPageSize() {
        return Optional.ofNullable(ServletHolder.getParameter("page_size", Integer::parseInt, 10)).orElse(PAGE_SIZE);
    }

}
