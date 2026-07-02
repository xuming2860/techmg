package com.icbc.sh.techmg.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果 VO — 解决 Gson 无法序列化 MyBatis-Plus IPage 的问题。
 */
public class PageResult<T> {

    @SerializedName("records")
    public List<T> records;

    @SerializedName("total")
    public long total;

    @SerializedName("size")
    public long size;

    @SerializedName("current")
    public long current;

    @SerializedName("pages")
    public long pages;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.records = page.getRecords();
        result.total = page.getTotal();
        result.size = page.getSize();
        result.current = page.getCurrent();
        result.pages = page.getPages();
        return result;
    }

    /** 将 IPage&lt;E&gt; 转换为 PageResult&lt;V&gt;，支持 Entity→VO 映射 */
    public static <E, V> PageResult<V> from(IPage<E> page, Function<E, V> converter) {
        PageResult<V> result = new PageResult<>();
        result.records = page.getRecords().stream().map(converter).collect(Collectors.toList());
        result.total = page.getTotal();
        result.size = page.getSize();
        result.current = page.getCurrent();
        result.pages = page.getPages();
        return result;
    }
}
