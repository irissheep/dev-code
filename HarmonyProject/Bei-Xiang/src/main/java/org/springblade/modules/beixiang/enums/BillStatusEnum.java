package org.springblade.modules.beixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum BillStatusEnum {
	FINISH("1","已完成"),
	ABNORMAL_FALLBACK("2","异常退回"),
	ABNORMAL_FETCH("3","异常取走"),
	IGNORED("4","已忽略");

	private String code;

	private String name;
}
