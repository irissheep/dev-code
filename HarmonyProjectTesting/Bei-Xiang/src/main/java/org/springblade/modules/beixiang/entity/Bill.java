package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @TableName bx_bill
 */
@TableName(value ="bx_bill")
@Data
public class Bill implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 账单编号
     */
	@ApiModelProperty("账单编号")
    private String billNo;

	/**
	 * 用户行为
	 */
	@ApiModelProperty("用户行为")
	private String action;

    /**
     * 商家id
     */
	@ApiModelProperty("商户id")
    private String merchantId;

    /**
     * 用户id
     */
	@ApiModelProperty("客户id")
    private Long userId;

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
	 *
	 */
	@ApiModelProperty("账单状态")
	private String status;

    /**
     *
     */
    private Integer isDeleted;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Long createUser;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    private Long updateUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
