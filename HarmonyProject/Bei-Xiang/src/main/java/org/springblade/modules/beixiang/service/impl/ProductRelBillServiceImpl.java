package org.springblade.modules.beixiang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.ProductRelBill;
import org.springblade.modules.beixiang.service.ProductRelBillService;
import org.springblade.modules.beixiang.mapper.ProductRelBillMapper;
import org.springblade.modules.beixiang.vo.NumberCountVO;
import org.springblade.modules.beixiang.vo.SaleTrendVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_product_rel_bill】的数据库操作Service实现
* @createDate 2024-06-17 17:07:51
*/
@Service
public class ProductRelBillServiceImpl extends ServiceImpl<ProductRelBillMapper, ProductRelBill>
    implements ProductRelBillService{
	//商品销售数量统计
	@Override
	public List<NumberCountVO> numberCount(NumberCountDTO dto) {
		return baseMapper.numberCount(dto);

	}
	//销量趋势
	@Override
	public List<SaleTrendVO> saleTrend(String name) {
		return baseMapper.saleTrend(name);

	}
}




