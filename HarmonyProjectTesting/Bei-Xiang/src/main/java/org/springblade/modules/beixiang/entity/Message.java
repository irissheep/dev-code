package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName(value ="bx_message")
public class Message {
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	/**
	 * 消息头
	 */
	@ApiModelProperty("标题")
	private String title;
	/**
	 * 消息
	 */
	@ApiModelProperty("消息")
	private String msg;
	/**
	 *连接需要的用户id
	 */
	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty("消息状态 0 未读 1 已读")
	private Integer status;

	@ApiModelProperty("消息类型 0 补货预警 1 异常取走 2异常退回 3 已完成")
	private String type;

	@ApiModelProperty("订单id")
	private Long billId;
	/**
	 * 时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty("创建时间")
	public String createTime;

	@TableField(exist = false)
	@ApiModelProperty("商品图片")
	private String productUrl;

	@TableField(exist = false)
	@ApiModelProperty("抓拍图片")
	private String captureUrl;

	@ApiModelProperty("ntp_time")
	private String ntpTime;
}
