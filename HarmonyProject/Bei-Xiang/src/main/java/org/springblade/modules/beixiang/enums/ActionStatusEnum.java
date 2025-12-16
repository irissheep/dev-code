package org.springblade.modules.beixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionStatusEnum {
	FETCH("1","取走"),
	FALLBACK("2","退回");

	private String code;

	private String name;


}
