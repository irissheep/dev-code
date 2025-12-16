package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryVO {
	@ApiModelProperty("商品货架")
	private String shelf;

	@ApiModelProperty("商品名称")
	private String name;

	@ApiModelProperty("库存数量")
	private String number;
}
