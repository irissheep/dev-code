package org.springblade.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserListVO {
	@ApiModelProperty("用户id")
	private Long id;
	@ApiModelProperty("账户名")
	private String account;
}
