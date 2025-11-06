package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResVO {

	private Long id;
	/**
	 * 商品名称
	 */
	@ApiModelProperty("商品名称")
	private String name;

	/**
	 * 商品单价
	 */
	@ApiModelProperty("商品单价")
	private BigDecimal price;


	/**
	 * 商品图片
	 */
	@ApiModelProperty("商品图片")
	private String pictureUrl;

	/**
	 * 商品数量
	 */
	@ApiModelProperty("商品数量")
	private Integer number;
}
