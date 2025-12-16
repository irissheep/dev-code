package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Weight {

	private String recentReportTime;


	private String deviceId;


	private Double weightFirst;


	private Double weightSecond;


	private Double weightThird;


	private Double weightFourth;

}
