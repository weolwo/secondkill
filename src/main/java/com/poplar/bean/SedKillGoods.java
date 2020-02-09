package com.poplar.bean;

import lombok.Data;

import java.util.Date;

/**
 * by poplar created on 2020/2/9
 */
@Data
public class SedKillGoods {
    private Long id;
    private Double price;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
