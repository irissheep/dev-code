package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceDTO {
	@ApiModelProperty("关键字")
	private String keyword;

	private String category;

	private String name;

	private String deviceNo;
}
