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
 * @TableName bx_account_log
 */
@TableName(value ="bx_account_log")
@Data
public class AccountLog implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
	@ApiModelProperty("账户id")
    private Long accountId;

    /**
     * 充值金额
     */
	@ApiModelProperty("充值金额")
    private BigDecimal rechargeAmount;

    /**
     * 创建时间
     */
	@ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 创建用户
     */
    private Long createUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
