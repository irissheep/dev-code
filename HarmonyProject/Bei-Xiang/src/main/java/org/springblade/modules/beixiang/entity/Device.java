package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @TableName bx_device
 */
@TableName(value ="bx_device")
@Data
public class Device implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 设备名称
     */
	@ApiModelProperty("设备名称")
    private String name;

    /**
     * 设备分类
     */
	@ApiModelProperty("设备分类")
    private String category;

    /**
     * 设备编号
     */
	@ApiModelProperty("设备编号")
    private String deviceNo;

    /**
     * 最近上报时间
     */
	@ApiModelProperty("最近上报时间")
    private Date recentReportTime;

    /**
     * 设备状态
     */
	@ApiModelProperty("设备状态 0 离线 1 在线")
    private String status;

	@ApiModelProperty("格子")
	private String cell;

	@ApiModelProperty("")
	private Double totalWeight;

    /**
     * 是否删除
     */
	@TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
