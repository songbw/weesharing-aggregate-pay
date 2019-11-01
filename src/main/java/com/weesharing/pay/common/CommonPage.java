package com.weesharing.pay.common;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;

import lombok.Data;


/**
 * 分页数据封装类
 * Created by macro on 2019/4/19.
 */
@Data
public class CommonPage<T> {
    private Long pageNum;
    private Long pageSize;
    private Long totalPage;
    private Long total;
    private List<T> list;

    /**
     * 将SpringData分页后的list转为分页信息
     */
    public static <T> CommonPage<T> restPage(IPage<T> pageInfo) {
        CommonPage<T> result = new CommonPage<T>();
        result.setTotalPage(pageInfo.getPages());
        result.setPageNum(pageInfo.getCurrent());
        result.setPageSize(pageInfo.getSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getRecords());
        return result;
    }
}
