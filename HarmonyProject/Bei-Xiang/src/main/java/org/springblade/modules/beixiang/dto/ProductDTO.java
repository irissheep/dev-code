package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductDTO {
	@ApiModelProperty("关键字")
	private String keyword;

	private String name;

	private String shelf;

	private Integer status;


}
