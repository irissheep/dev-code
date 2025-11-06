package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName bx_product_rel_bill_consumption
 */
@TableName(value ="bx_product_rel_bill_consumption")
@Data
public class ProductRelBillConsumption implements Serializable {
    /**
     *
     */
	@TableId(type= IdType.ASSIGN_ID)
    private Long id;

    /**
     *
     */
    private Long productId;

    /**
     *
     */
    private Long billId;



	/**
     * 商品数量
     */
    private Integer productNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
