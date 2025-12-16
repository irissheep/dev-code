package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @TableName bx_product_rel_bill
 */
@TableName(value ="bx_product_rel_bill")
@Data
public class ProductRelBill implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 商品id
     */
	@ApiModelProperty("商品id")
    private Long productId;

    /**
     * 账单id
     */
	@ApiModelProperty("账单id")
    private Long billId;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 创建人
	 */
	private Long createUser;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 更新人
	 */
	private Long updateUser;
	/**
     * 商品数量
     */
    private Integer productNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
