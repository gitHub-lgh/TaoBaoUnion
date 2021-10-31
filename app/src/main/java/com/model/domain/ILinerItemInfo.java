package com.model.domain;

public interface ILinerItemInfo extends IBaseInfo{
    /**
     * 获取原价
     */
    String getFinalPrise();

    /**
     * 获取优惠价格
     */
    long getCouponAmount();

    /**
     * 获取销量
     */
    long getVolume();
}
