package org.springblade.modules.beixiang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DeviceAddOrUpdateDTO {
	@ApiModelProperty(value = "设备id")
	private String id;

	@ApiModelProperty(value = "设备名称")
	private String name;

	@ApiModelProperty(value = "设备编号")
	private String deviceNo;

	@ApiModelProperty(value = "设备类别")
	private String category;

	@ApiModelProperty(value = "最近上报时间")
	private Date recentReportTime;

	@ApiModelProperty(value = "设备状态 0 离线 1 在线")
	private String status;
}
