package org.springblade.modules.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDTO {
	@ApiModelProperty("id")
	private Long id;

	@ApiModelProperty("账户名")
	private String account;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("头像")
	private String avatar;

	@ApiModelProperty("旧密码")
	private String oldPassword;

	@ApiModelProperty("密码")
	private String password;

	@ApiModelProperty("确认密码")
	private String confirmPwd;

	@ApiModelProperty("分类")
	private String category;
}
