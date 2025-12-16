package org.springblade.modules.beixiang.mapper;

import org.apache.ibatis.annotations.Param;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.ProductRelBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.beixiang.vo.NumberCountVO;
import org.springblade.modules.beixiang.vo.SaleTrendVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_product_rel_bill】的数据库操作Mapper
* @createDate 2024-06-17 17:07:51
* @Entity org.springblade.modules.beixiang.entity.ProductRelBill
*/
public interface ProductRelBillMapper extends BaseMapper<ProductRelBill> {

    List<NumberCountVO> numberCount(@Param("dto") NumberCountDTO dto);

	List<SaleTrendVO> saleTrend(String name);
}




