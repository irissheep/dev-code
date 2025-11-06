package org.springblade.modules.beixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DeviceEnum {
	Online("1", "在线"),
	Offline("0", "离线");

	private String code;

	private String name;

	private final static Map<String, DeviceEnum> TYPE_MAP_BY_CODE
		= Arrays.stream(DeviceEnum.values()).collect(Collectors.toMap(DeviceEnum::getCode, t -> t));

	public static DeviceEnum parse(String code) {
		return TYPE_MAP_BY_CODE.get(code);
	}
}
