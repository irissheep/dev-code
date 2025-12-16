package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductAddOrUpdateDTO {
	@ApiModelProperty(value = "商品id")
    private Long id;

	@ApiModelProperty(value = "商品名称")
	private String name;

	@ApiModelProperty(value = "商品货架Id")
	private Long deviceId;

	@ApiModelProperty(value = "商品单价")
	private BigDecimal price;

	@ApiModelProperty(value = "商品数量")
	private Integer number;

	@ApiModelProperty(value = "商品重量")
	private BigDecimal weight;

	@ApiModelProperty(value = "商品阈值")
	private Integer thresholdValue;

	@ApiModelProperty(value = "商品图片")
	private String pictureUrl;
}
