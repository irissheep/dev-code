package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PurchaseBehaviorVO {
	@ApiModelProperty("商品名称")
	private String name;

	@ApiModelProperty("成功购买")
	private Integer successCount;

	@ApiModelProperty("取消购买")
	private Integer cancelCount;

}
