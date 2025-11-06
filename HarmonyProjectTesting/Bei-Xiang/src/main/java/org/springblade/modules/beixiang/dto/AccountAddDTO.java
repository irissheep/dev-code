package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountAddDTO {
	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("充值金额")
	private BigDecimal rechargeAmount;


}
