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
 * @TableName bx_product
 */
@TableName(value ="bx_product")
@Data
public class Product implements Serializable {
    /**
     * 商品ID
     */
    @TableId
    private Long id;

    /**
     * 商品名称
     */
	@ApiModelProperty("商品名称")
    private String name;

    /**
     * 所在货架
     */
	@ApiModelProperty("所在货架id")
    private Long deviceId;

    /**
     * 商品单价
     */
	@ApiModelProperty("商品单价")
    private BigDecimal price;

    /**
     * 补货阈值
     */
	@ApiModelProperty("补货阈值")
    private Integer thresholdValue;

    /**
     * 商品重量
     */
	@ApiModelProperty("商品重量")
    private BigDecimal weight;

    /**
     * 商品图片
     */
	@ApiModelProperty("商品图片")
    private String pictureUrl;

    /**
     * 库存数量
     */
	@ApiModelProperty("库存数量")
    private Integer number;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     *
     */
	@ApiModelProperty("创建时间")
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
