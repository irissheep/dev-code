package org.springblade.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoVO {

	@ApiModelProperty("id" )
	private Long id;

	@ApiModelProperty("账户" )
	private String account;
	/**
	 * 头像
	 */
	@ApiModelProperty("头像" )
	private String avatar;


	@ApiModelProperty("姓名" )
	private String name;

	@ApiModelProperty("是否认证 0 未认证,1 已认证")
	private Integer isCertified;
}
