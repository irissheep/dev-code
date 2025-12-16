package org.springblade.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginVO {
	@ApiModelProperty("用户id")
	private Long id;

	@ApiModelProperty("账号")
	private String account;

	@ApiModelProperty("用户类型")
	private String category;

	@ApiModelProperty("token")
	private String token;
}
