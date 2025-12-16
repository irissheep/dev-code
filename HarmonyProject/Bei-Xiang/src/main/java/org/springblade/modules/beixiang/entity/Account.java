package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @TableName bx_account
 */
@TableName(value ="bx_account")
@Data
public class Account implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
	@ApiModelProperty("用户id")
    private Long userId;

    /**
     * 余额
     */
	@ApiModelProperty("余额")
    private BigDecimal balance;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
