package org.springblade.modules.beixiang.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SaleTrendVO {
	@ApiModelProperty("销售日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String saleDate;

	@ApiModelProperty("商品数量")
	private Integer number;
}





