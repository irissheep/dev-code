package org.springblade.modules.beixiang.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeviceTreeVO {
	private Long id;

	private String name;

	private String cell;

	private List<DeviceTreeVO> childrenTreeVO;
}
