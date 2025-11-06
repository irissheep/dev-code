package org.springblade.modules.beixiang.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.beixiang.dto.ProductAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.service.ProductService;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("product")
@Api(tags = "商品接口")
public class ProductController {
	@Resource
	private ProductService productService;

	@ApiOperation(value = "新增或修改商品", notes = "")
	@PostMapping("submit")
	public R<Long> submit(@RequestBody ProductAddOrUpdateDTO dto) {
		return R.data(productService.submit(dto));
	}

	@ApiOperation(value = "商品详情", notes = "")
	@GetMapping("detail")
	public R<ProductVO> detail(String id) {
		return R.data(productService.detail(id));
	}

	@ApiOperation(value = "商品分页", notes = "")
	@GetMapping("page")
	public R<IPage<ProductVO>> list(Query query, ProductDTO dto) {
		return R.data(productService.getProductList(query, dto));
	}

	@ApiOperation(value = "商品列表名称", notes = "")
	@GetMapping("listName")
	public R<List<String>> listName() {

		return R.data(productService.listName());
	}

	@ApiOperation(value = "商品补货", notes = "status 1 开始补货 2 完成补货")
	@GetMapping("replenishment")
	public R replenishment(String status) {
		return R.data(productService.replenishment(status));
	}

	@ApiOperation(value = "补货预警", notes = "")
	@GetMapping("replenishWarn")
	public R<List<ProductVO>> replenishWarn() {
		return R.data(productService.replenishWarn());
	}

}
