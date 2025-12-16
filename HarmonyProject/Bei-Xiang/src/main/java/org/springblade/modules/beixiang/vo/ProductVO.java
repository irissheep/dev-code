package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.modules.beixiang.entity.Product;

@Data
public class ProductVO extends Product {
	@ApiModelProperty("商品状态")
	private String status;

	@ApiModelProperty("商品货架")
	private String shelf;

	@ApiModelProperty("格子")
	private String cell;





}
