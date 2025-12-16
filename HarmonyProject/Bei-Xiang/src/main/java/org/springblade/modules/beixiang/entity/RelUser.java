package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName(value ="bx_rel_user")
@Data
public class RelUser {
	@TableId(value ="id", type = IdType.ASSIGN_ID)
	private Long id;
	@TableField("user_id")
	private String userId;
	@TableField("ntp_time")
	private String ntpTime;

	@TableField("call_times")
	private Integer callTimes;

	@TableField("picture_url")
	private String pictureUrl;

	@TableField("status")
	private int status;

	@TableField("weight_info")
	private String weightInfo;
}
