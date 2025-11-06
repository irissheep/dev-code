package org.springblade.modules.beixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum CellEnum {
	FIRST("first","格子1"),
	TWO("second","格子2"),
	THREE("third","格子3"),
	FOUR("fourth","格子4");

	private String code;
	private String name;

	private final static Map<String, CellEnum> TYPE_MAP_BY_CODE
		= Arrays.stream(CellEnum.values()).collect(Collectors.toMap(CellEnum::getCode, t -> t));

	public static CellEnum parse(String code) {
		return TYPE_MAP_BY_CODE.get(code);
	}
}
