package org.springblade.modules.beixiang.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class BillVO {
	@ApiModelProperty("订单状态 1 已完成, 2 异常退回, 3 异常取走, 4 已忽略")
	private String status;

	@ApiModelProperty("商品货架")
	private String shelf;

	@ApiModelProperty("账单id")
	private Long id;

	@ApiModelProperty("用户行为")
	private String action;

	/**
	 * 账单编号
	 */
	@ApiModelProperty("账单编号")
	private String billNo;

	/**
	 * 商家id
	 */
	@ApiModelProperty("商家id")
	private String merchantId;

	/**
	 * 用户id
	 */

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("账户名")
	private String account;

	/**
	 * 账单总额
	 */
	@ApiModelProperty("账单总额")
	private BigDecimal totalAmount;

	/**
	 * 商品总数
	 */
	@ApiModelProperty("商品总数")
	private Integer totalNumber;

	/**
	 *创建时间
	 */
	@ApiModelProperty("支付时间")
	private Date createTime;

	/**
	 *商品列表
	 */
	@ApiModelProperty("商品列表")
	private List<ProductResVO>productList;

	@ApiModelProperty("用户图片")
	private String pictureUrl;

	@ApiModelProperty("余额")
	private BigDecimal balance;

	@ApiModelProperty("商品id")
	private Long productId;

}
