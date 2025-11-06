package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class NumberCountVO {
	@ApiModelProperty("商品名称")
	private String name;

	@ApiModelProperty("商品数量")
	private String number;
}
