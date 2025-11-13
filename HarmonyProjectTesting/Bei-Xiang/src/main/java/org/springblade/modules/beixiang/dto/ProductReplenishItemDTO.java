package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductReplenishItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "商品ID")
	private Long productId;

	@ApiModelProperty(value = "补货数量")
	private Integer quantity;
}

