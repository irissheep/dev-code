package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleRankVO {

	@ApiModelProperty("商品名称")
	private String name;
	@ApiModelProperty("商品单价")
	private BigDecimal price;
	@ApiModelProperty("商品数量")
	private Integer number;
}
