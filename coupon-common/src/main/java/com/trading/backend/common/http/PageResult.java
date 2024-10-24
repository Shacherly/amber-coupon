package com.trading.backend.common.http;


import com.github.pagehelper.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Getter
@Setter
public class PageResult<T> {

    private Integer page;

    private Integer page_size;

    private Integer total_pages;

    private Long count;

    private List<T> items;

    private static <T> PageResult<T> defaultPage() {
        PageResult<T> result = new PageResult<>();
        result.setPage(1);
        result.setPage_size(10);
        return result;
    }

    public static <T> PageResult<T> oneItemPage(T item) {
        PageResult<T> result = new PageResult<>();
        result.setPage(1);
        result.setPage_size(10);
        result.setItems(Collections.singletonList(item));
        return result;
    }

    public static <T> PageResult<T> empty() {
        PageResult<T> result = defaultPage();
        result.setTotal_pages(0);
        result.setCount(0L);
        result.setItems(new ArrayList<>());
        return result;
    }

    public static <T> PageResult<T> ofPage(Page<T> page) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPage(page.getPageNum());
        pageResult.setPage_size(page.getPageSize());
        pageResult.setCount(page.getTotal());
        pageResult.setItems(page.getResult());
        pageResult.setTotal_pages(page.getPages());
        return pageResult;
    }

    /**
     * 用于VO之间的转换
     * @param another
     * @param <T>
     * @return
     */
    public static <T> PageResult<T> of(PageResult<?> another) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPage(another.getPage());
        pageResult.setPage_size(another.getPage_size());
        pageResult.setCount(another.getCount());
        pageResult.setItems(Collections.emptyList());
        pageResult.setTotal_pages(another.getTotal_pages());
        return pageResult;
    }

    public static <T, E> PageResult<T> fromAnother(PageResult<E> page, Function<E, T> converter) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal_pages(page.getTotal_pages());
        pageResult.setPage(page.getPage());
        pageResult.setPage_size(page.getPage_size());
        pageResult.setCount(page.getCount());
        pageResult.setItems(page.getItems().stream().map(converter).collect(Collectors.toList()));
        return pageResult;
    }

    public static <T, E, F> PageResult<T> fromAnother(
            PageResult<E> page, Predicate<E> predicate, Consumer<E> consumer, Function<E, T> converter) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal_pages(page.getTotal_pages());
        pageResult.setPage(page.getPage());
        pageResult.setPage_size(page.getPage_size());
        pageResult.setCount(page.getCount());
        pageResult.setItems(page.getItems().stream().peek(item -> {if (predicate.test(item)) consumer.accept(item);}).map(converter).collect(Collectors.toList()));
        return pageResult;
    }

    public void generateTotalPages(){
        if (count == 0){
            total_pages = 0;
        }else if (page_size == 0){
            total_pages = Integer.MAX_VALUE;
        }else {
            total_pages = (int)(count / page_size);
            total_pages += (count % page_size != 0 ? 1 : 0);
        }
    }

}
