package org.springblade.modules.beixiang.service;

import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.ProductRelBill;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.beixiang.vo.NumberCountVO;
import org.springblade.modules.beixiang.vo.SaleTrendVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_product_rel_bill】的数据库操作Service
* @createDate 2024-06-17 17:07:51
*/
public interface ProductRelBillService extends IService<ProductRelBill> {

	List<NumberCountVO> numberCount(NumberCountDTO dto);

	List<SaleTrendVO> saleTrend(String name);
}
