package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductReplenishDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "补货状态 1 开始补货 2 完成补货")
	private String status;

	@ApiModelProperty(value = "补货明细")
	private List<ProductReplenishItemDTO> items;

	// 兼容前端另一种参数命名：productList: [{ id, number }]
	@ApiModelProperty(value = "兼容字段：补货明细(productList)")
	private List<SimpleProductItem> productList;

	@Data
	public static class SimpleProductItem implements Serializable {
		private static final long serialVersionUID = 1L;
		@ApiModelProperty(value = "商品ID")
		private Long id;
		@ApiModelProperty(value = "补货数量")
		private Integer number;
	}
}

