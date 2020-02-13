package com.poplar.vo;

import com.poplar.bean.User;
import lombok.Data;

@Data
public class GoodsDetailVo {
	private int status = 0;
	private int remainSeconds = 0;
	private GoodsVo goods ;
	private User user;
}
