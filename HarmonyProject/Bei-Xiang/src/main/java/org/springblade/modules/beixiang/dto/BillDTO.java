package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDTO {
	@ApiModelProperty("id")
	private Long id;
	@ApiModelProperty("用户id")
	private Long userId;
	@ApiModelProperty("处理类型 0 忽略 1 处理")
	private String handle;
	@ApiModelProperty("订单编号")
	private String billNo;
	@ApiModelProperty("金额")
	private BigDecimal amount;
	@ApiModelProperty("订单状态 2 异常退回 3 异常取走")
	private String status;
}
