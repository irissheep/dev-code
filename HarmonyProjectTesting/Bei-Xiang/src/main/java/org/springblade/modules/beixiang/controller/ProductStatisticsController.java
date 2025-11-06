package org.springblade.modules.beixiang.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liquibase.pro.packaged.S;
import org.springblade.core.tool.api.R;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.service.BillService;
import org.springblade.modules.beixiang.service.ProductRelBillService;
import org.springblade.modules.beixiang.service.ProductService;
import org.springblade.modules.beixiang.vo.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("statistics")
@Api(tags = "统计接口")
public class ProductStatisticsController {

	@Resource
	private ProductService productService;

	@Resource
	private ProductRelBillService relBillService;

	@Resource
	private BillService billService;

	@ApiOperation(value = "商品库存统计", notes = "")
	@GetMapping("inventory")
	public R<List<InventoryVO>> inventory() {
		return R.data(productService.inventory());
	}

	@ApiOperation(value = "商品销售数量统计", notes = "")
	@GetMapping("numberCount")
	public R<List<NumberCountVO>> numberCount(NumberCountDTO dto) {
		return R.data(relBillService.numberCount(dto));
	}

	@ApiOperation(value = "商品销售趋势", notes = "")
	@GetMapping("saleTrend")
	public R<List<SaleTrendVO>> saleTrend(String name) {

		return R.data(relBillService.saleTrend(name));
	}

	@ApiOperation(value = "用户选购行为统计", notes = "")
	@GetMapping("purchaseBehavior")
	public R<List<PurchaseBehaviorVO>> purchaseBehavior(NumberCountDTO dto) {
		return R.data(billService.purchaseBehavior(dto));
	}

	@ApiOperation(value = "销售统计", notes = "")
	@GetMapping("saleCount")
	public R<Map<String, String>> saleCount() {
		return R.data(billService.saleCount());
	}

	@ApiOperation(value = "销量排行", notes = "")
	@GetMapping("SaleRank")
	public R<List<SaleRankVO>> SaleRank(String condition) {
		return R.data(productService.SaleRank(condition));
	}


}
